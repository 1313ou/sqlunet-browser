/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.workers.core

import android.os.Build
import android.util.Log
import com.bbou.download.DownloadData
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.util.function.BiConsumer
import java.util.zip.ZipInputStream

/**
 * Download Zip Core
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class DownloadZipCore(progressConsumer: BiConsumer<Long, Long>) : DownloadCore(progressConsumer) {

    /**
     * Zip entry
     */
    private var entryFilter: String? = null

    // W O R K

    /**
     * Work
     *
     * @param fromUrl    zip source
     * @param toFile      destination dir
     * @param renameFrom rename source
     * @param renameTo   rename destination
     * @param entry      zip source entry
     * @return download data
     */
    @Throws(Exception::class)
    override fun work(fromUrl: String, toFile: String, renameFrom: String?, renameTo: String?, entry: String?): DownloadData {
        this.entryFilter = entry
        return super.work(fromUrl, toFile, renameFrom, renameTo, null)
    }

    // C O R E W O R K

    /**
     * Download job
     *
     * @return download data
     */
    @Throws(Exception::class)
    override fun job(): DownloadData {
        // first
        prerequisite()
        val outFile = File(toFile!!)
        var date: Long
        var size: Long
        val zDate: Long
        val zSize: Long
        var zEtag: String? = null
        var zVersion: String? = null
        var zStaticVersion: String? = null
        var done = false
        var httpConnection: HttpURLConnection? = null
        try {
            // connection
            val url = URL(fromUrl)
            Log.d(TAG, "Getting $url")
            var connection = url.openConnection()
            connection.connectTimeout = TIMEOUT_S * 1000
            // connection.addRequestProperty("If-None-Match", "*"); // returns HTTP 304 Not Modified

            // handle redirect
            if (connection is HttpURLConnection) {
                httpConnection = connection
                httpConnection.instanceFollowRedirects = false
                HttpURLConnection.setFollowRedirects(false)
                val status = httpConnection.responseCode
                Log.d(TAG, "Response Code ... $status")
                if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    // headers

                    // zDate = connection.getLastModified(); // new Date(date))
                    // zSize = connection.getContentLength()
                    zEtag = connection.getHeaderField("etag")
                    zVersion = connection.getHeaderField("x-version")
                    zStaticVersion = connection.getHeaderField("x-static-version")

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

            // getting zip file length
            // expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
            if (connection is HttpURLConnection) {
                if (httpConnection!!.responseCode != HttpURLConnection.HTTP_OK) {
                    val message = "server returned HTTP " + httpConnection.responseCode + " " + httpConnection.responseMessage
                    throw RuntimeException(message)
                }
            }

            // headers
            Log.d(TAG, "Headers " + connection.headerFields)
            zDate = connection.lastModified // new Date(date))
            zSize = connection.contentLength.toLong()
            if (zEtag == null) {
                zEtag = connection.getHeaderField("etag")
            }
            if (zVersion == null) {
                zVersion = connection.getHeaderField("x-version")
            }
            if (zStaticVersion == null) {
                zStaticVersion = connection.getHeaderField("x-static-version")
            }

            // extract
            BufferedInputStream(connection.getInputStream(), CHUNK_SIZE)
                .use { `is` ->
                    ZipInputStream(`is`)
                        .use { zis ->
                            generateSequence { zis.nextEntry }
                                .filterNot { it.isDirectory }
                                .filter { entryFilter.isNullOrEmpty() || it.name.matches(entryFilter!!.toRegex()) } // accept if filter unspecified
                                .forEach { it ->

                                    // get the entry
                                    val entryName = it.name
                                    size = it.size
                                    date = it.time
                                    Log.d(TAG, "Extracting $it $size $date")

                                    // files
                                    val tempOutFile = File(outFile, "$entryName.part")
                                    val entryOutFile = File(outFile, entryName)

                                    // dirs
                                    val parent = tempOutFile.parentFile
                                    if (!parent!!.exists())
                                        parent.mkdirs()

                                    // copy
                                    val os = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Files.newOutputStream(tempOutFile.toPath()) else FileOutputStream(tempOutFile)
                                    os.use { copyStreams(zis, it, size) }

                                    // rename temp to target
                                    tempOutFile.renameTo(entryOutFile)

                                    // optional rename
                                    if (entryName == renameFrom && renameTo != null) {
                                        val renamed = renameTo?.run { File(outFile, this) }
                                        entryOutFile.renameTo(renamed!!)
                                    }

                                    // date
                                    setDate(entryOutFile, date)
                                    done = true
                                }
                        }
                }
        } finally {
            httpConnection?.disconnect()
        }
        Log.d(TAG, "Downloaded " + outFile.absolutePath)

        // tail
        return if (done) {
            DownloadData(fromUrl, toFile, zDate, zSize, zEtag, zVersion, zStaticVersion)
        } else {
            throw RuntimeException("Entry not found $entryFilter")
        }
    }

    companion object {

        private const val TAG = "DownloadDelegateZip"
    }
}
