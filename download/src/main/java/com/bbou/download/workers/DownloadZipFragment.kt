/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.workers

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import com.bbou.deploy.workers.Deploy
import com.bbou.download.DownloadData
import com.bbou.download.Keys.DOWNLOAD_ENTRY_ARG
import com.bbou.download.Keys.DOWNLOAD_TO_DIR_ARG
import com.bbou.download.Notifier
import com.bbou.download.common.R
import com.bbou.download.workers.core.DownloadZipWork
import java.io.File
import androidx.core.net.toUri

/**
 * Download fragment using DownloadZipWork
 * Interface between work and activity.
 * Cancel messages are to be sent to this fragment's receiver.
 * Signals completion through the OnComplete callback in the activity.
 * This fragment uses a zipped file zip downloader core (only matched
 * entries or all by default) are written to target directory location.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class DownloadZipFragment : DownloadBaseFragment() {

    // R E S O U R C E S

    /**
     * Layout id
     */
    override val layoutId: Int
        get() = R.layout.fragment_zip_download

    // A R G U M E N T S

    /**
     * Download source entry
     */
    private var sourceEntry: String? = null

    /**
     * Destination dir
     */
    private var toDir: File? = null

    override fun unmarshal() {
        super.unmarshal()

        // arguments
        val toDirArg = arguments?.getString(DOWNLOAD_TO_DIR_ARG)
        val sourceEntryArg = arguments?.getString(DOWNLOAD_ENTRY_ARG)
        toDir = if (toDirArg != null) File(toDirArg) else null
        sourceEntry = sourceEntryArg
        if (!downloadUrl!!.endsWith(Deploy.ZIP_EXTENSION)) {
            downloadUrl += Deploy.ZIP_EXTENSION
        }
    }

    // S E T   D E S T I N A T I O N

    override fun setDestination(view: View) {
        val targetView = view.findViewById<TextView>(R.id.target)
        val targetView2 = view.findViewById<TextView>(R.id.target2)
        val targetView3 = view.findViewById<TextView>(R.id.target3)
        val targetView4 = view.findViewById<TextView>(R.id.target4)
        val targetView5 = view.findViewById<TextView>(R.id.target5)

        targetView.text = toDir?.absolutePath ?: ""

        if (targetView2 != null) {
            val selectEntry: CharSequence = SpannableStringBuilder(getText(R.string.select_zip_entry)).append(sourceEntry ?: "*")
            targetView2.text = selectEntry
        }
        if (targetView3 != null) {
            targetView3.text = ""
        }
        if (targetView4 != null) {
            val from: CharSequence = if (renameFrom != null) SpannableStringBuilder(getText(R.string.rename_source)).append(renameFrom) else ""
            targetView4.text = from
        }
        if (targetView5 != null) {
            val to: CharSequence = if (renameTo != null) SpannableStringBuilder(getText(R.string.rename_dest)).append(renameTo) else ""
            targetView5.text = to
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
                val to = toDir!!.absolutePath
                val entry = sourceEntry

                // start job
                start(from, entry, to, renameFrom, renameTo)

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
     * @param entry source zip entry
     * @param toDir destination dir
     * @param renameFrom rename source
     * @param renameTo rename dest
     */
    private fun start(fromUrl: String, entry: String?, toDir: String, renameFrom: String?, renameTo: String?) {
        uuid = if (useCoroutines)
            com.bbou.download.workers.core.coroutines.DownloadZipWork.Utils.startWork(requireContext(), fromUrl, entry, toDir, renameFrom, renameTo, this, observer)
        else
            DownloadZipWork.Utils.startWork(requireContext(), fromUrl, entry, toDir, renameFrom, renameTo, this, observer)
    }

    // A B S T R A C T

    override fun deploy() {}
    override fun md5() {}
    override fun cleanup() {}

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

        // UI
        requireActivity().runOnUiThread { endUI(status) }

        // success
        if (status == Status.STATUS_SUCCEEDED) {
            // kill request
            if (requestKill != null) {
                requestKill!!.run()
            }

            // new datapack
            if (requestNew != null) {
                requestNew!!.run()
            }
        }

        // record
        if (Status.STATUS_SUCCEEDED == status) {
            recordTarget()
        }

        // super
        super.onDownloadDone(status, downloadData)

        // complete
        fireComplete(status == Status.STATUS_SUCCEEDED)
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
        val to = toDir!!.name
        val contentText = "$from→$to"
        Notifier.fireNotification(context, notificationId, type, contentText, *args)
    }

    companion object {

        private const val TAG = "ZipDownloadF"
    }
}
