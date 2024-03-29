/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.workers

import android.content.Context
import android.util.Log
import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.bbou.download.DownloadData
import com.bbou.download.Notifier
import com.bbou.download.ProbedDownloadFragment
import com.bbou.download.preference.Settings
import com.bbou.download.workers.core.DownloadWork
import com.bbou.download.workers.core.toDownloadData
import java.util.UUID

/**
 * Base download fragment. Handles
 * - status
 * - notification
 * - cancel intent receiver
 * - kill and new data emitter
 * - work info livedata observer that observes work info status and data change
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class DownloadBaseFragment : ProbedDownloadFragment() {

    /**
     * Whether to use coroutine workers
     */
    val useCoroutines = true

    // S T A T U S

    /**
     * Result
     */
    @JvmField
    protected var success: Boolean? = null

    /**
     * Cancel pending
     */
    @JvmField
    protected var cancel = false

    /**
     * Cause
     */
    @JvmField
    protected var cause: String? = null

    /**
     * Exception
     */
    @JvmField
    protected var exception: String? = null

    /**
     * Downloading flag (prevents re-entrance)
     */
    @JvmField
    protected var working = false

    // N O T I F I C A T I O N

    /**
     * Fire UI notification
     *
     * @param context        context
     * @param notificationId notification id
     * @param type           notification
     * @param args           arguments
     */
    abstract fun fireNotification(context: Context, notificationId: Int, type: Notifier.NotificationType, vararg args: Any)

    // W O R K I N F O   L I V E D A T A   O B S E R V E R

    /**
     * Observer
     */
    @JvmField
    protected val observer = Observer { wi: WorkInfo ->

        @UiThread

        // progress
        if (working) // drop event if not
        {
            val data = wi.progress
            val progressDownloaded = data.getLong(DownloadWork.PROGRESS, 0)
            val progressTotal = data.getLong(DownloadWork.TOTAL, 0)
            progress = progressDownloaded to progressTotal

            // ui
            val inProgress = status == Status.STATUS_RUNNING || status == Status.STATUS_PAUSED
            if (inProgress) {
                updateUI()
            }

            // notification
            val progressPercent = progressDownloaded.toFloat() / progressTotal
            fireNotification(requireContext(), notificationId, Notifier.NotificationType.UPDATE, progressPercent)

            Log.d(TAG, "Observed progress $progressDownloaded/$progressTotal")
        }

        // from state
        when (wi.state) {
            WorkInfo.State.ENQUEUED -> Log.d(TAG, "Observed enqueued")
            WorkInfo.State.RUNNING -> {
                Log.d(TAG, "Observed running")
                working = true // confirm
                fireNotification(requireContext(), notificationId, Notifier.NotificationType.START)
            }

            WorkInfo.State.SUCCEEDED -> {
                Log.d(TAG, "Observed succeeded")

                // state
                working = false
                success = true
                exception = null
                cause = null

                // status
                status = pullStatus()

                // fire notification
                fireNotification(requireContext(), notificationId, Notifier.NotificationType.FINISH, true)

                // fire onDone
                val downloadData = wi.outputData.toDownloadData()
                onDownloadDone(Status.STATUS_SUCCEEDED, downloadData)
            }

            WorkInfo.State.FAILED -> {
                Log.d(TAG, "Observed failed")

                // state
                working = false // release
                val errData = wi.outputData
                success = false
                exception = errData.getString(DownloadWork.EXCEPTION)
                cause = errData.getString(DownloadWork.EXCEPTION_CAUSE)

                // status
                status = pullStatus()

                // fire notification
                fireNotification(requireContext(), notificationId, Notifier.NotificationType.FINISH, false)

                // fire onDone
                onDownloadDone(Status.STATUS_FAILED, null)
            }

            WorkInfo.State.BLOCKED -> Log.d(TAG, "Observed block")
            WorkInfo.State.CANCELLED -> {
                Log.d(TAG, "Observed cancel")

                // state
                working = false
                success = false
                cancel = true
                exception = null
                cause = null

                // status
                status = pullStatus()

                // fire notification
                fireNotification(requireContext(), notificationId, Notifier.NotificationType.CANCEL, false)

                // fire onDone
                onDownloadDone(Status.STATUS_CANCELLED, null)
            }
        }
    }

    // C O N T R O L

    /**
     * Work uuid
     */
    @JvmField
    protected var uuid: UUID? = null

    /**
     * Cancel download
     */
    override fun cancel() {
        cancel = true
        if (uuid != null) {
            DownloadWork.Utils.stopWork(requireContext(), uuid!!)
        }
    }

    // S T A T U S

    /**
     * Download status translation to the abstract layer
     *
     * @return (status, progress)
     */
    @Synchronized
    override fun pullStatus(): Status {
        val status = if (cancel) {
            Status.STATUS_CANCELLED
        } else if (working) {
            Status.STATUS_RUNNING
        } else {
            if (success == null) {
                Status.STATUS_PENDING
            } else {
                if (exception == null && success!!) Status.STATUS_SUCCEEDED else Status.STATUS_FAILED
            }
        }
        return status
    }

    /**
     * Download exception reason
     */
    override val reason: String?
        get() {
            if (exception != null) {
                return exception
            }
            return if (cause != null) {
                cause
            } else null
        }

    companion object {

        private const val TAG = "DownloadBaseF"
    }

    /**
     * Event sink for download events fired by downloader
     *
     * @param status download status
     * @param downloadData download data
     */
    @CallSuper
    override fun onDownloadDone(status: Status, downloadData: DownloadData?) {
        super.onDownloadDone(status, downloadData)

        if (status == Status.STATUS_SUCCEEDED && downloadData != null) {
            Settings.recordDatapackSource(requireContext(), downloadData, mode?.toString())
        }
    }
}
