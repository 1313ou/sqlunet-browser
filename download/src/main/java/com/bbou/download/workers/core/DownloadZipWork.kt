/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.workers.core

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import java.util.UUID

/**
 * Download worker
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object DownloadZipWork : DownloadWork() {

    private const val ARG_ENTRY = "from_entry"

    /**
     * Utilities
     */
    object Utils {

        /**
         * Enqueuing work with observer
         *
         * @param context    context
         * @param fromUrl    url to download
         * @param toFile     file to save
         * @param renameFrom rename source
         * @param renameTo   rename destination
         * @param owner      lifecycle owner
         * @param observer   observer
         * @return work uuid
         */
        @JvmStatic
        fun startWork(context: Context, fromUrl: String, entry: String?, toFile: String, renameFrom: String?, renameTo: String?, owner: LifecycleOwner, observer: Observer<WorkInfo>): UUID {
            val wm = WorkManager.getInstance(context)

            // request
            val data = Data.Builder()
                .putString(ARG_FROM, fromUrl)
                .putString(ARG_ENTRY, entry)
                .putString(ARG_TO, toFile)
                .putString(ARG_RENAME_FROM, renameFrom)
                .putString(ARG_RENAME_TO, renameTo)
                .build()
            val downloadRequest: WorkRequest = OneTimeWorkRequest.Builder(DownloadZipWorker::class.java)
                .setInputData(data)
                .addTag(WORKER_TAG)
                .build()
            val uuid = downloadRequest.id

            // observe
            DownloadWork.Utils.observe(context, uuid, owner, observer)

            // run
            wm.enqueue(downloadRequest)
            return uuid
        }
    }

    // C O R O U T I N E W O R K E R

    /**
     * Download Zip worker
     *
     * @constructor
     *
     * @param context context
     * @param params parameters
     */
    class DownloadZipWorker(context: Context, params: WorkerParameters) : DownloadWorker(context, params) {

        /**
         * Delegate
         */
        private val delegate = DownloadZipCore(progressConsumer)

        /**
         * Do work
         *
         * @return result
         */
        @WorkerThread
        override fun doWork(): Result {

            // retrieve params
            val inData = this.inputData
            val fromUrl = inData.getString(ARG_FROM)
            val entry = inData.getString(ARG_ENTRY)
            val toFile = inData.getString(ARG_TO)
            val renameFrom = inData.getString(ARG_RENAME_FROM)
            val renameTo = inData.getString(ARG_RENAME_TO)

            // do the work
            return try {
                assert(fromUrl != null)
                assert(toFile != null)
                val downloadData = delegate.work(fromUrl!!, toFile!!, renameFrom, renameTo, entry)
                val builder = downloadData.toData()
                val outputData = builder
                    .putString(EXCEPTION, null)
                    .putLong(PROGRESS, downloadData.size ?: -1)
                    .putLong(TOTAL, downloadData.size ?: -1)
                    .build()
                Result.success(outputData)
            } catch (e: Exception) {
                val outputData = Data.Builder()
                    .putString(EXCEPTION, e.message)
                    .putString(EXCEPTION_CAUSE, if (e.cause != null) e.cause!!.message else null)
                    .putLong(PROGRESS, 0)
                    .putLong(TOTAL, 0)
                    .build()
                Result.failure(outputData)
            }
        }

        /**
         * On stopped callback
         */
        override fun onStopped() {
            super.onStopped()

            // use this callback as the signal for your worker to cancel its ongoing work
            delegate.cancel()
        }
    }
}
