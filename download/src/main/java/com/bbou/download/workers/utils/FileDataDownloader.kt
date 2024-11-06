/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.workers.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.bbou.concurrency.Task
import com.bbou.download.Keys.DOWNLOAD_FROM_ARG
import com.bbou.download.UpdateActivity
import com.bbou.download.UpdateFragment
import com.bbou.download.common.R
import com.bbou.download.preference.Settings
import com.bbou.download.utils.FileData
import java.net.HttpURLConnection
import java.net.URL
import java.util.Date

/**
 * File data downloader
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FileDataDownloader(private val listener: Listener) : Task<String, Void, FileData?>() {

    override fun doJob(params: String?): FileData? {
        val urlString = params!!
        var date: Long = -1
        var size: Long = -1
        var etag: String? = null
        var version: String? = null
        var staticVersion: String? = null
        var httpConnection: HttpURLConnection? = null
        try {
            // url
            val url = URL(urlString)
            Log.d(TAG, "Getting $url")

            // connection
            var connection = url.openConnection()

            // handle redirect
            if (connection is HttpURLConnection) {
                httpConnection = connection
                httpConnection.instanceFollowRedirects = false
                HttpURLConnection.setFollowRedirects(false)
                val status = httpConnection.responseCode
                Log.d(TAG, "Response Code ... $status")
                if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    // headers
                    date = connection.lastModified // new Date(date))
                    size = connection.contentLength.toLong()
                    etag = connection.getHeaderField("etag")
                    version = connection.getHeaderField("x-version")
                    staticVersion = connection.getHeaderField("x-static-version")

                    // get redirect url from "location" header field
                    val newUrl = httpConnection.getHeaderField("Location")

                    // close
                    httpConnection.inputStream.close()

                    // disconnect
                    httpConnection.disconnect()

                    // open the new connection again
                    httpConnection = URL(newUrl).openConnection() as HttpURLConnection
                    connection = httpConnection
                    httpConnection.instanceFollowRedirects = true
                    HttpURLConnection.setFollowRedirects(true)
                    Log.d(TAG, "Redirect to URL : $newUrl")
                }
            }

            // connect
            connection.connect()

            // expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
            if (connection is HttpURLConnection) {
                if (httpConnection!!.responseCode != HttpURLConnection.HTTP_OK) {
                    val message = "server returned HTTP " + httpConnection.responseCode + " " + httpConnection.responseMessage
                    throw RuntimeException(message)
                }
            }
            val name = url.file
            if (date <= 0) {
                date = connection.lastModified // new Date(date))
            }
            if (size <= 0) {
                size = connection.contentLength.toLong()
            }
            if (etag == null) {
                etag = connection.getHeaderField("etag")
            }
            if (version == null) {
                version = connection.getHeaderField("x-version")
            }
            if (staticVersion == null) {
                staticVersion = connection.getHeaderField("x-static-version")
            }

            // close
            connection.getInputStream().close()
            return FileData(name, date, size, etag, version, staticVersion)
        } catch (e: Exception) {
            Log.e(TAG, "While downloading", e)
        } finally {
            httpConnection?.disconnect()
        }
        return null
    }

    override fun onDone(result: FileData?) {
        Log.d(TAG, "Completed $result")
        listener.onDone(result)
    }

    override fun onCancelled() {
        Log.d(TAG, "Cancelled")
        listener.onDone(null)
    }

    /**
     * File data downloader listener
     */
    fun interface Listener {

        /**
         * Done
         *
         * @param result file data
         */
        fun onDone(result: FileData?)
    }

    companion object {

        private const val TAG = "FileDataDownloader"

        /**
         * Start file data downloading
         *
         * @param activity       launching activity
         * @param downloadIntent download intent (activity launched if update is requested)
         */
        fun start(activity: Activity, downloadIntent: Intent) {
            // unmarshal arguments
            val downloadSourceUrl = downloadIntent.getStringExtra(DOWNLOAD_FROM_ARG)
            val name = Uri.parse(downloadSourceUrl).lastPathSegment

            // download source data
            if (downloadSourceUrl.isNullOrEmpty() || name == null) {
                val message = activity.getString(R.string.no_url)
                activity.runOnUiThread { Toast.makeText(activity, message, Toast.LENGTH_LONG).show() }
                return
            }

            // download source data (acquired by task)
            val task = FileDataDownloader { srcData: FileData? ->

                // in-use downstream data
                val downDateValue = Settings.getDatapackDate(activity)
                val downSizeValue = Settings.getDatapackSize(activity)
                val downDate = if (downDateValue == -1L || downDateValue == 0L) null else Date(downDateValue)
                val downSize = if (downSizeValue == -1L) null else downSizeValue

                // in-use downstream recorded source data
                val downSource = Settings.getDatapackSource(activity)
                val downSourceDateValue = Settings.getDatapackSourceDate(activity)
                val downSourceSizeValue = Settings.getDatapackSourceSize(activity)
                val downSourceDate = if (downSourceDateValue == -1L || downSourceDateValue == 0L) null else Date(downSourceDateValue)
                val downSourceSize = if (downSourceSizeValue == -1L) null else downSourceSizeValue
                val downSourceEtag = Settings.getDatapackSourceEtag(activity)
                val downSourceVersion = Settings.getDatapackSourceVersion(activity)
                val downSourceStaticVersion = Settings.getDatapackSourceStaticVersion(activity)

                // upstream data from http connection
                val srcDate = srcData?.getDate()
                val srcSize = srcData?.getSize()
                val srcEtag = srcData?.etag
                val srcVersion = srcData?.version
                val srcStaticVersion = srcData?.staticVersion

                // newer
                val same = srcEtag != null && srcEtag == downSourceEtag || srcVersion != null && srcVersion == downSourceVersion || srcStaticVersion != null && srcStaticVersion == downSourceStaticVersion // one match
                val newer = srcDate == null || downDate == null || srcDate > downDate // upstream src date from http connection is newer than recorded date
                val srcNewer = srcDate == null || downSourceDate == null || srcDate > downSourceDate // upstream src date from http connection is newer than recorded source date

                // start update activity
                val intent = Intent(activity, UpdateActivity::class.java)

                // result
                val na = activity.getString(R.string.na)
                val bytes = activity.getString(R.string.bytes)
                intent.putExtra(UpdateFragment.UP_SOURCE_ARG, downloadSourceUrl)
                intent.putExtra(UpdateFragment.UP_DATE_ARG, srcDate?.toString() ?: na)
                intent.putExtra(UpdateFragment.UP_SIZE_ARG, if (srcSize == null) na else "$srcSize $bytes")
                intent.putExtra(UpdateFragment.UP_ETAG_ARG, srcEtag ?: na)
                intent.putExtra(UpdateFragment.UP_VERSION_ARG, srcVersion ?: na)
                intent.putExtra(UpdateFragment.UP_STATIC_VERSION_ARG, srcStaticVersion ?: na)
                intent.putExtra(UpdateFragment.DOWN_NAME_ARG, name)
                intent.putExtra(UpdateFragment.DOWN_DATE_ARG, downDate?.toString() ?: na)
                intent.putExtra(UpdateFragment.DOWN_SIZE_ARG, if (downSize == null) na else "$downSize $bytes")
                intent.putExtra(UpdateFragment.DOWN_SOURCE_ARG, downSource ?: activity.getString(R.string.unsourced))
                intent.putExtra(UpdateFragment.DOWN_SOURCE_DATE_ARG, downSourceDate?.toString() ?: na)
                intent.putExtra(UpdateFragment.DOWN_SOURCE_SIZE_ARG, if (downSourceSize == null) na else "$downSourceSize $bytes")
                intent.putExtra(UpdateFragment.DOWN_SOURCE_ETAG_ARG, downSourceEtag ?: na)
                intent.putExtra(UpdateFragment.DOWN_SOURCE_VERSION_ARG, downSourceVersion ?: na)
                intent.putExtra(UpdateFragment.DOWN_SOURCE_STATIC_VERSION_ARG, if (downSourceVersion == null) na else downSourceStaticVersion)
                intent.putExtra(UpdateFragment.NEWER_ARG, !same && (newer || srcNewer))

                // to do if confirmed
                intent.putExtra(UpdateFragment.DOWNLOAD_INTENT_ARG, downloadIntent)
                activity.startActivity(intent)
            }
            task.execute(downloadSourceUrl)
        }
    }
}
