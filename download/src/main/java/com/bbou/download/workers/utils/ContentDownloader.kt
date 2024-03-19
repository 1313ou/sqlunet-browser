/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.workers.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.bbou.concurrency.Task
import com.bbou.deploy.workers.Deploy
import com.bbou.download.Keys
import com.bbou.download.Keys.DOWNLOAD_FROM_ARG
import com.bbou.download.Keys.DOWNLOAD_MODE_ARG
import com.bbou.download.Keys.DOWNLOAD_TARGET_FILE_ARG
import com.bbou.download.Keys.DOWNLOAD_TO_FILE_ARG
import com.bbou.download.common.R
import com.bbou.download.preference.Settings
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.util.function.BiConsumer

/**
 * (Table of) Contents downloader task
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
@Suppress("unused")
class ContentDownloader(private val listener: Listener) : Task<String, Void, Array<String>?>() {

    /**
     * Exception while executing
     */
    private var exception: Exception? = null

    override fun doJob(params: String?): Array<String>? {
        val srcArg = params!!
        try {
            // connect
            val url = URL(srcArg)
            Log.d(TAG, "Getting $url")
            val connection = url.openConnection()
            connection.connect()

            // expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
            if (connection is HttpURLConnection) {
                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    val message = "server returned HTTP " + connection.responseCode + " " + connection.responseMessage
                    throw RuntimeException(message)
                }
            }
            connection.getInputStream().use { input ->
                InputStreamReader(input).use { isr ->
                    BufferedReader(isr).use { reader ->
                        // read
                        val items: MutableList<String> = ArrayList()
                        reader.lineSequence().forEach {
                            items.add(it)

                            // interrupted
                            if (Thread.interrupted()) {
                                val ie = InterruptedException("interrupted while downloading")
                                exception = ie
                                throw ie
                            }

                            // cancelled
                            if (isCancelled()) {
                                throw InterruptedException("cancelled")
                            }
                        }
                        return items.toTypedArray<String>()
                    }
                }
            }
        } catch (e: InterruptedException) {
            exception = e
            Log.d(TAG, "Interrupted $e")
        } catch (e: Exception) {
            exception = e
            Log.e(TAG, "While downloading", e)
        }
        return null
    }

    override fun onDone(result: Array<String>?) {
        Log.d(TAG, "Completed")
        listener.onDone(result)
    }

    override fun onCancelled() {
        Log.d(TAG, "Cancelled")
        listener.onDone(null)
    }

    /**
     * Content downloader listener
     */
    fun interface Listener {
        /**
         * Done
         *
         * @param result content items
         */
        fun onDone(result: Array<String>?)
    }

    companion object {
        private const val TAG = "ContentDownloader"
        private const val CONTENT_FILE = "content"

        /** @noinspection UnusedReturnValue
         */
        private fun addTargetToIntent(context: Context, intent: Intent, name: String): Intent {
            return when (val mode = intent.getStringExtra(DOWNLOAD_MODE_ARG)!!) {
                Settings.Mode.DOWNLOAD.toString() -> {
                    addTargetToIntentPlainDownload(context, intent, name)
                }

                Settings.Mode.DOWNLOAD_ZIP.toString() -> {
                    addTargetToIntentZipDownload(context, intent, name)
                }

                Settings.Mode.DOWNLOAD_ZIP_THEN_UNZIP.toString() -> {
                    addTargetToIntentZipDownloadThenDeploy(context, intent, name)
                }

                else -> throw RuntimeException(mode)
            }
        }

        private fun addTargetToIntentPlainDownload(context: Context, intent: Intent, name: String): Intent {
            val source = "${Settings.getRepoPref(context)}/$name"
            val dest = "${Settings.getDatapackDir(context)}/$name"
            intent.putExtra(DOWNLOAD_FROM_ARG, source) // source archive
            intent.putExtra(DOWNLOAD_TO_FILE_ARG, dest) // dest file
            intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, dest) // target file
            return intent
        }

        private fun addTargetToIntentZipDownload(context: Context, intent: Intent, name: String): Intent {
            var zipname = name
            if (!zipname.endsWith(Deploy.ZIP_EXTENSION)) {
                zipname += Deploy.ZIP_EXTENSION
            }
            val source = "${Settings.getRepoPref(context)}/$zipname"
            val dest = Settings.getDatapackDir(context)
            val target = Settings.getDownloadTarget(context, name)
            intent.putExtra(DOWNLOAD_FROM_ARG, source) // source archive
            intent.putExtra(Keys.DOWNLOAD_TO_DIR_ARG, dest) // source archive
            intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, target) // target file to be extracted
            return intent
        }

        private fun addTargetToIntentZipDownloadThenDeploy(context: Context, intent: Intent, name: String): Intent {
            var zipname = name
            if (!zipname.endsWith(Deploy.ZIP_EXTENSION)) {
                zipname += Deploy.ZIP_EXTENSION
            }
            val source = "${Settings.getRepoPref(context)}/$zipname"
            val dest = "${Settings.getCachePref(context)}/$zipname"
            val target = Settings.getDownloadTarget(context, name)
            intent.putExtra(DOWNLOAD_FROM_ARG, source) // source archive
            intent.putExtra(DOWNLOAD_TO_FILE_ARG, dest) // destination archive
            intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, target) // target file to be extracted
            return intent
        }

        private fun show(activity: Activity, result: Array<String>?, targetFile: String, consumer: BiConsumer<Context, String>) {

            val inflater = LayoutInflater.from(activity)
            @SuppressLint("InflateParams") val header = inflater.inflate(R.layout.content_header, null)
            val sourceView = header.findViewById<TextView>(R.id.source)
            sourceView.text = targetFile

            val alert = AlertDialog.Builder(activity)
            alert.setCustomTitle(header)
            if (result == null) {
                alert.setIconAttribute(android.R.attr.alertDialogIcon)
                    .setMessage(R.string.status_task_failed)
            } else {
                alert.setItems(result) { _: DialogInterface?, which: Int ->
                    val item = result[which]
                    consumer.accept(activity, item)
                }
            }
            alert.show()
        }

        /**
         * Content
         */
        fun showContents(activity: Activity, consumer: BiConsumer<Context, String>) {
            Toast.makeText(activity, R.string.status_download_downloadable_content, Toast.LENGTH_SHORT).show()
            val activityReference = WeakReference(activity)
            val repo = Settings.getRepoPref(activity)
            if (repo == null) {
                Toast.makeText(activity, R.string.status_download_error_null_download_url, Toast.LENGTH_SHORT).show()
                return
            }
            val dest = Settings.getCachePref(activity)
            val sourceFile = "$repo/$CONTENT_FILE"
            val targetFile = "$dest/$CONTENT_FILE"

            ContentDownloader label@{ result: Array<String>? ->

                // handle result
                // lifecycle
                val activity2 = activityReference.get()
                if (activity2 == null || activity2.isFinishing || activity2.isDestroyed) {
                    return@label
                }
                show(activity2, result, targetFile, consumer)

            }.execute(sourceFile)
        }
    }
}
