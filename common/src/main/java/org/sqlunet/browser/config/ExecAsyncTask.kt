/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.core.util.Consumer
import androidx.fragment.app.FragmentActivity
import com.bbou.concurrency.Task
import com.bbou.concurrency.observe.TaskDialogObserver
import com.bbou.concurrency.observe.TaskObserver
import org.sqlunet.browser.common.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

/**
 * Execution manager
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class ExecAsyncTask
/**
 * Constructor
 *
 * @param activity    activity
 * @param whenDone    done listener
 * @param observer    observer
 * @param publishRate publish rate
 */(
    /**
     * Activity
     */
    private val activity: Activity,
    /**
     * Done listener
     */
    private val whenDone: Consumer<Boolean>,
    /**
     * Observer
     */
    private val observer: TaskObserver<Pair<Number, Number>>,
    /**
     * Publish rate
     */
    private val publishRate: Int,
) {

    // S Q L

    /**
     * Sql executor
     */
    private class AsyncExecuteFromSql(

        /**
         * Data base path
         */
        private val dataBase: String,

        /**
         * Done listener
         */
        val whenDone: Consumer<Boolean>,

        /**
         * Task observer
         */
        private val observer: TaskObserver<Pair<Number, Number>>,

        /**
         * Publish rate
         */
        private val publishRate: Int,

        ) : Task<Array<String>, Pair<Number, Number>, Boolean>() {

        override fun doJob(params: Array<String>?): Boolean {

            try {
                SQLiteDatabase.openDatabase(dataBase, null, SQLiteDatabase.OPEN_READWRITE).use {

                    // execute
                    val total = params!!.size
                    var successCount = 0
                    for (i in 0 until total) {
                        val sql = params[i].trim { c -> c <= ' ' }
                        if (sql.isEmpty()) {
                            continue
                        }

                        // exec
                        it.execSQL(sql)
                        successCount++
                        Log.d(TAG, "Sql: $sql")

                        // publish
                        if (total % publishRate == 0) {
                            postProgress(Pair<Number, Number>(i, total))
                        }

                        // cooperative exit
                        if (isCancelled()) {
                            Log.d(TAG, "Cancelled!")
                            break
                        }
                        if (Thread.interrupted()) {
                            Log.d(TAG, "Interrupted!")
                            break
                        }
                    }
                    postProgress(Pair<Number, Number>(successCount, total))
                    return true
                }
            } catch (e: Exception) {
                Log.e(TAG, "While executing", e)
            }
            return false
        }

        override fun onPreExecute() {
            observer.taskStart(this)
        }

        override fun onProgress(progress: Pair<Number, Number>) {
            observer.taskProgress(progress)
        }

        override fun onDone(result: Boolean?) {
            observer.taskFinish(result!!)
            whenDone.accept(result)
        }
    }

    /**
     * Execute sql statements
     */
    fun fromSql(dataBase: String): Task<Array<String>, Pair<Number, Number>, Boolean> {
        return AsyncExecuteFromSql(dataBase, whenDone, observer, publishRate)
    }

    // A R C H I V E   F I L E

    private class AsyncExecuteFromArchiveFile(

        /**
         * Data base path
         */
        private val dataBase: String,

        /**
         * Zip entry
         */
        private val entry: String,

        /**
         * Done listener
         */
        val whenDone: Consumer<Boolean>,

        /**
         * Task observer
         */
        private val observer: TaskObserver<Pair<Number, Number>>,

        /**
         * Publish rate
         */
        private val publishRate: Int,

        /**
         * Power manager
         */
        private val powerManager: PowerManager,

        /**
         * Power manager
         */
        private val window: Window,

        ) : Task<String, Pair<Number, Number>, Boolean>() {

        override fun doJob(params: String?): Boolean {

            Log.d(TAG, "$params!$entry -> $dataBase")
            val wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "org.sqlunet.browser:Executor")
            wakelock.acquire(20 * 60 * 1000L /*20 minutes*/)
            var inTransaction = false
            try {
                SQLiteDatabase.openDatabase(dataBase, null, SQLiteDatabase.CREATE_IF_NECESSARY).use { db ->
                    try {
                        ZipFile(params).use { zipFile ->

                            // zip
                            val zipEntry = zipFile.getEntry(entry) ?: throw IOException("Zip entry not found $entry")
                            zipFile.getInputStream(zipEntry).use { `is` ->
                                InputStreamReader(`is`).use { isr ->
                                    BufferedReader(isr).use { reader ->

                                        // journal off
                                        if (SKIP_TRANSACTION) {
                                            db.execSQL("PRAGMA journal_mode = OFF;")
                                        }
                                        // temp store
                                        // db.execSQL("PRAGMA temp_store = FILE;")

                                        // iterate through lines (assuming each insert has its own line and there's no other stuff)
                                        var status = true
                                        var count = 0
                                        var successCount = 0
                                        var sql: String? = null
                                        var line: String
                                        while (reader.readLine().also { line = it } != null) {
                                            if (line.startsWith("-- ")) {
                                                continue
                                            }

                                            // accumulator
                                            if (sql == null) {
                                                sql = line
                                            } else {
                                                sql += '\n' + line
                                            }

                                            // wrap
                                            if (!line.endsWith(";")) {
                                                continue
                                            }

                                            // filter
                                            if (sql == "BEGIN;" || sql == "BEGIN TRANSACTION;") {
                                                if (SKIP_TRANSACTION) {
                                                    continue
                                                }
                                                inTransaction = true
                                            }
                                            if (sql == "COMMIT;" || sql == "COMMIT TRANSACTION;" || sql == "END;" || sql == "END TRANSACTION;") {
                                                if (SKIP_TRANSACTION) {
                                                    continue
                                                }
                                                inTransaction = false
                                            }

                                            // execute
                                            try {
                                                // exec one sql
                                                db.execSQL(sql)
                                                successCount++
                                            } catch (e: SQLiteException) {
                                                Log.e(TAG, "SQL exec failed: " + e.message)
                                                status = false
                                            }

                                            // accounting
                                            count++
                                            sql = null

                                            // progress
                                            val isInteractive = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) powerManager.isInteractive else @Suppress("DEPRECATION") powerManager.isScreenOn
                                            if (isInteractive) {
                                                if (count % publishRate == 0) {
                                                    postProgress(Pair<Number, Number>(count, -1))
                                                }
                                            }

                                            // cooperative exit
                                            if (isCancelled()) {
                                                Log.d(TAG, "Cancelled!")
                                                break
                                            }
                                            if (Thread.interrupted()) {
                                                Log.d(TAG, "Interrupted!")
                                                break
                                            }
                                        }
                                        postProgress(Pair<Number, Number>(successCount, count))
                                        return status
                                    }
                                }
                            }
                        }
                    } catch (e1: IOException) {
                        Log.e(TAG, "While executing SQL from archive", e1)
                        return false
                    } finally {
                        if (db != null) {
                            if (inTransaction) {
                                db.endTransaction()
                            }
                        }
                    }
                }
            } finally {
                // wake lock
                wakelock.release()
            }
        }

        override fun onPreExecute() {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            observer.taskStart(this)
        }

        override fun onProgress(progress: Pair<Number, Number>) {
            observer.taskProgress(progress)
        }

        override fun onDone(result: Boolean?) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            observer.taskFinish(result!!)
            whenDone.accept(result)
        }
    }

    /**
     * Execute sql statements from zip file
     */
    fun fromArchiveFile(dataBase: String, entry: String): Task<String, Pair<Number, Number>, Boolean> {
        val powerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
        val window = activity.window
        return AsyncExecuteFromArchiveFile(dataBase, entry, whenDone, observer, publishRate, powerManager, window)
    }

    // U R I

    private class AsyncExecuteFromUri(

        /**
         * Data base path
         */
        private val dataBase: String,

        /**
         * Content resolver
         */
        val resolver: ContentResolver,

        /**
         * Done listener
         */
        val whenDone: Consumer<Boolean>,

        /**
         * Task observer
         */
        private val observer: TaskObserver<Pair<Number, Number>>,

        /**
         * Publish rate
         */
        private val publishRate: Int,

        /**
         * Power manager
         */
        private val powerManager: PowerManager,

        /**
         * Power manager
         */
        private val window: Window,

        ) : Task<Uri, Pair<Number, Number>, Boolean>() {

        override fun doJob(params: Uri?): Boolean {

            Log.d(TAG, "$params -> $dataBase")
            if (!resolver.getType(params!!)!!.startsWith("text/plain")) {
                Log.e(TAG, "Illegal mime type " + resolver.getType(params))
                return false
            }
            val wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "org.sqlunet.browser:Executor")
            wakelock.acquire(20 * 60 * 1000L /*20 minutes*/)
            var inTransaction = false
            try {
                SQLiteDatabase.openDatabase(dataBase, null, SQLiteDatabase.CREATE_IF_NECESSARY).use { db ->
                    try {
                        resolver.openInputStream(params).use { `is` ->
                            InputStreamReader(`is`).use { isr ->
                                BufferedReader(isr).use { reader ->
                                    // journal off
                                    if (SKIP_TRANSACTION) {
                                        db.execSQL("PRAGMA journal_mode = OFF;")
                                    }
                                    // temp store
                                    // db.execSQL("PRAGMA temp_store = FILE;")

                                    // iterate through lines (assuming each insert has its own line and there's no other stuff)
                                    var status = true
                                    var count = 0
                                    var successCount = 0
                                    var sql: String? = null
                                    var line: String
                                    while (reader.readLine().also { line = it } != null) {
                                        if (line.startsWith("-- ")) {
                                            continue
                                        }

                                        // accumulator
                                        if (sql == null) {
                                            sql = line
                                        } else {
                                            sql += '\n' + line
                                        }

                                        // wrap
                                        if (!line.endsWith(";")) {
                                            continue
                                        }

                                        // filter
                                        if (sql == "BEGIN;" || sql == "BEGIN TRANSACTION;") {
                                            if (SKIP_TRANSACTION) {
                                                continue
                                            }
                                            inTransaction = true
                                        }
                                        if (sql == "COMMIT;" || sql == "COMMIT TRANSACTION;" || sql == "END;" || sql == "END TRANSACTION;") {
                                            if (SKIP_TRANSACTION) {
                                                continue
                                            }
                                            inTransaction = false
                                        }

                                        // execute
                                        try {
                                            // exec one sql
                                            db.execSQL(sql)
                                            successCount++
                                        } catch (e: SQLiteException) {
                                            Log.e(TAG, "SQL update failed: " + e.message)
                                            status = false
                                        }

                                        // accounting
                                        count++
                                        sql = null

                                        // progress
                                        val isInteractive = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) powerManager.isInteractive else @Suppress("DEPRECATION") powerManager.isScreenOn
                                        if (isInteractive) {
                                            if (count % publishRate == 0) {
                                                postProgress(Pair<Number, Number>(count, -1))
                                            }
                                        }

                                        // cooperative exit
                                        if (isCancelled()) {
                                            Log.d(TAG, "Cancelled!")
                                            break
                                        }
                                        if (Thread.interrupted()) {
                                            Log.d(TAG, "Interrupted!")
                                            break
                                        }
                                    }
                                    postProgress(Pair<Number, Number>(successCount, count))
                                    return status
                                }
                            }
                        }
                    } catch (e1: IOException) {
                        Log.e(TAG, "While executing SQL from uri", e1)
                        return false
                    } finally {
                        if (db != null) {
                            if (inTransaction) {
                                db.endTransaction()
                            }
                        }
                    }
                }
            } finally {
                // wake lock
                wakelock.release()
            }
        }

        override fun onPreExecute() {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            observer.taskStart(this)
        }

        override fun onProgress(progress: Pair<Number, Number>) {
            observer.taskProgress(progress)
        }

        override fun onDone(result: Boolean?) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            observer.taskFinish(result!!)
            whenDone.accept(result)
        }
    }

    /**
     * Execute sql statements from zip file
     */
    fun fromUri(dataBase: String, resolver: ContentResolver): Task<Uri, Pair<Number, Number>, Boolean> {
        val powerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
        val window = activity.window
        return AsyncExecuteFromUri(dataBase, resolver, whenDone, observer, publishRate, powerManager, window)
    }

    // A R C H I V E   U R I

    /**
     * Sql executor
     */
    private class AsyncExecuteFromArchiveUri(

        /**
         * Data base path
         */
        private val dataBase: String,

        /**
         * Zip entry
         */
        private val entry: String,

        /**
         * Content resolver
         */
        val resolver: ContentResolver,

        /**
         * Done listener
         */
        val whenDone: Consumer<Boolean>,

        /**
         * Task observer
         */
        private val observer: TaskObserver<Pair<Number, Number>>,

        /**
         * Publish rate
         */
        private val publishRate: Int,

        /**
         * Power manager
         */
        private val powerManager: PowerManager,

        /**
         * Power manager
         */
        private val window: Window,

        ) : Task<Uri, Pair<Number, Number>, Boolean>() {

        override fun doJob(params: Uri?): Boolean {

            Log.d(TAG, "$params!$entry -> $dataBase")
            if (!resolver.getType(params!!)!!.startsWith("application/zip")) {
                Log.e(TAG, "Illegal mime type " + resolver.getType(params))
                return false
            }

            val wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "org.sqlunet.browser:Executor")
            wakelock.acquire(20 * 60 * 1000L /*20 minutes*/)
            var inTransaction = false
            try {
                SQLiteDatabase.openDatabase(dataBase, null, SQLiteDatabase.CREATE_IF_NECESSARY).use { db ->
                    try {
                        resolver.openInputStream(params).use { `is` ->
                            ZipInputStream(`is`).use { zis ->
                                InputStreamReader(zis).use { isr ->
                                    BufferedReader(isr).use { reader ->
                                        // temp store
                                        // db.execSQL("PRAGMA temp_store = FILE;")

                                        // zip
                                        var zipEntry: ZipEntry
                                        while (zis.nextEntry.also { zipEntry = it } != null) {
                                            if (zipEntry.name != entry) {
                                                continue
                                            }

                                            // journal off
                                            if (SKIP_TRANSACTION) {
                                                db.execSQL("PRAGMA journal_mode = OFF;")
                                            }

                                            // iterate through lines (assuming each insert has its own line and there's no other stuff)
                                            var status = true
                                            var count = 0
                                            var successCount = 0
                                            var sql: String? = null
                                            var line: String
                                            while (reader.readLine().also { line = it } != null) {
                                                if (line.startsWith("-- ")) {
                                                    continue
                                                }

                                                // accumulator
                                                if (sql == null) {
                                                    sql = line
                                                } else {
                                                    sql += '\n' + line
                                                }

                                                // wrap
                                                if (!line.endsWith(";")) {
                                                    continue
                                                }

                                                // filter
                                                if (sql == "BEGIN;" || sql == "BEGIN TRANSACTION;") {
                                                    if (SKIP_TRANSACTION) {
                                                        continue
                                                    }
                                                    inTransaction = true
                                                }
                                                if (sql == "COMMIT;" || sql == "COMMIT TRANSACTION;" || sql == "END;" || sql == "END TRANSACTION;") {
                                                    if (SKIP_TRANSACTION) {
                                                        continue
                                                    }
                                                    inTransaction = false
                                                }

                                                // execute
                                                try {
                                                    // exec one sql
                                                    db.execSQL(sql)
                                                    successCount++
                                                } catch (e: SQLiteException) {
                                                    Log.e(TAG, "SQL update failed: " + e.message)
                                                    status = false
                                                }

                                                // accounting
                                                count++
                                                sql = null

                                                // progress
                                                val isInteractive = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) powerManager.isInteractive else @Suppress("DEPRECATION") powerManager.isScreenOn
                                                if (isInteractive) {
                                                    if (count % publishRate == 0) {
                                                        postProgress(Pair<Number, Number>(count, -1))
                                                    }
                                                }

                                                // cooperative exit
                                                if (isCancelled()) {
                                                    Log.d(TAG, "Cancelled!")
                                                    break
                                                }
                                                if (Thread.interrupted()) {
                                                    Log.d(TAG, "Interrupted!")
                                                    break
                                                }
                                            }
                                            postProgress(Pair<Number, Number>(successCount, count))
                                            return status
                                        }
                                        // found none
                                        return false
                                    }
                                }
                            }
                        }
                    } catch (e1: IOException) {
                        Log.e(TAG, "While executing from archive uri", e1)
                        return false
                    } finally {
                        if (db != null) {
                            if (inTransaction) {
                                db.endTransaction()
                            }
                        }
                    }
                }
            } finally {
                // wake lock
                wakelock.release()
            }
        }

        override fun onPreExecute() {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            observer.taskStart(this)
        }

        override fun onProgress(progress: Pair<Number, Number>) {
            observer.taskProgress(progress)
        }

        override fun onDone(result: Boolean?) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            observer.taskFinish(result!!)
            whenDone.accept(result)
        }
    }

    /**
     * Execute sql statements from zip uri
     *
     * @param dataBase database
     * @param entry    zip entry
     * @param resolver content resolver
     */
    fun fromArchiveUri(dataBase: String, entry: String, resolver: ContentResolver): Task<Uri, Pair<Number, Number>, Boolean> {
        val powerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
        val window = activity.window
        return AsyncExecuteFromArchiveUri(dataBase, entry, resolver, whenDone, observer, publishRate, powerManager, window)
    }

    // V A C U U M

    private class AsyncVacuum(

        /**
         * Done listener
         */
        val whenDone: Consumer<Boolean>,
        /**
         * Task observer
         */
        private val observer: TaskObserver<Pair<Number, Number>>,

        ) : Task<Pair<String, String>, Void, Boolean>() {

        @Suppress("SameReturnValue")
        override fun doJob(params: Pair<String, String>?): Boolean {

            val databasePathArg = params!!.first
            val tempDirArg = params.second

            // database
            val db = SQLiteDatabase.openDatabase(databasePathArg, null, SQLiteDatabase.OPEN_READWRITE)

            // set parameters
            // db.execSQL("PRAGMA journal_mode = PERSIST;")
            db.execSQL("PRAGMA temp_store = FILE;")
            db.execSQL("PRAGMA temp_store_directory = '$tempDirArg';")
            Log.d(TAG, "vacuuming in $tempDirArg")
            db.execSQL("VACUUM")
            Log.d(TAG, "vacuumed in $tempDirArg")
            db.close()
            return true
        }

        override fun onDone(result: Boolean?) {
            observer.taskFinish(true)
            whenDone.accept(result == true)
        }
    }

    /**
     * Vacuum database
     *
     * @param database database path
     * @param tempDir  temp directory
     */
    fun vacuum(database: String, tempDir: String) {
        val task: Task<Pair<String, String>, Void, Boolean> = AsyncVacuum(whenDone, observer)
        task.execute(Pair(database, tempDir))
    }

    companion object {

        private const val TAG = "ExecAsyncTask"

        const val EXEC_LOG = "sqlunet_exe.log"

        /**
         * Execute transaction
         */
        private const val SKIP_TRANSACTION = false

        /**
         * Launch uri
         *
         * @param activity     activity
         * @param uri          source zip uri
         * @param databasePath database path
         * @param whenDone     to run when done
         */
        fun launchExecUri(activity: FragmentActivity, uri: Uri, databasePath: String, whenDone: Consumer<Boolean>) {
            val observer: TaskObserver<Pair<Number, Number>> = TaskDialogObserver<Pair<Number, Number>>(activity.supportFragmentManager)
                .setTitle(activity.getString(R.string.action_exec_from_uri))
                .setMessage(uri.toString())
            launchExecUri(activity, observer, uri, databasePath, whenDone)
        }

        /**
         * Launch exec of archive uri
         *
         * @param activity     activity
         * @param uri          source uri
         * @param databasePath database path
         * @param whenDone     to run when done
         */
        fun launchExecZippedUri(activity: FragmentActivity, uri: Uri, entry: String, databasePath: String, whenDone: Consumer<Boolean>) {
            val observer: TaskObserver<Pair<Number, Number>> = TaskDialogObserver<Pair<Number, Number>>(activity.supportFragmentManager)
                .setTitle(activity.getString(R.string.action_exec_from_uri))
                .setMessage(uri.toString())
            launchExecZippedUri(activity, observer, uri, entry, databasePath, whenDone)
        }

        /**
         * Launch uri
         *
         * @param activity activity
         * @param observer observer
         * @param uri      source zip uri
         * @param dest     database path
         * @param whenDone to run when done
         */
        private fun launchExecUri(activity: Activity, observer: TaskObserver<Pair<Number, Number>>, uri: Uri, dest: String, whenDone: Consumer<Boolean>) {
            val whenDone2 = Consumer { result: Boolean -> whenDone.accept(result) }
            ExecAsyncTask(activity, whenDone2, observer, 1000)
                .fromUri(dest, activity.contentResolver)
                .execute(uri)
            observer.taskUpdate(activity.getString(R.string.status_executing))
        }

        /**
         * Launch exec of entry in archive uri
         *
         * @param activity  activity
         * @param observer  observer
         * @param sourceUri source zip uri
         * @param zipEntry  zip entry
         * @param dest      database path
         * @param whenDone  to run when done
         */
        private fun launchExecZippedUri(activity: Activity, observer: TaskObserver<Pair<Number, Number>>, sourceUri: Uri, zipEntry: String, dest: String, whenDone: Consumer<Boolean>) {
            val whenDone2 = Consumer { result: Boolean -> whenDone.accept(result) }
            ExecAsyncTask(activity, whenDone2, observer, 1000)
                .fromArchiveUri(dest, zipEntry, activity.contentResolver)
                .execute(sourceUri)
            observer.taskUpdate(activity.getString(R.string.status_executing) + ' ' + zipEntry)
        }
    }
}
