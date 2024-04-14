/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.workers.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import com.bbou.concurrency.Task
import com.bbou.download.common.R
import com.bbou.download.workers.utils.ResourcesDownloader.Listener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.function.BiConsumer

/**
 * Resources downloader task
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class ResourcesDownloader internal constructor(private val listener: Listener) : Task<ResourcesDownloader.Params, Void, Collection<Array<String>>?>() {

    /**
     * Parameters
     *
     * @property resourcesUrl resources url
     * @property filter filter
     */
    data class Params(val resourcesUrl: String, val filter: String?)

    /**
     * Exception while executing
     */
    private var exception: Exception? = null

    override fun doJob(params: Params?): Collection<Array<String>>? {
        val resArg = params!!.resourcesUrl
        val lineFilter = params.filter
        var httpConnection: HttpURLConnection? = null
        try {
            // connect
            val url = URL(resArg)
            Log.d(TAG, "Get $url")
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
                        // read
                        var lines: MutableCollection<Array<String>>? = null
                        reader.lineSequence().forEach { it ->
                            var line: String = it
                            line = line.trim { it <= ' ' }
                            if (line.isNotEmpty() && !line.startsWith("#")) {
                                if (lineFilter.isNullOrEmpty() || line.matches(lineFilter.toRegex())) {
                                    val fields: Array<String> = line.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                    if (lines == null) {
                                        lines = ArrayList()
                                    }
                                    lines!!.add(fields)
                                }
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
                        return lines
                    }
                }
            }
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

    override fun onDone(result: Collection<Array<String>>?) {
        Log.d(TAG, "Completed $result")
        listener.onDone(result)
    }

    override fun onCancelled() {
        Log.d(TAG, "Cancelled")
        listener.onDone(null)
    }

    /**
     * Resources downloader listener
     */
    fun interface Listener {

        /**
         * Done
         *
         * @param result resources
         */
        fun onDone(result: Collection<Array<String>>?)
    }

    companion object {

        private const val TAG = "ResourcesDownloader"

        // Get resources from resource directory
        /**
         * Display resources
         */
        fun showResources(activity: Activity) {
            val url = activity.getString(R.string.resources_directory)
            val filter = activity.getString(R.string.resources_directory_filter)
            ResourcesDownloader(Listener { resources: Collection<Array<String>>? ->
                if (activity.isFinishing || activity.isDestroyed) {
                    return@Listener
                }
                if (resources == null) {
                    AlertDialog.Builder(activity)
                        .setTitle(activity.getString(R.string.action_directory) + " of " + url)
                        .setMessage(R.string.status_task_failed)
                        .show()
                } else {
                    if (!activity.isFinishing && !activity.isDestroyed) {
                        val sb = SpannableStringBuilder()
                        sb.append('\n')
                        for (row in resources) {
                            sb.append(java.lang.String.join(" ", *row))
                            sb.append('\n')
                            sb.append('\n')
                        }
                        AlertDialog.Builder(activity)
                            .setTitle(activity.getString(R.string.resource_directory) + ' ' + url)
                            .setMessage(sb)
                            .show()
                    }
                }
            }).execute(Params(url, filter))
        }

        /**
         * Populate radio group with resources
         */
        fun populateLists(context: Context, consumer: BiConsumer<List<String>, List<String>>) {
            val url = context.getString(R.string.resources_directory)
            val filter = context.getString(R.string.resources_directory_filter)
            ResourcesDownloader { resources: Collection<Array<String>>? ->
                if (resources != null) {
                    val values: MutableList<String> = ArrayList()
                    val labels: MutableList<String> = ArrayList()
                    for (row in resources) {
                        // ewn	OEWN	2023	Bitbucket	https://bitbucket.org/semantikos2/semantikos22/raw/53e04fe21bc901ee15631873972445c2c8725652	zipped
                        val value = row[4]
                        val label = String.format("%s %s (%s %s)", row[1], row[2], row[3], row[5])
                        values.add(value)
                        labels.add(label)
                    }
                    consumer.accept(values, labels)
                }
            }.execute(Params(url, filter))
        }

        /**
         * Populate radio group with resources
         */
        fun populateRadioGroup(context: Context, optionsView: RadioGroup) {
            populateLists(context) { values: List<String>, labels: List<String> ->
                val n = values.size.coerceAtMost(labels.size)
                for (i in 0 until n) {
                    val value: CharSequence = values[i]
                    val label: CharSequence = labels[i]
                    val radioButton = RadioButton(context)
                    radioButton.text = label
                    radioButton.tag = value
                    radioButton.isEnabled = true
                    optionsView.addView(radioButton)
                }
            }
        }
    }
}
