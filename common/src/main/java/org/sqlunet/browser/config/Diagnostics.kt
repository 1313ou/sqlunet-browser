/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteCantOpenDatabaseException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import com.bbou.concurrency.Task
import com.bbou.deploy.workers.Deploy.computeDigest
import com.bbou.download.preference.Settings
import com.bbou.download.preference.Settings.Mode
import org.sqlunet.assetpack.AssetPackLoader
import org.sqlunet.browser.common.R
import org.sqlunet.browser.config.Status
import org.sqlunet.provider.XNetContract
import org.sqlunet.provider.XSqlUNetProvider.Companion.makeUri
import org.sqlunet.settings.StorageSettings
import org.sqlunet.settings.StorageUtils
import org.sqlunet.settings.StorageUtils.mbToString
import org.sqlunet.settings.StorageUtils.storageStats
import java.io.File
import java.util.Date
import java.util.function.Consumer

object Diagnostics {

    @SuppressLint("DiscouragedApi")
    private fun report(context: Context): CharSequence {
        val sb = SpannableStringBuilder()
        append(sb, "DIAGNOSTICS", StyleSpan(Typeface.BOLD))
        sb.append('\n')

        // APP
        sb.append('\n')
        append(sb, "app", StyleSpan(Typeface.BOLD))
        sb.append('\n')
        val packageName = context.applicationInfo.packageName
        sb.append(packageName)
        sb.append('\n')
        val pInfo: PackageInfo
        try {
            pInfo = context.packageManager.getPackageInfo(packageName, 0)
            val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pInfo.longVersionCode else pInfo.versionCode.toLong()
            sb.append("version: ")
            sb.append(code.toString())
            sb.append('\n')
        } catch (e: PackageManager.NameNotFoundException) {
            sb.append("package info: ")
            sb.append(e.message)
            sb.append('\n')
        }
        sb.append("api: ")
        sb.append(Build.VERSION.SDK_INT.toString())
        sb.append(' ')
        sb.append(Build.VERSION.CODENAME)
        sb.append('\n')

        // DATABASE
        val database = StorageSettings.getDatabasePath(context)
        sb.append('\n')
        append(sb, "database", StyleSpan(Typeface.BOLD))
        sb.append('\n')
        sb.append("path: ")
        sb.append(database)
        sb.append('\n')
        if (database.isNotEmpty()) {
            val databaseFile = File(database)
            val databaseExists = databaseFile.exists()
            sb.append("exists: ")
            sb.append(databaseExists.toString())
            sb.append('\n')
            val parent = databaseFile.getParent()!!
            val dataStats = storageStats(parent)
            val df = dataStats[StorageUtils.STORAGE_FREE]
            val dc = dataStats[StorageUtils.STORAGE_CAPACITY]
            val dp = dataStats[StorageUtils.STORAGE_OCCUPANCY]
            sb.append("free: ")
            sb.append(mbToString(df))
            sb.append('\n')
            sb.append("capacity: ")
            sb.append(mbToString(dc))
            sb.append('\n')
            sb.append("occupancy: ")
            sb.append(dp.toString())
            sb.append('%')
            sb.append('\n')
            if (databaseExists) {
                val databaseIsFile = databaseFile.isFile()
                val databaseLastModified = databaseFile.lastModified()
                val databaseSize = databaseFile.length()
                val databaseCanRead = databaseFile.canRead()
                sb.append("is file: ")
                sb.append(databaseIsFile.toString())
                sb.append('\n')
                sb.append("size: ")
                sb.append(databaseSize.toString())
                sb.append('\n')
                sb.append("last modified: ")
                sb.append(if (databaseLastModified == -1L || databaseLastModified == 0L) "n/a" else Date(databaseLastModified).toString())
                sb.append('\n')
                sb.append("can read: ")
                sb.append(databaseCanRead.toString())
                sb.append('\n')
                val md5 = computeDigest(database)
                sb.append("md5: ")
                sb.append(md5 ?: "null")
                sb.append('\n')
                sb.append("can open: ")
                var databaseCanOpen = false
                try {
                    databaseCanOpen = canOpen(database)
                    sb.append(databaseCanOpen.toString())
                    sb.append('\n')
                    val status = Status.status(context)
                    val existsDb = status and Status.EXISTS != 0
                    val existsTables = status and Status.EXISTS_TABLES != 0
                    if (existsDb) {
                        // TABLES
                        sb.append('\n')
                        append(sb, "tables", StyleSpan(Typeface.BOLD))
                        sb.append('\n')
                        sb.append("tables exist: ")
                        sb.append(existsTables.toString())
                        sb.append('\n')
                        val res = context.resources
                        val requiredTables = res.getStringArray(R.array.required_tables)
                        val requiredIndexes = res.getStringArray(R.array.required_indexes)
                        val requiredPmTablesResId = res.getIdentifier("required_pm", "array", packageName)
                        val requiredTSWnResId = res.getIdentifier("required_texts_wn", "array", packageName)
                        val requiredTSVnResId = res.getIdentifier("required_texts_vn", "array", packageName)
                        val requiredTSPbResId = res.getIdentifier("required_texts_pb", "array", packageName)
                        val requiredTSFnResId = res.getIdentifier("required_texts_fn", "array", packageName)
                        val requiredPmTables = if (requiredPmTablesResId == 0) null else res.getStringArray(requiredPmTablesResId)
                        val requiredTSWn = if (requiredTSWnResId == 0) null else res.getStringArray(requiredTSWnResId)
                        val requiredTSVn = if (requiredTSVnResId == 0) null else res.getStringArray(requiredTSVnResId)
                        val requiredTSPb = if (requiredTSPbResId == 0) null else res.getStringArray(requiredTSPbResId)
                        val requiredTSFn = if (requiredTSFnResId == 0) null else res.getStringArray(requiredTSFnResId)
                        try {
                            val existingTablesAndIndexes = Status.tablesAndIndexes(context)
                            if (existingTablesAndIndexes != null) {
                                for (table in requiredTables) {
                                    sb.append("table ")
                                    sb.append(table)
                                    sb.append(" exists: ")
                                    val exists = existingTablesAndIndexes.contains(table)
                                    sb.append(exists.toString())
                                    if (exists) {
                                        sb.append(" rows: ")
                                        sb.append(rowCount(database, table).toString())
                                    }
                                    sb.append('\n')
                                }
                                sb.append('\n')
                                for (index in requiredIndexes) {
                                    sb.append("index ")
                                    sb.append(index)
                                    sb.append(": ")
                                    sb.append(existingTablesAndIndexes.contains(index).toString())
                                    sb.append('\n')
                                }
                                if (requiredPmTables != null) {
                                    sb.append('\n')
                                    for (table in requiredPmTables) {
                                        sb.append("pm table ")
                                        sb.append(table)
                                        sb.append(" exists: ")
                                        val exists = existingTablesAndIndexes.contains(table)
                                        sb.append(exists.toString())
                                        if (exists) {
                                            sb.append(" rows: ")
                                            sb.append(rowCount(database, table).toString())
                                        }
                                        sb.append('\n')
                                    }
                                }
                                if (requiredTSWn != null) {
                                    sb.append('\n')
                                    for (table in requiredTSWn) {
                                        sb.append("wn table ")
                                        sb.append(table)
                                        sb.append(" exists: ")
                                        val exists = existingTablesAndIndexes.contains(table)
                                        sb.append(exists.toString())
                                        if (exists) {
                                            sb.append(" rows: ")
                                            sb.append(rowCount(database, table).toString())
                                        }
                                        sb.append('\n')
                                    }
                                }
                                if (requiredTSVn != null) {
                                    sb.append('\n')
                                    for (table in requiredTSVn) {
                                        sb.append("vn table ")
                                        sb.append(table)
                                        sb.append(" exists: ")
                                        val exists = existingTablesAndIndexes.contains(table)
                                        sb.append(exists.toString())
                                        if (exists) {
                                            sb.append(" rows: ")
                                            sb.append(rowCount(database, table).toString())
                                        }
                                        sb.append('\n')
                                    }
                                }
                                if (requiredTSPb != null) {
                                    sb.append('\n')
                                    for (table in requiredTSPb) {
                                        sb.append("pb table ")
                                        sb.append(table)
                                        sb.append(" exists: ")
                                        val exists = existingTablesAndIndexes.contains(table)
                                        sb.append(exists.toString())
                                        if (exists) {
                                            sb.append(" rows: ")
                                            sb.append(rowCount(database, table).toString())
                                        }
                                        sb.append('\n')
                                    }
                                }
                                if (requiredTSFn != null) {
                                    sb.append('\n')
                                    for (table in requiredTSFn) {
                                        sb.append("fn table ")
                                        sb.append(table)
                                        sb.append(" exists: ")
                                        val exists = existingTablesAndIndexes.contains(table)
                                        sb.append(exists.toString())
                                        if (exists) {
                                            sb.append(" rows: ")
                                            sb.append(rowCount(database, table).toString())
                                        }
                                        sb.append('\n')
                                    }
                                }
                            } else {
                                sb.append("null existing tables or indexes")
                                sb.append('\n')
                            }
                        } catch (e: Exception) {
                            sb.append("cannot read tables or indexes: ")
                            sb.append(e.message)
                            sb.append('\n')
                        }
                    }

                    // M E T A

                    val meta = queryMeta(context)
                    if (meta != null) {
                        sb.append('\n')
                        append(sb, "meta", StyleSpan(Typeface.BOLD))
                        sb.append('\n')
                        sb.append("created: ")
                        sb.append(meta[0])
                        sb.append('\n')
                        sb.append("size: ")
                        sb.append(meta[1])
                        sb.append('\n')
                        sb.append("build: ")
                        sb.append(meta[2])
                        sb.append('\n')
                    }
                } catch (e: SQLiteCantOpenDatabaseException) {
                    sb.append(databaseCanOpen.toString())
                    sb.append('\n')
                    sb.append(e.message)
                    sb.append('\n')
                }
            }
        }

        // RECORDED SOURCE

        val source = Settings.getDatapackSource(context)
        val sourceSize = Settings.getDatapackSourceSize(context)
        val sourceStamp = Settings.getDatapackSourceDate(context)
        val sourceEtag = Settings.getDatapackSourceEtag(context)
        val sourceVersion = Settings.getDatapackSourceVersion(context)
        val sourceStaticVersion = Settings.getDatapackSourceStaticVersion(context)
        val sourceType = Settings.getDatapackSourceType(context)
        val name = Settings.getDatapackName(context)
        val size = Settings.getDatapackSize(context)
        val stamp = Settings.getDatapackDate(context)
        sb.append('\n')
        append(sb, "source", StyleSpan(Typeface.BOLD))
        sb.append('\n')
        sb.append("recorded source: ")
        sb.append(source ?: "null")
        sb.append('\n')
        sb.append("recorded source type: ")
        sb.append(sourceType ?: "null")
        sb.append('\n')
        sb.append("recorded source size: ")
        sb.append(if (sourceSize == -1L) "null" else sourceSize.toString())
        sb.append('\n')
        sb.append("recorded source date: ")
        sb.append(if (sourceStamp == -1L || sourceStamp == 0L) "null" else Date(sourceStamp).toString())
        sb.append('\n')
        sb.append("recorded source etag: ")
        sb.append(sourceEtag ?: "null")
        sb.append('\n')
        sb.append("recorded source version: ")
        sb.append(sourceVersion ?: "null")
        sb.append('\n')
        sb.append("recorded source static version: ")
        sb.append(sourceStaticVersion ?: "null")
        sb.append('\n')
        sb.append("recorded name: ")
        sb.append(name ?: "null")
        sb.append('\n')
        sb.append("recorded size: ")
        sb.append(if (size == -1L) "null" else size.toString())
        sb.append('\n')
        sb.append("recorded date: ")
        sb.append(if (stamp == -1L || stamp == 0L) "null" else Date(stamp).toString())
        sb.append('\n')

        // ASSET PACKS
        val assetPack = context.getString(R.string.asset_primary)
        val assetZip = context.getString(R.string.asset_zip_primary)
        val assetDir = context.getString(R.string.asset_dir_primary)
        sb.append('\n')
        append(sb, "assets", StyleSpan(Typeface.BOLD))
        sb.append('\n')
        sb.append("primary asset pack: ")
        sb.append(assetPack)
        sb.append('\n')
        sb.append("primary asset archive: ")
        sb.append(assetDir)
        sb.append('/')
        sb.append(assetZip)
        sb.append('\n')
        val assetLocation = AssetPackLoader(context, assetPack).assetPackPathIfInstalled()
        sb.append("primary asset ")
        sb.append(assetPack)
        if (assetLocation != null) {
            sb.append(" installed at: ")
            sb.append(assetLocation)
        } else {
            sb.append(" not installed")
        }
        sb.append('\n')
        val altAssetPack = context.getString(R.string.asset_alt)
        val altAssetDir = context.getString(R.string.asset_dir_alt)
        val altAssetZip = context.getString(R.string.asset_zip_alt)
        sb.append("alt asset pack: ")
        if (altAssetPack.isNotEmpty()) {
            sb.append(altAssetPack)
            sb.append('\n')
            sb.append("alt asset archive: ")
            sb.append(altAssetDir)
            sb.append('/')
            sb.append(altAssetZip)
            sb.append('\n')
        }
        val altAssetLocation = AssetPackLoader(context, altAssetPack).assetPackPathIfInstalled()
        sb.append("alt asset ")
        sb.append(altAssetPack)
        if (altAssetLocation != null) {
            sb.append(" installed at: ")
            sb.append(altAssetLocation)
        } else {
            sb.append(" not installed")
        }
        sb.append('\n')

        // DOWNLOAD
        val mode = Mode.getModePref(context)
        val dbDownloadSource = StorageSettings.getDbDownloadSourcePath(context, mode == Settings.Mode.DOWNLOAD_ZIP_THEN_UNZIP || mode == Settings.Mode.DOWNLOAD_ZIP)
        val dbDownloadTarget = StorageSettings.getDatabasePath(context)
        sb.append('\n')
        append(sb, "download", StyleSpan(Typeface.BOLD))
        sb.append('\n')
        sb.append("download source: ")
        sb.append(dbDownloadSource)
        sb.append('\n')
        sb.append("download target: ")
        sb.append(dbDownloadTarget)
        sb.append('\n')
        return sb
    }

