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
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.UUID
import java.util.function.BiConsumer

/**
 * Download worker
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class DownloadWork {

    // W O R K

    /**
     * Downloader worker
     *
     * @constructor
     *
     * @param context context
     * @param params parameters
     */
    open class DownloadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

        /**
         * Progress consumer
         */
        @JvmField
        protected val progressConsumer = BiConsumer { downloaded: Long, total: Long ->
            setProgressAsync(
                Data.Builder()
                    .putLong(PROGRESS, downloaded)
                    .putLong(TOTAL, total)
                    .build()
            )
        }

        /**
         * Delegate
         */
        private val delegate = DownloadCore(progressConsumer)

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
            val toFile = inData.getString(ARG_TO)
            val renameFrom = inData.getString(ARG_RENAME_FROM)
            val renameTo = inData.getString(ARG_RENAME_TO)

            // do the work
            return try {
                val downloadData = delegate.work(fromUrl!!, toFile!!, renameFrom, renameTo, null)
                val builder = downloadData.toData()
                val outputData = builder
                    .putString(EXCEPTION, null)
                    .putLong(PROGRESS, downloadData.size ?:-1)
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
         * onStopped callback
         */
        override fun onStopped() {
            super.onStopped()

            // use this callback as the signal for your worker to cancel its ongoing work
            delegate.cancel()
        }
    }

    companion object {
        /**
         * Worker tag
         */
        const val WORKER_TAG = "download"

        /**
         * Source URL
         */
        const val ARG_FROM = "from_url"

        /**
         * Destination file
         */
        const val ARG_TO = "to_file"

        /**
         * Rename source key
         */
        const val ARG_RENAME_FROM = "rename_from"

        /**
         * Rename destination key
         */
        const val ARG_RENAME_TO = "rename_to"

        /**
         * Exception key
         */
        const val EXCEPTION = "download_exception"

        /**
         * Exception cause key
         */
        const val EXCEPTION_CAUSE = "download_exception_cause"

        /**
         * Progress key
         */
        const val PROGRESS = "download_progress"

        /**
         * Total key
         */
        const val TOTAL = "download_total"
    }

    /**
     * Utilities
     */
    object Utils {

        // O B S E R V E

        /**
         * Observe
         *
         * @param context context
         * @param id work id
         * @param owner lifecycle owner
         * @param observer work info observer
         */
        @JvmStatic
        fun observe(context: Context, id: UUID, owner: LifecycleOwner, observer: Observer<WorkInfo>) {
            val live = WorkManager.getInstance(context).getWorkInfoByIdLiveData(id)
            live.observe(owner, observer)
        }

        // S T A R T

        /**
         * Enqueuing work with observer
         *
         * @param context  context
         * @param fromUrl  url to download
         * @param toFile   file to save
         * @param owner    lifecycle owner
         * @param observer observer
         * @return work uuid
         */
        @JvmStatic
        fun startWork(context: Context, fromUrl: String, toFile: String, owner: LifecycleOwner, observer: Observer<WorkInfo>): UUID {
            val wm = WorkManager.getInstance(context)

            // request
            val data = Data.Builder()
                .putString(ARG_FROM, fromUrl)
                .putString(ARG_TO, toFile)
                .build()
            val downloadRequest: WorkRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
                .setInputData(data)
                .addTag(WORKER_TAG)
                .build()
            val uuid = downloadRequest.id

            // observe
            observe(context, uuid, owner, observer)

            // run
            wm.enqueue(downloadRequest)
            return uuid
        }

        // S T O P

        /**
         * Cancel work
         *
         * @param context context
         * @param uuid    work uuid
         */
        @JvmStatic
        fun stopWork(context: Context, uuid: UUID) {
            WorkManager.getInstance(context).cancelWorkById(uuid)
        }
    }
}
