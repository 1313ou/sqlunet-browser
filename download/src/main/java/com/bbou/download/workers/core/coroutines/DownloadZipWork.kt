package com.bbou.download.workers.core.coroutines

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import com.bbou.download.workers.core.coroutines.DownloadWork.DownloadWorker.Companion.ARG_FROM
import com.bbou.download.workers.core.coroutines.DownloadWork.DownloadWorker.Companion.ARG_RENAME_FROM
import com.bbou.download.workers.core.coroutines.DownloadWork.DownloadWorker.Companion.ARG_RENAME_TO
import com.bbou.download.workers.core.coroutines.DownloadWork.DownloadWorker.Companion.ARG_TO
import com.bbou.download.workers.core.coroutines.DownloadWork.DownloadWorker.Companion.WORKER_TAG
import com.bbou.download.workers.core.toData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

/**
 * Download Zip worker based on coroutine worker
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object DownloadZipWork : DownloadWork() {

    private const val ARG_ENTRY = "from_entry"

    /**
     * Download zip coroutine worker
     *
     * @constructor
     *
     * @param context
     * @param params
     */
    class DownloadZipWorker(context: Context, params: WorkerParameters) : DownloadWorker(context, params) {

        private val delegate = DownloadZipCore(progressConsumer)

        override suspend fun doWork(): Result {
            // retrieve params
            val inData = this.inputData
            val fromUrl = inData.getString(ARG_FROM)
            val entry = inData.getString(ARG_ENTRY)
            val toFile = inData.getString(ARG_TO)
            val renameFrom = inData.getString(ARG_RENAME_FROM)
            val renameTo = inData.getString(ARG_RENAME_TO)

            // do the work
            return try {
                coroutineScope {
                    withContext(Dispatchers.Default) {

                        // launch
                        val job = async { delegate.work(fromUrl!!, toFile!!, renameFrom, renameTo, entry) }

                        // await
                        val downloadData = job.await()

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

                        // convert to worker data
                        val builder = downloadData.toData()
                        val outputData = builder
                            .putString(EXCEPTION, null)
                            .putLong(PROGRESS, downloadData.size ?: -1)
                            .putLong(TOTAL, downloadData.size ?: -1)
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
    }

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
}
