/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.deploy.workers

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.DialogInterface
import android.net.Uri
import androidx.core.util.Consumer
import androidx.fragment.app.FragmentActivity
import com.bbou.concurrency.Task
import com.bbou.concurrency.observe.TaskDialogObserver
import com.bbou.concurrency.observe.TaskObserver
import com.bbou.deploy.workers.DeployOps.copyFromFile
import com.bbou.deploy.workers.DeployOps.copyFromUri
import com.bbou.deploy.workers.DeployOps.copyFromUrl
import com.bbou.deploy.workers.DeployOps.md5FromFile
import com.bbou.deploy.workers.DeployOps.md5FromUri
import com.bbou.deploy.workers.DeployOps.md5FromUrl
import com.bbou.deploy.workers.DeployOps.unzipEntryFromArchiveFile
import com.bbou.deploy.workers.DeployOps.unzipEntryFromArchiveUri
import com.bbou.deploy.workers.DeployOps.unzipEntryFromArchiveUrl
import com.bbou.deploy.workers.DeployOps.unzipFromArchiveFile
import com.bbou.deploy.workers.DeployOps.unzipFromArchiveUri
import com.bbou.deploy.workers.DeployOps.unzipFromArchiveUrl
import java.net.URL

/**
 * File async tasks
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FileTasks(
    private val observer: TaskObserver<Pair<Number, Number>>,
    private val consumer: Consumer<*>?,
    private val publishRate: Int,
) {

    // CORE

    // copy

    /**
     * Copy from file task
     *
     * @property dest destination
     * @property observer progress observer
     * @property consumer result consumer
     * @property publishRate progress publish rate
     */
    class AsyncCopyFromFile(
        private val dest: String,
        private val observer: TaskObserver<Pair<Number, Number>>,
        private val consumer: Consumer<Boolean>?,
        private val publishRate: Int,
    ) : Task<String, Pair<Number, Number>, Boolean>(), DeployOps.Publisher {

        override fun doJob(params: String?): Boolean {
            val srcFileArg = params!!
            return copyFromFile(srcFileArg, dest, this, this, publishRate)
        }

        override fun onProgress(progress: Pair<Number, Number>) {
            super.onProgress(progress)
            observer.taskProgress(progress)
        }

        override fun onDone(result: Boolean?) {
            observer.taskFinish(result == true)
            consumer?.accept(result?:false)
        }

        override fun onCancelled() {
            observer.taskFinish(false)
        }

        override fun publish(current: Long, total: Long) {
            postProgress(current to total)
        }
    }

    /**
     * Copy from Uri task
     *
     * @property dest destination
     * @property resolver content resolver
     * @property observer progress observer
     * @property consumer result consumer
     * @property publishRate progress publish rate
     */
    class AsyncCopyFromUri(
        private val dest: String,
        private val resolver: ContentResolver,
        private val observer: TaskObserver<Pair<Number, Number>>,
        private val consumer: Consumer<Boolean>?,
        private val publishRate: Int,
    ) : Task<Uri, Pair<Number, Number>, Boolean>(), DeployOps.Publisher {

        override fun doJob(params: Uri?): Boolean {
            val srcUriArg = params!!
            return copyFromUri(srcUriArg, resolver, dest, this, this, publishRate)
        }

        override fun onPreExecute() {
            super.onPreExecute()
            observer.taskStart(this)
        }

        override fun onProgress(progress: Pair<Number, Number>) {
            super.onProgress(progress)
            observer.taskProgress(progress)
        }

        override fun onDone(result: Boolean?) {
            observer.taskFinish(result == true)
            consumer?.accept(result?:false)
        }

        override fun onCancelled() {
            observer.taskFinish(false)
        }

        override fun publish(current: Long, total: Long) {
            postProgress(current to total)
        }
    }

    /**
     * Copy from URL task
     *
     * @property dest destination
     * @property observer progress observer
     * @property consumer result consumer
     * @property publishRate progress publish rate
     */
    class AsyncCopyFromUrl(
        private val dest: String,
        private val observer: TaskObserver<Pair<Number, Number>>,
        private val consumer: Consumer<Boolean>?,
        private val publishRate: Int,
    ) : Task<URL, Pair<Number, Number>, Boolean>(), DeployOps.Publisher {

        override fun doJob(params: URL?): Boolean {
            val srcUriArg = params!!
            return copyFromUrl(srcUriArg, dest, this, this, publishRate)
        }

        override fun onPreExecute() {
            super.onPreExecute()
            observer.taskStart(this)
        }

        override fun onProgress(progress: Pair<Number, Number>) {
            super.onProgress(progress)
            observer.taskProgress(progress)
        }

        override fun onDone(result: Boolean?) {
            observer.taskFinish(result == true)
            consumer?.accept(result?:false)
        }

        override fun onCancelled() {
            observer.taskFinish(false)
        }

        override fun publish(current: Long, total: Long) {
            postProgress(current to total)
        }
    }

    // unzip

    /**
     * Unzip entries from archive file task
     *
     * @property dest destination
     * @property observer progress observer
     * @property consumer result consumer
     * @property publishRate progress publish rate
     */
    class AsyncUnzipFromArchiveFile(
        private val dest: String,
        private val observer: TaskObserver<Pair<Number, Number>>,
        private val consumer: Consumer<Boolean>?,
        private val publishRate: Int,
    ) : Task<String, Pair<Number, Number>, Boolean>(), DeployOps.Publisher {

        override fun doJob(params: String?): Boolean {
            val srcArchiveArg = params!!
            return unzipFromArchiveFile(srcArchiveArg, dest, this, this, publishRate)
        }

        override fun onPreExecute() {
            observer.taskStart(this)
        }

        override fun onProgress(progress: Pair<Number, Number>) {
            observer.taskProgress(progress)
        }

        override fun onDone(result: Boolean?) {
            observer.taskFinish(result == true)
            consumer?.accept(result?:false)
        }

        override fun onCancelled() {
            observer.taskFinish(false)
        }

        override fun publish(current: Long, total: Long) {
            postProgress(current to total)
        }
    }

    /**
     * Unzip entries from archive Uri task
     *
     * @property dest destination
     * @property resolver content resolver
     * @property observer progress observer
     * @property consumer result consumer
     * @property publishRate progress publish rate
     */
    class AsyncUnzipFromArchiveUri(
        private val dest: String,
        private val resolver: ContentResolver,
        private val observer: TaskObserver<Pair<Number, Number>>,
        private val consumer: Consumer<Boolean>?,
        private val publishRate: Int,
    ) : Task<Uri, Pair<Number, Number>, Boolean>(), DeployOps.Publisher {

        override fun doJob(params: Uri?): Boolean {
            val srcArchiveArg = params!!
            return unzipFromArchiveUri(srcArchiveArg, resolver, dest, this, this, publishRate)
        }

        override fun onPreExecute() {
            observer.taskStart(this)
        }

        override fun onProgress(progress: Pair<Number, Number>) {
            observer.taskProgress(progress)
        }

        override fun onDone(result: Boolean?) {
            observer.taskFinish(result == true)
            consumer?.accept(result?:false)
        }

        override fun onCancelled() {
            observer.taskFinish(false)
        }

        override fun publish(current: Long, total: Long) {
            postProgress(current to total)
        }
    }

    /**
     * Unzip entries from archive URL task
     *
     * @property dest destination
     * @property observer progress observer
     * @property consumer result consumer
     * @property publishRate progress publish rate
     */
    class AsyncUnzipFromArchiveUrl(
        private val dest: String,
        private val observer: TaskObserver<Pair<Number, Number>>,
        private val consumer: Consumer<Boolean>?,
        private val publishRate: Int,
    ) : Task<URL, Pair<Number, Number>, Boolean>(), DeployOps.Publisher {

        override fun doJob(params: URL?): Boolean {
            val srcArchiveArg = params!!
            return unzipFromArchiveUrl(srcArchiveArg, dest, this, this, publishRate)
        }

        override fun onPreExecute() {
            observer.taskStart(this)
        }

        override fun onProgress(progress: Pair<Number, Number>) {
            observer.taskProgress(progress)
        }

        override fun onDone(result: Boolean?) {
            observer.taskFinish(result == true)
            consumer?.accept(result?:false)
        }

        override fun onCancelled() {
            observer.taskFinish(false)
        }

        override fun publish(current: Long, total: Long) {
            postProgress(current to total)
        }
    }

    /**
     * Unzip entry from archive file task
     *
     * @property entry entry
     * @property dest destination
     * @property observer progress observer
     * @property consumer result consumer
     * @property publishRate progress publish rate
     */
    class AsyncUnzipEntryFromArchiveFile(
        private val entry: String,
        private val dest: String,
        private val observer: TaskObserver<Pair<Number, Number>>,
        private val consumer: Consumer<Boolean>?,
        private val publishRate: Int,
    ) : Task<String, Pair<Number, Number>, Boolean>(), DeployOps.Publisher {

        override fun doJob(params: String?): Boolean {
            val srcArchiveArg = params!!
            return unzipEntryFromArchiveFile(srcArchiveArg, entry, dest, this, this, publishRate)
        }

        override fun onPreExecute() {
            observer.taskStart(this)
        }

        override fun onProgress(progress: Pair<Number, Number>) {
            observer.taskProgress(progress)
        }

        override fun onDone(result: Boolean?) {
            observer.taskFinish(result == true)
            consumer?.accept(result?:false)
        }

        override fun onCancelled() {
            observer.taskFinish(false)
        }

        override fun publish(current: Long, total: Long) {
            postProgress(current to total)
        }
    }

    /**
     * Unzip entry from archive Uri task
     *
     * @property entry entry
     * @property dest destination
     * @property resolver content resolver
     * @property observer progress observer
     * @property consumer result consumer
     * @property publishRate progress publish rate
     */
    class AsyncUnzipEntryFromArchiveUri(

        private val entry: String,
        private val dest: String,
        private val resolver: ContentResolver,
        private val observer: TaskObserver<Pair<Number, Number>>,
        private val consumer: Consumer<Boolean>?,
        private val publishRate: Int,
    ) : Task<Uri, Pair<Number, Number>, Boolean>(), DeployOps.Publisher {

        override fun doJob(params: Uri?): Boolean {
            val srcArchiveArg = params!!
            return unzipEntryFromArchiveUri(srcArchiveArg, entry, resolver, dest, this, this, publishRate)
        }

        override fun onPreExecute() {
            observer.taskStart(this)
        }

        override fun onProgress(progress: Pair<Number, Number>) {
            observer.taskProgress(progress)
        }

        override fun onDone(result: Boolean?) {
            observer.taskFinish(result == true)
            consumer?.accept(result?:false)
        }

        override fun onCancelled() {
            observer.taskFinish(false)
        }

        override fun publish(current: Long, total: Long) {
            postProgress(current to total)
        }
    }

    /**
     * Unzip entry from archive URL task
     *
     * @property entry entry
     * @property dest
     * @property observer progress observer
     * @property consumer result consumer
     * @property publishRate progress publish rate
     */
    class AsyncUnzipEntryFromArchiveUrl(

        private val entry: String,
        private val dest: String,
        private val observer: TaskObserver<Pair<Number, Number>>,
        private val consumer: Consumer<Boolean>?,
        private val publishRate: Int,
    ) : Task<URL, Pair<Number, Number>, Boolean>(), DeployOps.Publisher {

        override fun doJob(params: URL?): Boolean {
            val srcArchiveArg = params!!
            return unzipEntryFromArchiveUrl(srcArchiveArg, entry, dest, this, this, publishRate)
        }

        override fun onPreExecute() {
            observer.taskStart(this)
        }

        override fun onProgress(progress: Pair<Number, Number>) {
            observer.taskProgress(progress)
        }

        override fun onDone(result: Boolean?) {
            observer.taskFinish(result == true)
            consumer?.accept(result?:false)
        }

        override fun onCancelled() {
            observer.taskFinish(false)
        }

        override fun publish(current: Long, total: Long) {
            postProgress(current to total)
        }
    }

    // md5

    /**
     * MD5 from file task
     *
     * @property observer progress observer
     * @property consumer result consumer
     * @property publishRate progress publish rate
     */
    class AsyncMd5FromFile(
        private val observer: TaskObserver<Pair<Number, Number>>,
        private val consumer: Consumer<String?>?,
        private val publishRate: Int,
    ) : Task<String, Pair<Number, Number>, String?>(), DeployOps.Publisher {

        override fun doJob(params: String?): String? {
            val srcFileArg = params!!
            return md5FromFile(srcFileArg, this, this, publishRate)
        }

        override fun onPreExecute() {
            observer.taskStart(this)
        }

        override fun onProgress(progress: Pair<Number, Number>) {
            observer.taskProgress(progress)
        }

        override fun onCancelled() {
            observer.taskFinish(false)
            consumer?.accept(null)
        }

        override fun onDone(result: String?) {
            observer.taskFinish(true)
            consumer?.accept(result)
        }

        override fun publish(current: Long, total: Long) {
            postProgress(current to total)
        }
    }

    /**
     * MD5 from Uri task
     *
     * @property resolver content resolver
     * @property observer progress observer
     * @property consumer result consumer
     * @property publishRate progress publish rate
     */
    class AsyncMd5FromUri(
        private val resolver: ContentResolver,
        private val observer: TaskObserver<Pair<Number, Number>>,
        private val consumer: Consumer<String?>?,
        private val publishRate: Int,
    ) : Task<Uri, Pair<Number, Number>, String?>(), DeployOps.Publisher {

        override fun doJob(params: Uri?): String? {
            val uriArg = params!!
            return md5FromUri(uriArg, resolver, this, this, publishRate)
        }

        override fun onPreExecute() {
            observer.taskStart(this)
        }

        override fun onProgress(progress: Pair<Number, Number>) {
            observer.taskProgress(progress)
        }

        override fun onCancelled() {
            observer.taskFinish(false)
            consumer?.accept(null)
        }

        override fun onDone(result: String?) {
            observer.taskFinish(true)
            consumer?.accept(result)
        }

        override fun publish(current: Long, total: Long) {
            postProgress(current to total)
        }
    }

    /**
     * MD5 from URL task
     *
     * @property observer progress observer
     * @property consumer result consumer
     * @property publishRate progress publish rate
     */
    class AsyncMd5FromUrl(
        private val observer: TaskObserver<Pair<Number, Number>>,
        private val consumer: Consumer<String?>?,
        private val publishRate: Int,
    ) : Task<URL, Pair<Number, Number>, String?>(), DeployOps.Publisher {

        override fun doJob(params: URL?): String? {
            val uriArg = params!!
            return md5FromUrl(uriArg, this, this, publishRate)
        }

        override fun onPreExecute() {
            observer.taskStart(this)
        }

        override fun onProgress(progress: Pair<Number, Number>) {
            observer.taskProgress(progress)
        }

        override fun onCancelled() {
            observer.taskFinish(false)
            consumer?.accept(null)
        }

        override fun onDone(result: String?) {
            observer.taskFinish(true)
            consumer?.accept(result)
        }

        override fun publish(current: Long, total: Long) {
            postProgress(current to total)
        }
    }

    // HELPERS

    // copy

    /**
     * Copy from source file
     */
    fun copyFromFile(dest: String): Task<String, Pair<Number, Number>, Boolean> {
        return AsyncCopyFromFile(dest, observer, safeCast(consumer), publishRate)
    }

    /**
     * Copy from uri
     */
    fun copyFromUri(resolver: ContentResolver, dest: String): Task<Uri, Pair<Number, Number>, Boolean> {
        return AsyncCopyFromUri(dest, resolver, observer, safeCast(consumer), publishRate)
    }

    /**
     * Copy from url
     */
    fun copyFromUrl(dest: String): Task<URL, Pair<Number, Number>, Boolean> {
        return AsyncCopyFromUrl(dest, observer, safeCast(consumer), publishRate)
    }

    // unzip

    /**
     * Expand entry from zipfile
     */
    fun unzipEntryFromArchiveFile(entry: String, dest: String): Task<String, Pair<Number, Number>, Boolean> {
        return AsyncUnzipEntryFromArchiveFile(entry, dest, observer, safeCast(consumer), publishRate)
    }

    /**
     * Expand all from zipfile
     */
    fun unzipFromArchiveFile(dest: String): Task<String, Pair<Number, Number>, Boolean> {
        return AsyncUnzipFromArchiveFile(dest, observer, safeCast(consumer), publishRate)
    }

    /**
     * Expand all from zip uri
     */
    fun unzipFromArchiveUri(resolver: ContentResolver, dest: String): Task<Uri, Pair<Number, Number>, Boolean> {
        return AsyncUnzipFromArchiveUri(dest, resolver, observer, safeCast(consumer), publishRate)
    }

    /**
     * Expand entry from zip uri
     */
    fun unzipEntryFromArchiveUri(resolver: ContentResolver, entry: String, dest: String): Task<Uri, Pair<Number, Number>, Boolean> {
        return AsyncUnzipEntryFromArchiveUri(entry, dest, resolver, observer, safeCast(consumer), publishRate)
    }

    /**
     * Expand all from zip uri
     */
    fun unzipFromArchiveUrl(dest: String): Task<URL, Pair<Number, Number>, Boolean> {
        return AsyncUnzipFromArchiveUrl(dest, observer, safeCast(consumer), publishRate)
    }

    /**
     * Expand entry from zip uri
     */
    fun unzipEntryFromArchiveUrl(entry: String, dest: String): Task<URL, Pair<Number, Number>, Boolean> {
        return AsyncUnzipEntryFromArchiveUrl(entry, dest, observer, safeCast(consumer), publishRate)
    }

    // md5

    /**
     * Md5 check sum of file
     */
    fun md5FromFile(): Task<String, Pair<Number, Number>, String?> {
        return AsyncMd5FromFile(observer, safeCast(consumer), publishRate)
    }

    /**
     * Md5 check sum of input stream
     */
    fun md5FromUri(resolver: ContentResolver): Task<Uri, Pair<Number, Number>, String?> {
        return AsyncMd5FromUri(resolver, observer, safeCast(consumer), publishRate)
    }

    /**
     * Md5 check sum of input stream
     */
    fun md5FromUrl(): Task<URL, Pair<Number, Number>, String?> {
        return AsyncMd5FromUrl(observer, safeCast(consumer), publishRate)
    }

    companion object {

        // The Consumer<*> type parameter means that the parameter can be any type of Consumer.
        // The Consumer<T> type parameter means that the return value must be a Consumer of type T.
        // This is a safe cast because the Consumer<*> type parameter is a supertype of the Consumer<T> type parameter.
        // This means that any Consumer<*> can be used as a Consumer<T> without any problems.
        @Suppress("UNCHECKED_CAST")
        private fun <T> safeCast(consumer: Consumer<*>?): Consumer<T>? {
            return consumer as Consumer<T>?
        }

        // L A U N C H E R S

        // copy

        /**
         * Launch copy
         *
         * @param activity   activity
         * @param sourceFile source file
         * @param dest       database path
         * @param whenDone   to run when done
         */
        fun launchCopy(activity: FragmentActivity, sourceFile: String, dest: String, whenDone: Consumer<Boolean>?) {
            val observer = TaskDialogObserver<Pair<Number, Number>>(activity.supportFragmentManager)
                .setTitle(activity.getString(R.string.action_copy_datapack_from_file))
                .setMessage(sourceFile)
            launchCopy(activity, observer, sourceFile, dest, whenDone)
        }

        /**
         * Launch copy
         *
         * @param activity   activity
         * @param observer   observer
         * @param sourceFile source file
         * @param dest       database path
         * @param whenDone   to run when done
         */
        fun launchCopy(activity: Activity, observer: TaskObserver<Pair<Number, Number>>, sourceFile: String, dest: String, whenDone: Consumer<Boolean>?) {
            val consumer = Consumer { success: Boolean -> whenDone?.accept(success) }
            val task = FileTasks(observer, consumer, 1000).copyFromFile(dest)
            task.execute(sourceFile)
            observer.taskUpdate(activity.getString(R.string.status_copying) + ' ' + sourceFile)
        }

        /**
         * Launch copy
         *
         * @param activity activity
         * @param uri      source uri
         * @param dest     database path
         * @param whenDone to run when done
         */
        fun launchCopy(activity: FragmentActivity, uri: Uri, dest: String, whenDone: Consumer<Boolean>?) {
            val observer = TaskDialogObserver<Pair<Number, Number>>(activity.supportFragmentManager)
                .setTitle(activity.getString(R.string.action_copy_datapack_from_file))
                .setMessage(uri.toString())
            launchCopy(activity, observer, uri, dest, whenDone)
        }

        /**
         * Launch copy
         *
         * @param activity activity
         * @param observer observer
         * @param sourceUri      source uri
         * @param dest     database path
         * @param whenDone to run when done
         */
        fun launchCopy(activity: Activity, observer: TaskObserver<Pair<Number, Number>>, sourceUri: Uri, dest: String, whenDone: Consumer<Boolean>?) {
            val task = FileTasks(observer, whenDone, 1000).copyFromUri(activity.contentResolver, dest)
            task.execute(sourceUri)
            observer.taskUpdate(activity.getString(R.string.status_copying) + ' ' + sourceUri)
        }

        /**
         * Launch copy
         *
         * @param activity activity
         * @param url      source url
         * @param dest     database path
         * @param whenDone to run when done
         */
        fun launchCopy(activity: FragmentActivity, url: URL, dest: String, whenDone: Consumer<Boolean>?) {
            val observer = TaskDialogObserver<Pair<Number, Number>>(activity.supportFragmentManager)
                .setTitle(activity.getString(R.string.action_copy_datapack_from_file))
                .setMessage(url.toString())
            launchCopy(activity, observer, url, dest, whenDone)
        }

        /**
         * Launch copy
         *
         * @param activity activity
         * @param observer observer
         * @param sourceUrl  source url
         * @param dest  database path
         * @param whenDone to run when done
         */
        fun launchCopy(activity: Activity, observer: TaskObserver<Pair<Number, Number>>, sourceUrl: URL, dest: String, whenDone: Consumer<Boolean>?) {
            val task = FileTasks(observer, whenDone, 1000).copyFromUrl(dest)
            task.execute(sourceUrl)
            observer.taskUpdate(activity.getString(R.string.status_copying) + ' ' + sourceUrl)
        }

        // unzip

        /**
         * Launch unzipping archive file
         *
         * @param activity   activity
         * @param sourceFile source zip file
         * @param dest       database path
         * @param whenDone   to run when done
         */
        fun launchUnzip(activity: FragmentActivity, sourceFile: String, dest: String, whenDone: Consumer<Boolean>?) {
            val observer = TaskDialogObserver<Pair<Number, Number>>(activity.supportFragmentManager)
                .setTitle(activity.getString(R.string.action_unzip_datapack_from_archive))
                .setMessage(sourceFile)
            launchUnzip(activity, observer, sourceFile, dest, whenDone)
        }

        /**
         * Launch unzipping archive file
         *
         * @param activity   activity
         * @param observer   observer
         * @param sourceFile source zip file
         * @param dest       database path
         * @param whenDone   to run when done
         */
        fun launchUnzip(activity: Activity, observer: TaskObserver<Pair<Number, Number>>, sourceFile: String, dest: String, whenDone: Consumer<Boolean>?) {
            val consumer = Consumer { success: Boolean -> whenDone?.accept(success) }
            val task = FileTasks(observer, consumer, 1000).unzipFromArchiveFile(dest)
            task.execute(sourceFile)
            observer.taskUpdate(activity.getString(R.string.status_unzipping))
        }

        /**
         * Launch unzipping archive uri
         *
         * @param activity  activity
         * @param sourceUri source zip uri
         * @param dest      database path
         * @param whenDone  to run when done
         */
        fun launchUnzip(activity: FragmentActivity, sourceUri: Uri, dest: String, whenDone: Consumer<Boolean>?) {
            val observer = TaskDialogObserver<Pair<Number, Number>>(activity.supportFragmentManager)
                .setTitle(activity.getString(R.string.action_unzip_datapack_from_archive))
                .setMessage(sourceUri.toString())
            launchUnzip(activity, observer, sourceUri, dest, whenDone)
        }

        /**
         * Launch unzipping archive url
         *
         * @param activity  activity
         * @param sourceUrl source zip url
         * @param dest      database path
         * @param whenDone  to run when done
         */
        fun launchUnzip(activity: FragmentActivity, sourceUrl: URL, dest: String, whenDone: Consumer<Boolean>?) {
            val observer = TaskDialogObserver<Pair<Number, Number>>(activity.supportFragmentManager)
                .setTitle(activity.getString(R.string.action_unzip_datapack_from_archive))
                .setMessage(sourceUrl.toString())
            launchUnzip(activity, observer, sourceUrl, dest, whenDone)
        }

        /**
         * Launch unzipping archive uri
         *
         * @param activity  activity
         * @param observer  observer
         * @param sourceUri source zip uri
         * @param dest      database path
         * @param whenDone  to run when done
         */
        fun launchUnzip(activity: Activity, observer: TaskObserver<Pair<Number, Number>>, sourceUri: Uri, dest: String, whenDone: Consumer<Boolean>?) {
            val task = FileTasks(observer, whenDone, 1000).unzipFromArchiveUri(activity.contentResolver, dest)
            task.execute(sourceUri)
            observer.taskUpdate(activity.getString(R.string.status_unzipping))
        }

        /**
         * Launch unzipping archive uri
         *
         * @param activity  activity
         * @param observer  observer
         * @param sourceUrl source zip url
         * @param dest      database path
         * @param whenDone  to run when done
         */
        fun launchUnzip(activity: Activity, observer: TaskObserver<Pair<Number, Number>>, sourceUrl: URL, dest: String, whenDone: Consumer<Boolean>?) {
            val task = FileTasks(observer, whenDone, 1000).unzipFromArchiveUrl(dest)
            task.execute(sourceUrl)
            observer.taskUpdate(activity.getString(R.string.status_unzipping))
        }

        /**
         * Launch unzipping of entry in archive file
         *
         * @param activity   activity
         * @param sourceFile source zip file
         * @param zipEntry   zip entry
         * @param dest       database path
         * @param whenDone   to run when done
         */
        fun launchUnzip(activity: FragmentActivity, sourceFile: String, zipEntry: String, dest: String, whenDone: Consumer<Boolean>?) {
            val observer = TaskDialogObserver<Pair<Number, Number>>(activity.supportFragmentManager)
                .setTitle(activity.getString(R.string.action_unzip_datapack_from_archive))
                .setMessage(sourceFile)
            launchUnzip(activity, observer, sourceFile, zipEntry, dest, whenDone)
        }

        /**
         * Launch unzipping of entry in archive file
         *
         * @param activity   activity
         * @param observer   observer
         * @param sourceFile source zip file
         * @param zipEntry   zip entry
         * @param dest       database path
         * @param whenDone   to run when done
         */
        fun launchUnzip(activity: Activity, observer: TaskObserver<Pair<Number, Number>>, sourceFile: String, zipEntry: String, dest: String, whenDone: Consumer<Boolean>?) {
            val consumer = Consumer { success: Boolean -> whenDone?.accept(success) }
            val task = FileTasks(observer, consumer, 1000).unzipEntryFromArchiveFile(zipEntry, dest)
            task.execute(sourceFile)
            observer.taskUpdate(activity.getString(R.string.status_unzipping) + ' ' + zipEntry)
        }

        /**
         * Launch unzipping of entry in archive uri
         *
         * @param activity  activity
         * @param sourceUri source zip uri
         * @param zipEntry  zip entry
         * @param dest      database path
         * @param whenDone  to run when done
         */
        fun launchUnzip(activity: FragmentActivity, sourceUri: Uri, zipEntry: String, dest: String, whenDone: Consumer<Boolean>?) {
            val observer = TaskDialogObserver<Pair<Number, Number>>(activity.supportFragmentManager)
                .setTitle(activity.getString(R.string.action_unzip_datapack_from_archive))
                .setMessage(sourceUri.toString())
            launchUnzip(activity, observer, sourceUri, zipEntry, dest, whenDone)
        }

        /**
         * Launch unzipping of entry in archive url
         *
         * @param activity  activity
         * @param sourceUrl source zip url
         * @param zipEntry  zip entry
         * @param dest      database path
         * @param whenDone  to run when done
         */
        fun launchUnzip(activity: FragmentActivity, sourceUrl: URL, zipEntry: String, dest: String, whenDone: Consumer<Boolean>?) {
            val observer = TaskDialogObserver<Pair<Number, Number>>(activity.supportFragmentManager)
                .setTitle(activity.getString(R.string.action_unzip_datapack_from_archive))
                .setMessage(sourceUrl.toString())
            launchUnzip(activity, observer, sourceUrl, zipEntry, dest, whenDone)
        }

        /**
         * Launch unzipping of entry in archive uri
         *
         * @param activity  activity
         * @param observer  observer
         * @param sourceUri source zip uri
         * @param zipEntry  zip entry
         * @param dest      database path
         * @param whenDone  to run when done
         */
        fun launchUnzip(activity: Activity, observer: TaskObserver<Pair<Number, Number>>, sourceUri: Uri, zipEntry: String, dest: String, whenDone: Consumer<Boolean>?) {
            val task = FileTasks(observer, whenDone, 1000).unzipEntryFromArchiveUri(activity.contentResolver, zipEntry, dest)
            task.execute(sourceUri)
            observer.taskUpdate(activity.getString(R.string.status_unzipping) + ' ' + zipEntry)
        }

        /**
         * Launch unzipping of entry in archive url
         *
         * @param activity  activity
         * @param observer  observer
         * @param sourceUrl source zip url
         * @param zipEntry  zip entry
         * @param dest      database path
         * @param whenDone  to run when done
         */
        fun launchUnzip(activity: Activity, observer: TaskObserver<Pair<Number, Number>>, sourceUrl: URL, zipEntry: String, dest: String, whenDone: Consumer<Boolean>?) {
            val task = FileTasks(observer, whenDone, 1000).unzipEntryFromArchiveUrl(zipEntry, dest)
            task.execute(sourceUrl)
            observer.taskUpdate(activity.getString(R.string.status_unzipping) + ' ' + zipEntry)
        }

        // md5

        /**
         * Launch computation of MD5
         *
         * @param activity   activity
         * @param sourceFile source file
         * @param whenDone   to run when done
         */
        fun launchMd5(activity: FragmentActivity, sourceFile: String, whenDone: Runnable?) {
            val observer = TaskDialogObserver<Pair<Number, Number>>(activity.supportFragmentManager)
                .setTitle(activity.getString(R.string.action_md5))
                .setMessage(sourceFile)
            launchMd5(activity, observer, sourceFile, whenDone)
        }

        /**
         * Launch computation of MD5
         *
         * @param activity activity
         * @param uri      uri
         * @param whenDone to run when done
         */
        fun launchMd5(activity: FragmentActivity, uri: Uri, whenDone: Runnable?) {
            val observer = TaskDialogObserver<Pair<Number, Number>>(activity.supportFragmentManager)
                .setTitle(activity.getString(R.string.action_md5))
                .setMessage("input stream")
            launchMd5(activity, observer, uri, whenDone)
        }

        /**
         * Launch computation of MD5
         *
         * @param activity activity
         * @param url      url
         * @param whenDone to run when done
         */
        fun launchMd5(activity: FragmentActivity, url: URL, whenDone: Runnable?) {
            val observer = TaskDialogObserver<Pair<Number, Number>>(activity.supportFragmentManager)
                .setTitle(activity.getString(R.string.action_md5))
                .setMessage("input stream")
            launchMd5(activity, observer, url, whenDone)
        }

        /**
         * Launch computation of MD5
         *
         * @param activity   activity
         * @param observer   observer
         * @param sourceFile source file
         * @param whenDone   to run when done
         */
        fun launchMd5(activity: Activity, observer: TaskObserver<Pair<Number, Number>>, sourceFile: String, whenDone: Runnable?) {
            val consumer = Consumer { md5: String? ->
                val alert2 = AlertDialog.Builder(activity)
                if (md5 != null) {
                    alert2.setMessage(md5)
                } else {
                    alert2.setMessage(R.string.result_fail)
                }
                alert2.setOnDismissListener { _: DialogInterface? -> whenDone?.run() }
                alert2.show()
            }
            val task = FileTasks(observer, safeCast<String?>(consumer), 1000).md5FromFile()
            task.execute(sourceFile)
            observer.taskUpdate(activity.getString(R.string.status_md5_checking) + ' ' + sourceFile)
        }

        /**
         * Launch computation of MD5
         *
         * @param activity activity
         * @param observer observer
         * @param uri      uri
         * @param whenDone to run when done
         */
        fun launchMd5(activity: Activity, observer: TaskObserver<Pair<Number, Number>>, uri: Uri, whenDone: Runnable?) {
            val consumer = Consumer { md5: String? ->
                val alert2 = AlertDialog.Builder(activity)
                if (md5 != null) {
                    alert2.setMessage(md5)
                } else {
                    alert2.setMessage(R.string.result_fail)
                }
                alert2.setOnDismissListener { _: DialogInterface? -> whenDone?.run() }
                alert2.show()
            }
            val task = FileTasks(observer, safeCast<String?>(consumer), 1000).md5FromUri(activity.contentResolver)
            task.execute(uri)
            observer.taskUpdate(activity.getString(R.string.status_md5_checking) + ' ' + "input stream")
        }

        /**
         * Launch computation of MD5
         *
         * @param activity activity
         * @param observer observer
         * @param url      url
         * @param whenDone to run when done
         */
        fun launchMd5(activity: Activity, observer: TaskObserver<Pair<Number, Number>>, url: URL, whenDone: Runnable?) {
            val consumer = Consumer { md5: String? ->
                val alert2 = AlertDialog.Builder(activity)
                if (md5 != null) {
                    alert2.setMessage(md5)
                } else {
                    alert2.setMessage(R.string.result_fail)
                }
                alert2.setOnDismissListener { _: DialogInterface? -> whenDone?.run() }
                alert2.show()
            }
            val task = FileTasks(observer, safeCast<String?>(consumer), 1000).md5FromUrl()
            task.execute(url)
            observer.taskUpdate(activity.getString(R.string.status_md5_checking) + ' ' + "input stream")
        }
    }
}
