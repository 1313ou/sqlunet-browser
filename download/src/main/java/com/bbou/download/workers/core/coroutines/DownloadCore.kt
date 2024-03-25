/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.workers.core.coroutines

import android.os.Build
import android.util.Log
import com.bbou.download.DownloadData
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.util.function.BiConsumer

/**
 * Download delegate
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class DownloadCore(private val progressConsumer: BiConsumer<Long, Long>) {

    /**
     * From URL
     */
    @JvmField
    protected var fromUrl: String? = null

    /**
     * To file
     */
    @JvmField
    protected var toFile: String? = null

    /**
     * Rename source
     */
    @JvmField
    protected var renameFrom: String? = null

    /**
     * Rename dest
     */
    @JvmField
    protected var renameTo: String? = null

    /**
     * Cancel
     */
    private var cancel = false

    // W O R K

    /**
     * Work
     *
     * @param fromUrl    source url
     * @param toFile     destination file
     * @param renameFrom rename source
     * @param renameTo   rename destination
     * @return download data
     */
    @Throws(Exception::class)
    open fun work(fromUrl: String, toFile: String, renameFrom: String?, renameTo: String?, ignoredEntry: String?): DownloadData? {

        // arguments
        this.fromUrl = fromUrl
        this.toFile = toFile
        this.renameFrom = renameFrom
        this.renameTo = renameTo

        // do job
        cancel = false
        return try {
            val data = job()
            Log.d(TAG, "Completed")
            data
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Exception $e")
            cleanup()
            throw e
        }
    }

    // C O R E W O R K

    /**
     * Cancel
     */
    fun cancel() {
        cancel = true
    }

    /**
     * Download job
     *
     * @return download data
     */
    @Throws(Exception::class)
    protected open fun job(): DownloadData {
        // first
        prerequisite()
        val date: Long
        val size: Long
        var etag: String? = null
        var version: String? = null
        var staticVersion: String? = null
        val outFile = File("$toFile.part")
        val tempOutFile = File(toFile!!)
        var httpConnection: HttpURLConnection? = null
        try {
            // connection
            val url = URL(fromUrl)
            Log.d(TAG, "Getting $url")
            var connection = url.openConnection()
            connection.connectTimeout = TIMEOUT_S * 1000

            // handle redirect
            if (connection is HttpURLConnection) {
                httpConnection = connection
                httpConnection.instanceFollowRedirects = false
                HttpURLConnection.setFollowRedirects(false)
                val status = httpConnection.responseCode
                Log.d(TAG, "Response Code ... $status")
                if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    // headers

                    // date = connection.getLastModified()
                    // size = connection.getContentLength()
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
            Log.d(TAG, "Connecting")
            connection.connect()
            Log.d(TAG, "Connected")

            // getting file length
            // expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
            if (connection is HttpURLConnection) {
                if (httpConnection!!.responseCode != HttpURLConnection.HTTP_OK) {
                    val message = "server returned HTTP " + httpConnection.responseCode + " " + httpConnection.responseMessage
                    throw RuntimeException(message)
                }
            }

            // headers
            Log.d(TAG, "Headers " + connection.headerFields)
            date = connection.lastModified // new Date(date))
            size = connection.contentLength.toLong()
            if (etag == null) {
                etag = connection.getHeaderField("etag")
            }
            if (version == null) {
                version = connection.getHeaderField("x-version")
            }
            if (staticVersion == null) {
                staticVersion = connection.getHeaderField("x-static-version")
            }
            val is1 = BufferedInputStream(connection.getInputStream(), CHUNK_SIZE)
            val is2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Files.newOutputStream(tempOutFile.toPath()) else FileOutputStream(tempOutFile)
            is1.use { `is` ->
                is2.use { os ->
                    copyStreams(`is`, os, size)
                }
            }

            // rename temp to target
            tempOutFile.renameTo(outFile)

            // optional rename
            if (renameFrom != null && renameTo != null) {
                val renamed = File(outFile.parent, renameTo!!)
                outFile.renameTo(renamed)
            }

            // date
            setDate(outFile, date)

        } finally {
            httpConnection?.disconnect()
        }
        Log.d(TAG, "Downloaded " + outFile.absolutePath)

        // install
        Log.d(TAG, "Installing " + outFile.absolutePath)
        install(outFile, date, size)
        return DownloadData(fromUrl, toFile, date, size, etag, version, staticVersion)
    }

    /**
     * Prerequisite
     */
    private fun prerequisite() {
        if (toFile == null) {
            return
        }
        val dir = File(toFile!!).parentFile
        if (dir != null && !dir.exists()) {
            dir.mkdirs()
        }
    }

    /**
     * Copy streams or consume input stream
     *
     * @param is    input stream
     * @param os    output stream
     * @param total expected total length
     * @throws IOException io exception
     */
    @Throws(IOException::class, InterruptedException::class)
    protected fun copyStreams(`is`: InputStream, os: OutputStream?, total: Long) {

        progressConsumer.accept(0L, total)

        // copy streams
        val buffer = ByteArray(1024)
        var downloaded: Long = 0
        var chunks = 0
        var count: Int
        while (`is`.read(buffer).also { count = it } != -1) {
            downloaded += count.toLong()

            // publishing the progress
            if (chunks % PUBLISH_MAIN_GRANULARITY == 0) {
                progressConsumer.accept(downloaded, total)
            }
            if (chunks % PUBLISH_UPDATE_GRANULARITY == 0) {
                progressConsumer.accept(downloaded, total)
            }
            chunks++

            // writing data toFile file
            os?.write(buffer, 0, count)

            // interrupted
            if (Thread.interrupted()) {
                throw InterruptedException("interrupted")
            }
            if (cancel) {
                throw InterruptedException("cancelled")
            }
        }
        os?.flush()
    }

    // U T I L S

    /**
     * Install
     *
     * @param outFile temporary file
     * @param date    date stamp
     * @param size    expected size
     */
    private fun install(outFile: File, date: Long, size: Long) {

        if (toFile != null) {
            val newFile = File(toFile!!)
            var success = false
            if (outFile.exists()) {
                // rename
                if (newFile.exists()) {
                    newFile.delete()
                }
                success = outFile.renameTo(newFile)

                // date
                setDate(newFile, date)

                // size check
                if (size != -1L && newFile.length() != size) {
                    throw RuntimeException("Size do not match")
                }
            }
            Log.d(TAG, "Renamed $outFile to $newFile $success")
        }
    }

    /**
     * Set date
     *
     * @param outFile file
     * @param date    date stamp
     */
    private fun setDate(outFile: File, date: Long) {
        if (date != -1L) {
            outFile.setLastModified(date)
        }
    }

    /**
     * Cleanup
     */
    private fun cleanup() {
        if (toFile != null) {
            var file = File(toFile!!)
            if (file.exists()) {
                file.delete()
            }
            file = File("$toFile.part")
            if (file.exists()) {
                file.delete()
            }
        }
    }

    companion object {

        private const val TAG = "DownloadDelegate"

        /**
         * Buffer size
         */
        const val CHUNK_SIZE = 8192

        /**
         * Publish granularity (number of chunks) for update = 1MB x 10 = 10MB
         * 1MB = 8192 chunk size x 128 chunks
         */
        protected const val PUBLISH_UPDATE_GRANULARITY = 128 * 10

        /**
         * Publish granularity for (number of chunks) main = 1MB x 16 = 16MB
         * 1MB = 8192 chunk size x 128 chunks
         */
        protected const val PUBLISH_MAIN_GRANULARITY = 128 * 16

        /**
         * Timeout in seconds
         */
        const val TIMEOUT_S = 15
    }
}
