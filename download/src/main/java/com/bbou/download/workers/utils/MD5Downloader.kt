/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.workers.utils

import android.util.Log
import com.bbou.concurrency.Task
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * MD5 downloader task
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class MD5Downloader(private val listener: Listener) : Task<Pair<String, String>, Void, String?>() {

    /**
     * Exception while executing
     */
    private var exception: Exception? = null

    override fun doJob(params: Pair<String, String>?): String? {
        val md5Arg = params!!.first
        val targetArg = params.second
        var httpConnection: HttpURLConnection? = null
        try {
            // connect
            val url = URL(md5Arg)
            Log.d(TAG, "Getting $url")
            val connection = url.openConnection()
            connection.connect()

            // expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
            if (connection is HttpURLConnection) {
                httpConnection = connection
                if (httpConnection.responseCode != HttpURLConnection.HTTP_OK) {
                    val message = "server returned HTTP " + httpConnection.responseCode + " " + httpConnection.responseMessage
                    throw RuntimeException(message)
                }
            }
            connection.getInputStream().use { `is` ->
                InputStreamReader(`is`).use { isr ->
                    BufferedReader(isr).use { reader ->
                        reader.lineSequence().forEach { it ->
                            if (it.contains(targetArg)) {
                                val fields = it.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                return fields[0].trim { it <= ' ' }
                            }
                            // cooperative exit
                            if (isCancelled()) {
                                Log.d(TAG, "Cancelled!")
                                throw InterruptedException("cancelled")
                            }
                            if (Thread.interrupted()) {
                                Log.d(TAG, "Interrupted!")
                                val ie = InterruptedException("interrupted while downloading")
                                exception = ie
                                throw ie
                            }
                        }
                    }
                }
            }
            return null
        } catch (e: InterruptedException) {
            exception = e
            Log.d(TAG, e.toString())
        } catch (e: Exception) {
            exception = e
            Log.e(TAG, "While downloading", e)
        } finally {
            httpConnection?.disconnect()
        }
        return null
    }

    override fun onDone(result: String?) {
        Log.d(TAG, "Completed $result")
        listener.onDone(result)
    }

    override fun onCancelled() {
        Log.d(TAG, "Cancelled")
        listener.onDone("???")
    }

    /**
     * MD5 downloader listener
     */
    fun interface Listener {
        /**
         * Done
         *
         * @param result md5 digest
         */
        fun onDone(result: String?)
    }

    companion object {
        private const val TAG = "MD5Downloader"
    }
}
