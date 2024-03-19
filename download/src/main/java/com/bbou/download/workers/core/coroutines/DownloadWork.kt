package com.bbou.download.workers.core.coroutines

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import com.bbou.download.workers.core.coroutines.DownloadWork.DownloadWorker.Companion.ARG_FROM
import com.bbou.download.workers.core.coroutines.DownloadWork.DownloadWorker.Companion.ARG_TO
import com.bbou.download.workers.core.coroutines.DownloadWork.DownloadWorker.Companion.WORKER_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.function.BiConsumer
import kotlin.coroutines.cancellation.CancellationException

/**
 * Download worker based on coroutine worker
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class DownloadWork {

    /**
     * Download coroutine worker
     *
     * @constructor
     *
     * @param context context
     * @param params parameters
     */
    open class DownloadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

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

        @WorkerThread
        override suspend fun doWork(): Result {

            // retrieve params
            val inData = this.inputData
            val fromUrl = inData.getString(ARG_FROM)
            val toFile = inData.getString(ARG_TO)
            val renameFrom = inData.getString(ARG_RENAME_FROM)
            val renameTo = inData.getString(ARG_RENAME_TO)

            // do the work
            return try {
                assert(fromUrl != null)
                assert(toFile != null)

                coroutineScope {
                    withContext(Dispatchers.Default) {

                        // launch
                        val job = async {
                            delegate.work(fromUrl!!, toFile!!, renameFrom, renameTo, null)
                        }

                        // on completion
                        job.invokeOnCompletion { exception: Throwable? ->
                            when (exception) {
                                is CancellationException -> {
                                    // cleanup on completion/cancellations
                                    delegate.cancel()
                                }

                                else -> {
                                    // do something else.
                                }
                            }
                        }

                        // await
                        val outData = job.await()

                        // convert to worker data
                        val outputData = Data.Builder()
                            .putString(ARG_FROM, outData!!.fromUrl)
                            .putString(ARG_TO, outData.toFile)
                            .putString(EXCEPTION, null)
                            .putLong(SIZE, outData.size?:-1)
                            .putLong(PROGRESS, outData.size?:-1)
                            .putLong(TOTAL, outData.size?:-1)
                            .putLong(DATE, outData.date?:-1)
                            .putString(ETAG, outData.etag)
                            .putString(VERSION, outData.version)
                            .putString(STATIC_VERSION, outData.staticVersion)
                            .build()
                        Result.success(outputData)
                    }
                }
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

            /**
             * Size key
             */
            const val SIZE = "download_size"

            /**
             * Etag key
             */
            const val ETAG = "download_etag"

            /**
             * Date key
             */
            const val DATE = "download_date"

            /**
             * Version key
             */
            const val VERSION = "download_version"

            /**
             * Static version key
             */
            const val STATIC_VERSION = "download_static_version"
        }
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