    private fun queryMeta(context: Context): Array<String?>? {
        val uri = Uri.parse(makeUri(XNetContract.Meta.URI))
        val projection = arrayOf(XNetContract.Meta.CREATED, XNetContract.Meta.DBSIZE, XNetContract.Meta.BUILD)
        try {
            context.contentResolver.query(uri, projection, null, null, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val meta = arrayOfNulls<String>(3)
                    val createdIndex = cursor.getColumnIndex(XNetContract.Meta.CREATED)
                    if (!cursor.isNull(createdIndex)) {
                        val created = cursor.getString(createdIndex)
                        meta[0] = created
                    }
                    val sizeIndex = cursor.getColumnIndex(XNetContract.Meta.DBSIZE)
                    if (!cursor.isNull(sizeIndex)) {
                        val size = cursor.getLong(sizeIndex)
                        meta[1] = size.toString()
                    }
                    val buildIndex = cursor.getColumnIndex(XNetContract.Meta.BUILD)
                    if (!cursor.isNull(buildIndex)) {
                        val build = cursor.getString(buildIndex)
                        meta[2] = build
                    }
                    return meta
                }
            }
        } catch (ignored: Exception) {

        }
        return null
    }

    @Throws(SQLiteCantOpenDatabaseException::class)
    private fun canOpen(path: String): Boolean {
        SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY).use { return true }
    }

    @Throws(SQLiteException::class)
    private fun rowCount(path: String, table: String): Long {
        SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY).use { db -> return DatabaseUtils.queryNumEntries(db, table) }
    }

    /**
     * Append text
     *
     * @param sb    spannable string builder
     * @param text  text
     * @param spans spans to apply
     */
    private fun append(sb: SpannableStringBuilder, text: CharSequence?, vararg spans: Any) {
        if (text.isNullOrEmpty()) {
            return
        }
        val from = sb.length
        sb.append(text)
        val to = sb.length
        for (span in spans) {
            sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    /**
     * Async diagnostics
     *
     * @param consumer result listener
     */
    class AsyncDiagnostics internal constructor(private val consumer: Consumer<CharSequence?>) : Task<Context, Long, CharSequence>() {

        override fun doJob(params: Context?): CharSequence {
            return report(params!!)
        }

        override fun onDone(result: CharSequence?) {
            consumer.accept(result)
        }
    }
}
