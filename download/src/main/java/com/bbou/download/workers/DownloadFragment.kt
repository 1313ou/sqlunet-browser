/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.workers

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import com.bbou.concurrency.ObservedDelegatingTask
import com.bbou.concurrency.Task
import com.bbou.concurrency.observe.BaseObserver
import com.bbou.concurrency.observe.TaskDialogObserver
import com.bbou.concurrency.observe.TaskObserver
import com.bbou.deploy.workers.Deploy
import com.bbou.deploy.workers.Deploy.emptyDirectory
import com.bbou.deploy.workers.FileTasks
import com.bbou.deploy.workers.MD5
import com.bbou.download.DownloadData
import com.bbou.download.Keys.DOWNLOAD_TO_FILE_ARG
import com.bbou.download.Keys.THEN_UNZIP_TO_ARG
import com.bbou.download.Notifier
import com.bbou.download.common.R
import com.bbou.download.preference.Settings
import com.bbou.download.storage.ReportUtils
import com.bbou.download.workers.core.DownloadWork
import com.bbou.download.workers.utils.MD5Downloader
import java.io.File
import androidx.core.net.toUri

/**
 * Download fragment using DownloadWork.
 * Interface between work and activity.
 * Cancel messages are to be sent to this fragment's receiver.
 * Signals completion through the OnComplete callback in the activity.
 * This fragment uses a file downloader core
 * - end-to-end file downloads with option of md5 checking it
 * - zip expanding it to another location (in settings or files dir by default) if a zip file.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class DownloadFragment : DownloadBaseFragment() {

    // R E S O U R C E S

    /**
     * Layout id
     */
    override val layoutId: Int
        get() = R.layout.fragment_download

    // A R G U M E N T S

    /**
     * Destination file or dir
     */
    private var toFile: File? = null

    /**
     * Unzip dir
     */
    private var unzipDir: File? = null

    override fun unmarshal() {
        super.unmarshal()

        // arguments
        val toFileArg = arguments?.getString(DOWNLOAD_TO_FILE_ARG)
        val unzipToArg = arguments?.getString(THEN_UNZIP_TO_ARG)

        // download dest data
        toFile = if (toFileArg != null) File(toFileArg) else null
        unzipDir = if (unzipToArg != null) File(unzipToArg) else null
    }

    // S E T   D E S T I N A T I O N

    override fun setDestination(view: View) {
        val targetView = view.findViewById<TextView>(R.id.target)
        val targetView2 = view.findViewById<TextView>(R.id.target2)
        val targetView3 = view.findViewById<TextView>(R.id.target3)
        val targetView4 = view.findViewById<TextView>(R.id.target4)
        val targetView5 = view.findViewById<TextView>(R.id.target5)

        if (targetView2 != null && targetView3 != null) {
            val parent = if (toFile != null) toFile!!.parentFile else null
            var left = parent?.parent ?: ""
            left = "$left/"
            var mid = parent?.name ?: ""
            mid = "$mid/"
            targetView.text = left
            targetView2.text = mid
            targetView3.text = toFile?.name ?: ""
        } else {
            targetView.text = toFile?.absolutePath ?: ""
        }
        if (targetView4 != null) {
            val deployTo: CharSequence = if (unzipDir != null) SpannableStringBuilder(getText(R.string.deploy_dest)).append(unzipDir!!.parent).append('/') else ""
            targetView4.text = deployTo
        }
        if (targetView5 != null) {
            targetView5.text = unzipDir?.name ?: ""
        }
    }

    // C O N T R O L

    /**
     * Start download
     */
    override fun start() {
        synchronized(this) {
            Log.d(TAG, "Starting")
            if (!working) // prevent recursion
            {
                // reset
                success = null
                cancel = false
                exception = null
                cause = null

                // args
                val from = downloadUrl!!
                val to = toFile!!.absolutePath

                // start job
                start(from, to)

                // super
                super.start()

                // status
                working = true // set
                return
            }
        }
        throw RuntimeException("Already downloading")
    }

    /**
     * Start work
     *
     * @param fromUrl source zip url
     * @param toFile  destination file
     */
    private fun start(fromUrl: String, toFile: String) {
        uuid = if (useCoroutines)
            com.bbou.download.workers.core.coroutines.DownloadWork.Utils.startWork(appContext, fromUrl, toFile, this, observer)
        else
            DownloadWork.Utils.startWork(appContext, fromUrl, toFile, this, observer)
    }

    // A B S T R A C T

    /**
     * Cleanup download
     */
    override fun cleanup() {}

    /**
     * Deploy
     */
    override fun deploy() {
        // guard against no downloaded file
        if (toFile == null) {
            Log.e(TAG, "Deploy failure: no downloaded file")
            return
        }

        // guard against no unzip dir
        if (unzipDir == null) {
            val datapackDir = Settings.getDatapackDir(appContext)
            unzipDir = if (datapackDir == null) null else File(datapackDir)
        }
        if (unzipDir == null) {
            Log.e(TAG, "Null datapack dir, aborting deployment")
            return
        }

        // log
        Log.d(TAG, "Deploying $toFile to $unzipDir")

        // make sure unzip directory is clean
        emptyDirectory(unzipDir!!)

        // kill request
        if (requestKill != null) {
            requestKill!!.run()
        }

        // observer to proceed with record, cleanup and broadcast on successful task termination
        val unzipObserver: TaskObserver<Pair<Number, Number>> = object : BaseObserver<Pair<Number, Number>>() {
            override fun taskFinish(result: Boolean) {

                super.taskFinish(result)
                if (result) {

                    // record
                    recordTarget()

                    // delete downloaded file
                    if (toFile != null) {
                        // cleanup
                        toFile!!.delete()
                    }

                    // rename
                    if (renameFrom != null && renameTo != null && renameFrom != renameTo) {
                        // rename
                        val renameFromFile = File(unzipDir, renameFrom!!)
                        val renameToFile = File(unzipDir, renameTo!!)
                        val result2 = renameFromFile.renameTo(renameToFile)
                        Log.d(TAG, "Rename $renameFromFile to $renameToFile : $result2")
                    }

                    // new datapack
                    if (requestNew != null) {
                        requestNew!!.run()
                    }
                }

                // signal
                fireComplete(result)
            }
        }
        val baseTask = FileTasks(unzipObserver, null, 1000).unzipFromArchiveFile(unzipDir!!.absolutePath)

        // run task
        val activity: Activity? = activity
        // guard against finished/destroyed activity
        if (activity == null || isDetached || activity.isFinishing || activity.isDestroyed) {
            baseTask.execute(toFile!!.absolutePath)
        } else {
            // augment with a dialog observer if fragment is live
            val fileName = toFile!!.name
            val filePath = toFile!!.absolutePath
            val fatObserver = TaskDialogObserver<Pair<Number, Number>>(parentFragmentManager)
                .setTitle(appContext.getString(R.string.action_unzip_datapack_from_archive))
                .setMessage(fileName)
            val task: Task<String, Pair<Number, Number>, Boolean> = ObservedDelegatingTask(baseTask, fatObserver)
            task.execute(filePath)
        }
    }

    /**
     * MD5 check
     */
    override fun md5() {
        val from = downloadUrl + Deploy.MD5_EXTENSION
        val uri = downloadUrl?.toUri()
        val sourceFile = uri?.lastPathSegment
        val targetFile = if (toFile == null) "?" else toFile!!.name
        MD5Downloader(MD5Downloader.Listener { downloadedResult: String? ->
            val activity = activity
            if (activity == null || isDetached || activity.isFinishing || activity.isDestroyed) {
                return@Listener
            }
            if (downloadedResult == null) {
                AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.action_md5_of_what, targetFile))
                    .setMessage(R.string.status_task_failed)
                    .show()
            } else {
                val localPath = toFile!!.absolutePath
                MD5.md5(activity, localPath) { result: String? ->
                    val activity2: Activity? = getActivity()
                    if (activity2 != null && !isDetached && !activity2.isFinishing && !activity2.isDestroyed) {
                        val success = downloadedResult == result
                        val sb = SpannableStringBuilder()
                        ReportUtils.appendHeader(sb, getString(R.string.md5_downloaded))
                        sb.append('\n')
                        sb.append(downloadedResult)
                        sb.append('\n')
                        ReportUtils.appendHeader(sb, getString(R.string.md5_computed))
                        sb.append('\n')
                        sb.append(result ?: getString(R.string.status_task_failed))
                        sb.append('\n')
                        ReportUtils.appendHeader(sb, getString(R.string.md5_compared))
                        sb.append('\n')
                        sb.append(getString(if (success) R.string.status_task_success else R.string.status_task_failed))
                        AlertDialog.Builder(activity2)
                            .setTitle(getString(R.string.action_md5_of_what, targetFile))
                            .setMessage(sb)
                            .show()
                    }
                }
            }
        }).execute(from to sourceFile!!)
    }

    // E V E N T S

    /**
     * Event sink for download events fired by downloader
     *
     * @param status download status
     * @param downloadData download data
     */
    @CallSuper
    override fun onDownloadDone(status: Status, downloadData: DownloadData?) {

        Log.d(TAG, "OnDone $status")

        // deploy
        val requiresDeploy = unzipDir != null

        // UI
        requireActivity().runOnUiThread {

            endUI(status)

            // md5
            md5Button?.visibility = if (status == Status.STATUS_SUCCEEDED) View.VISIBLE else View.GONE

            // deploy button to complete task
            deployButton?.visibility = if (status == Status.STATUS_SUCCEEDED && requiresDeploy) View.VISIBLE else View.GONE
        }

        // invalidate
        if (status != Status.STATUS_SUCCEEDED) {
            toFile = null
        }

        // record
        if (Status.STATUS_SUCCEEDED == status && !requiresDeploy) {
            recordTarget()
        }

        // super
        super.onDownloadDone(status, downloadData)

        // complete
        if (status != Status.STATUS_SUCCEEDED || !requiresDeploy) {
            fireComplete(status == Status.STATUS_SUCCEEDED)
        }
    }

    // N O T I F I C A T I O N

    /**
     * Fire UI notification
     *
     * @param context        context
     * @param notificationId notification id
     * @param type           notification
     * @param args           arguments
     */
    override fun fireNotification(context: Context, notificationId: Int, type: Notifier.NotificationType, vararg args: Any) {
        val from = downloadUrl?.toUri()?.host
        val to = if (toFile == null) context.getString(R.string.result_deleted) else toFile!!.name
        val contentText = "$from→$to"
        Notifier.fireNotification(context, notificationId, type, contentText, *args)
    }

    companion object {

        /**
         * Log tag
         */
        private const val TAG = "DownloadF"
    }
}
