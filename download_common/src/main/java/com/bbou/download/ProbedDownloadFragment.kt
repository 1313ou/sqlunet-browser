/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download

import android.app.Activity
import android.util.Log
import androidx.annotation.CallSuper

/**
 * Probed download fragment
 * The core download fragment is probed periodically by a recurrent thread.
 * Status and progress are pulled.
 * UI is updated
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class ProbedDownloadFragment : BaseDownloadFragment() {

    /**
     * onResume
     */
    override fun onResume() {
        super.onResume()
        if (isDownloading.get()) {
            startObserve()
        }
    }

    /**
     * onPause
     */
    override fun onPause() {
        super.onPause()
        stopObserve()
    }

    /**
     * Start download
     */
    @CallSuper
    override fun start() {
        startObserve()
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
        stopObserve()
    }

    /**
     * Pull status
     *
     * @return status
     */
    abstract fun pullStatus(): Status

    /**
     * ProbeObserver
     */
    internal inner class ProbeObserver : Runnable {

        var stop = false
        override fun run() {

            Log.d(TAG, "Probe observer is alive")
            while (true) {
                // terminate observer if fragment is not in resumed state
                if (!isResumed) {
                    break
                }

                // status pulled every probe cycle
                status = pullStatus()

                Log.d(TAG, "Probe observer got status $status")

                // exit because task has ended
                if (status == Status.STATUS_FAILED) {
                    cleanup()
                    break
                }
                if (status == Status.STATUS_SUCCEEDED) {
                    break
                }

                // exit because task was cancelled
                if (stop) {
                    break
                }

                // observer update UI
                val activity: Activity? = activity
                if (activity != null && !isDetached && !activity.isFinishing && !activity.isDestroyed) {
                    activity.runOnUiThread(Runnable { updateUI() })
                }

                // sleep
                try {
                    Thread.sleep(TIMELAPSE)
                } catch (_: InterruptedException) {
                }
            }
            Log.d(TAG, "Probe observer dies")
        }
    }

    /**
     * ProbeObserver
     */
    private var probeObserver: ProbeObserver? = null

    /**
     * Start probe observer thread
     */
    private fun startObserve() {
        Log.d(TAG, "Start probe observer")
        probeObserver = ProbeObserver()
        Thread(probeObserver).start()
    }

    /**
     * Stop probe observer thread
     */
    private fun stopObserve() {
        if (probeObserver != null) {
            Log.d(TAG, "Stop probe observer")
            probeObserver!!.stop = true
        }
    }

    companion object {

        private const val TAG = "ProbedDownloadF"

        private const val TIMELAPSE = 3000L
    }
}
