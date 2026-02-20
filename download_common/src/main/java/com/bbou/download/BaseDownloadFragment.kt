/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bbou.download.Keys.CANCEL_BTN_STATE
import com.bbou.download.Keys.DEPLOY_BTN_STATE
import com.bbou.download.Keys.DOWNLOAD_BTN_BACKGROUND_STATE
import com.bbou.download.Keys.DOWNLOAD_BTN_IMAGE_STATE
import com.bbou.download.Keys.DOWNLOAD_BTN_STATE
import com.bbou.download.Keys.DOWNLOAD_FROM_ARG
import com.bbou.download.Keys.DOWNLOAD_TARGET_FILE_ARG
import com.bbou.download.Keys.MD5_BTN_STATE
import com.bbou.download.Keys.PROGRESS_STATE
import com.bbou.download.Keys.PROGRESS_STATUS_STATE
import com.bbou.download.Keys.RENAME_FROM_ARG
import com.bbou.download.Keys.RENAME_TO_ARG
import com.bbou.download.Notifier.Companion.ACTION_DOWNLOAD_CANCEL
import com.bbou.download.common.R
import com.bbou.download.preference.Settings
import com.bbou.download.storage.FormatUtils.formatAsInformationString
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates

/**
 * Base download fragment that handles
 * - common arguments
 * - status including progress
 * - UI components
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class BaseDownloadFragment : Fragment() {

    // C O N T E X T

    protected lateinit var appContext: Context

    // S T A T U S

    /**
     * Whether one download is in progress
     */
    protected var isDownloading = AtomicBoolean(false)

    /**
     * Status, describes download status
     */
    enum class Status(
        /**
         * String resource id
         */
        val res: Int,
    ) {

        /**
         * Pending
         */
        STATUS_PENDING(R.string.status_download_pending),

        /**
         * Running
         */
        STATUS_RUNNING(R.string.status_download_running),

        /**
         * Paused
         */
        STATUS_PAUSED(R.string.status_download_paused),

        /**
         * Succeeded
         */
        STATUS_SUCCEEDED(R.string.status_download_successful),

        /**
         * Failed
         */
        STATUS_FAILED(R.string.status_download_fail),

        /**
         * Cancelled
         */
        STATUS_CANCELLED(R.string.status_download_cancelled);

        /**
         * Make status string
         *
         * @param context context
         * @return status string
         */
        fun toString(context: Context): String {
            return context.getString(res)
        }
    }

    /**
     * Id for the current notification
     */
    var notificationId = Notifier.notificationId()

    // I N  D A T A

    /**
     * Status
     */
    protected var status: Status? = null

    /**
     * Progress
     */
    protected var progress: Pair<Long, Long>? = 0L to 0L

    // A R G S

    /**
     * Download uri
     */
    protected var downloadUrl: String? = null

    /**
     * Rename source
     */
    protected var renameFrom: String? = null

    /**
     * Rename destination
     */
    protected var renameTo: String? = null

    /**
     * Mode
     */
    protected var mode: Settings.Mode? = null

    /**
     * Unmarshal arguments
     */
    protected open fun unmarshal() {

        // mode
        val modeStr = arguments?.getString(Keys.DOWNLOAD_MODE_ARG)
        mode = modeStr?.let { Settings.Mode.valueOf(modeStr) }

        // download source arg
        downloadUrl = arguments?.getString(DOWNLOAD_FROM_ARG)
        if (downloadUrl == null || downloadUrl!!.isEmpty()) {
            val message = appContext.getString(R.string.status_download_error_null_download_url)
            warn(message)
        }

        // rename
        renameFrom = arguments?.getString(RENAME_FROM_ARG)
        renameTo = arguments?.getString(RENAME_TO_ARG)
    }

    // W I D G E T S

    /**
     * Progress bar
     */
    private lateinit var progressBar: ProgressBar

    /**
     * Progress status view
     */
    private lateinit var progressStatus: TextView

    /**
     * Status view
     */
    private lateinit var statusTextView: TextView

    /**
     * Cancel downloads button
     */
    private lateinit var cancelButton: Button

    /**
     * MD5 button
     */
    protected var deployButton: Button? = null

    /**
     * MD5 button
     */
    protected var md5Button: Button? = null

    /**
     * Download button
     */
    private lateinit var downloadButton: ImageButton

    // R E S O U R C E S

    /**
     * Layout id
     */
    abstract val layoutId: Int

    /**
     * Saved resource id of image applied to download button
     */
    private var downloadButtonImageResId by Delegates.notNull<Int>()

    /**
     * Saved resource id of background applied to download button
     */
    private var downloadButtonBackgroundResId by Delegates.notNull<Int>()

    // L I F E C Y C L E

    /**
     * onCreate
     *
     * @param savedInstanceState saved instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // store app context
        appContext = requireContext().applicationContext

        // unmarshal arguments
        unmarshal()

        // cancel receiver
        Log.d(TAG, "Register cancel receiver")
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_DOWNLOAD_CANCEL)
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        ContextCompat.registerReceiver(appContext, cancelReceiver, intentFilter, ContextCompat.RECEIVER_NOT_EXPORTED)

        // notifications
        Notifier.initChannels(appContext)
    }

    /**
     * onCreateView
     *
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState saved instance state
     * @return
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    /**
     * onViewCreated
     *
     * @param view view
     * @param savedInstanceState saved instance state
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // components
        progressBar = view.findViewById(R.id.progressBar)
        progressStatus = view.findViewById(R.id.progressStatus)
        statusTextView = view.findViewById(R.id.status)

        // default button resources
        downloadButtonImageResId = R.drawable.bn_download
        downloadButtonBackgroundResId = R.drawable.bg_button_action

        // buttons
        downloadButton = view.findViewById(R.id.downloadButton)
        cancelButton = view.findViewById(R.id.cancelButton)
        deployButton = view.findViewById(R.id.deployButton)
        md5Button = view.findViewById(R.id.md5Button)

        // click listeners
        downloadButton.setOnClickListener {
            val wasNotDownloading = isDownloading.getAndSet(true)
            if (wasNotDownloading) {
                onDownloadStart()
                startUI()
                try {
                    start()
                } catch (e: NullPointerException) {
                    Log.e(TAG, "While starting", e)
                    warn(getString(R.string.status_download_null_exception))
                    onDownloadDone(Status.STATUS_FAILED, null)
                    isDownloading.set(false)
                } catch (e: Exception) {
                    Log.e(TAG, "While starting", e)
                    val message = e.message
                    warn(message ?: e.javaClass.name)
                    onDownloadDone(Status.STATUS_FAILED, null)
                    isDownloading.set(false)
                }
            }
        }
        cancelButton.setOnClickListener {
            cancelButton.visibility = View.INVISIBLE
            cancel()
            isDownloading.set(false)
        }
        deployButton?.setOnClickListener { deploy() }
        md5Button?.setOnClickListener { md5() }

        // source textviews
        setSource(view)

        // destination
        setDestination(view)

        // status
        statusTextView.text = ""

        // init
        if (savedInstanceState === null) {
            initUI()
        }
    }

    /**
     * Called when all saved state has been restored into the view hierarchy
     * of the fragment.  This can be used to do initialization based on saved
     * state that you are letting the view hierarchy track itself, such as
     * whether check box widgets are currently checked.  This is called
     * after [.onViewCreated] and before [.onStart].
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {

            downloadButton.visibility = savedInstanceState.getInt(DOWNLOAD_BTN_STATE, View.VISIBLE)
            downloadButtonImageResId = savedInstanceState.getInt(DOWNLOAD_BTN_IMAGE_STATE, R.drawable.bn_download)
            val drawable = AppCompatResources.getDrawable(requireContext(), downloadButtonImageResId)!!
            downloadButton.setImageDrawable(drawable)
            downloadButtonBackgroundResId = savedInstanceState.getInt(DOWNLOAD_BTN_BACKGROUND_STATE, R.drawable.bg_button_action)
            downloadButton.setBackgroundResource(downloadButtonBackgroundResId)

            progressBar.visibility = savedInstanceState.getInt(PROGRESS_STATE, View.INVISIBLE)
            progressStatus.visibility = savedInstanceState.getInt(PROGRESS_STATUS_STATE, View.INVISIBLE)

            cancelButton.visibility = savedInstanceState.getInt(CANCEL_BTN_STATE, View.INVISIBLE)
            deployButton?.visibility = savedInstanceState.getInt(DEPLOY_BTN_STATE, View.INVISIBLE)
            md5Button?.visibility = savedInstanceState.getInt(MD5_BTN_STATE, View.INVISIBLE)
        }
    }

    /**
     * onSaveInstanceState
     *
     * @param outState outgoing saved state
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(DOWNLOAD_BTN_STATE, downloadButton.visibility)
        outState.putInt(DOWNLOAD_BTN_IMAGE_STATE, downloadButtonImageResId)
        outState.putInt(DOWNLOAD_BTN_BACKGROUND_STATE, downloadButtonBackgroundResId)
        outState.putInt(CANCEL_BTN_STATE, cancelButton.visibility)
        deployButton?.visibility?.let { outState.putInt(DEPLOY_BTN_STATE, it) }
        md5Button?.visibility?.let { outState.putInt(MD5_BTN_STATE, it) }
        outState.putInt(PROGRESS_STATE, progressBar.visibility)
        outState.putInt(PROGRESS_STATUS_STATE, progressStatus.visibility)
    }

    /**
     * onDestroy
     */
    override fun onDestroy() {
        super.onDestroy()

        // main receiver
        Log.d(TAG, "Unregister cancel receiver")
        appContext.unregisterReceiver(cancelReceiver)
    }

    // C A N C E L   R E C E I V E R

    /**
     * Broadcast receiver for cancel events
     */
    private val cancelReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "Received broadcast request")
            val action = intent.action
            if (ACTION_DOWNLOAD_CANCEL == action) {
                Log.d(TAG, "Received cancel broadcast request")

                // do
                cancel()
            }
        }
    }

    // O P E R A T I O N S

    /**
     * Start download
     */
    protected abstract fun start()

    /**
     * Cancel download
     */
    protected abstract fun cancel()

    /**
     * Deploy tail operation
     *
     * @noinspection EmptyMethod
     */
    protected abstract fun deploy()

    /**
     * Md5 operation
     */
    protected abstract fun md5()

    /**
     * Cleanup after download
     *
     * @noinspection EmptyMethod
     */
    @Suppress("EmptyMethod")
    protected abstract fun cleanup()

    // S T A T U S

    /**
     * Get reason
     *
     * @return reason
     */
    abstract val reason: String?

    /**
     * Build status message
     *
     * @param status status
     * @return message
     */
    private fun buildStatusString(status: Status?): String {
        val statusStr = status?.toString(appContext) ?: "unknown"
        if (status != Status.STATUS_SUCCEEDED) {
            if (reason != null) {
                return "$statusStr\n$reason"
            }
        }
        return statusStr
    }

    // U I   H E L P E R S

    /**
     * Initial UI state
     */
    @UiThread
    private fun initUI() {
        if (!isDownloading.get()) {
            downloadButton.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
            progressStatus.visibility = View.INVISIBLE
            cancelButton.visibility = View.GONE
        } else {
            downloadButton.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            progressStatus.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE
        }
    }

    /**
     * Start UI state
     */
    @UiThread
    private fun startUI() {
        // buttons
        downloadButton.isEnabled = false
        downloadButton.visibility = View.INVISIBLE
        cancelButton.visibility = View.VISIBLE

        // progress
        progressBar.visibility = View.VISIBLE
        progressStatus.visibility = View.VISIBLE

        // status
        statusTextView.text = ""
    }

    /**
     * End UI state
     *
     * @param status status
     */
    @UiThread
    protected fun endUI(status: Status) {
        Log.d(TAG, "End UI $status")

        // progress
        progressBar.visibility = View.INVISIBLE
        progressStatus.visibility = View.INVISIBLE

        // status
        val message = buildStatusString(status)
        statusTextView.text = message
        statusTextView.visibility = View.VISIBLE

        // buttons
        downloadButtonImageResId = if (status == Status.STATUS_SUCCEEDED) R.drawable.bn_download_ok else R.drawable.bn_download
        val drawable = AppCompatResources.getDrawable(requireContext(), downloadButtonImageResId)!!
        downloadButton.setImageDrawable(drawable)
        downloadButtonBackgroundResId = if (status == Status.STATUS_SUCCEEDED) R.drawable.bg_button_ok else R.drawable.bg_button_err
        downloadButton.setBackgroundResource(downloadButtonBackgroundResId)
        downloadButton.isEnabled = false
        downloadButton.visibility = View.VISIBLE

        cancelButton.visibility = View.GONE
    }

    /**
     * Update UI
     */
    @UiThread
    protected fun updateUI() {

        val progress100 = if (progress!!.second == 0L) -1 else (progress!!.first * 100L / progress!!.second).toInt()
        val count = "$progress100%   ${formatAsInformationString(progress!!.first)}"
        val message = buildStatusString(status)
        Log.d(TAG, "Update UI $count, $message")
        progressBar.visibility = View.VISIBLE
        progressStatus.visibility = View.VISIBLE
        progressBar.progress = progress100
        progressStatus.text = count
        statusTextView.text = message
    }

    /**
     * Set source
     * @param view view
     */
    @UiThread
    private fun setSource(view: View) {
        val srcView = view.findViewById<TextView>(R.id.src)
        val srcView2 = view.findViewById<TextView>(R.id.src2)
        val srcView3 = view.findViewById<TextView>(R.id.src3)
        if (srcView2 != null && srcView3 != null) {
            try {
                val uri = downloadUrl?.toUri()
                var host = uri?.host
                val port = uri?.port
                if (port != -1) {
                    host += ":$port"
                }
                val file = uri?.lastPathSegment
                var path = uri?.path
                if (path != null && file != null) {
                    path = path.take(path.lastIndexOf(file))
                }
                srcView3.text = host
                srcView2.text = path
                srcView.text = file
            } catch (_: Exception) {
                srcView3.setText(R.string.status_error_invalid)
                srcView2.setText(R.string.status_error_invalid)
                srcView.setText(R.string.status_error_invalid)
            }
        } else {
            srcView.text = downloadUrl
        }
    }

    /**
     * Set destination
     * @param view view
     */
    @UiThread
    protected abstract fun setDestination(view: View)

    /**
     * Warn
     */
    private fun warn(message: CharSequence) {
        val activity: Activity? = activity
        if (activity != null && !isDetached && !activity.isFinishing && !activity.isDestroyed) {
            activity.runOnUiThread { Toast.makeText(activity, message, Toast.LENGTH_LONG).show() }
        }
    }

    // R E C O R D

    /**
     * Record target.
     * Target is either the downloaded file or the file contained in it and deployed from it.
     */
    protected fun recordTarget() {
        val arguments = arguments
        if (arguments != null) {
            val target = arguments.getString(DOWNLOAD_TARGET_FILE_ARG)
            if (target != null) {
                Settings.recordDatapackFile(appContext, File(target))
                Log.d(TAG, "Recorded $target")
            }
        }
    }

    // B R O A D C A S T

    /**
     * New data available request runnable, run when new data becomes available
     */
    var requestNew: Runnable? = null

    /**
     * Kill old data request runnable, run when old data should be discarded
     */
    var requestKill: Runnable? = null

    // E V E N T S

    /**
     * Event sink for download events fired by downloader
     */
    @Suppress("EmptyMethod")
    @CallSuper
    open fun onDownloadStart() {
    }

    /**
     * Event sink for download events fired by downloader
     *
     * @param status download status
     * @param downloadData download data
     */
    @CallSuper
    open fun onDownloadDone(status: Status, downloadData: DownloadData?) {
    }

    /**
     * Fire completion events to activity listener
     *
     * @param success whether download and deployment were successful
     */
    @CallSuper
    fun fireComplete(success: Boolean) {
        Log.d(TAG, "OnComplete succeeded=$success $this")
        val listener = (activity as CompletionListener?)!!
        listener.onComplete(success)
    }

    companion object {

        private const val TAG = "BaseDownloadF"
    }
}
