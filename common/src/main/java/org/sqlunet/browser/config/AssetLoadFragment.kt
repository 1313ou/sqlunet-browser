/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bbou.concurrency.Cancelable
import com.bbou.concurrency.observe.TaskObserver
import com.bbou.download.storage.FormatUtils.formatAsInformationString
import org.sqlunet.browser.AppContext
import org.sqlunet.browser.EntryActivity.Companion.rerun
import org.sqlunet.browser.common.R
import org.sqlunet.browser.config.SetupAsset.deliverAsset
import org.sqlunet.settings.Settings

/**
 * About fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class AssetLoadFragment : Fragment(), TaskObserver<Pair<Number, Number>> {

    private var titleTextView: TextView? = null
    private var messageTextView: TextView? = null
    private var progressBar: ProgressBar? = null
    private var progressTextView: TextView? = null
    private var statusTextView: TextView? = null
    private var cancelButton: Button? = null
    private var task: Cancelable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_assetload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // find view components to control loading
        titleTextView = view.findViewById(R.id.title)
        messageTextView = view.findViewById(R.id.message)
        statusTextView = view.findViewById(R.id.status)
        progressBar = view.findViewById(R.id.progressBar)
        progressTextView = view.findViewById(R.id.progressProgress)
        cancelButton = view.findViewById(R.id.cancelButton)
        cancelButton!!.setOnClickListener {
            val result = task != null && task!!.cancel(true)
            Log.d(TAG, "Cancel task @" + (if (task == null) "null" else Integer.toHexString(task.hashCode())) + ' ' + result)
        }

        // load assets
        val asset = Settings.getAssetPack(AppContext.context)
        val assetDir = Settings.getAssetPackDir(AppContext.context)
        val assetZip = Settings.getAssetPackZip(AppContext.context)
        val assetZipEntry = AppContext.context.getString(R.string.asset_zip_entry)
        val whenComplete = Runnable {

            // avoid IllegalStateException on completion
            val context2 = context
            if (context2 != null) {
                rerun(context2)
            }
        }
        deliverAsset(asset, assetDir, assetZip, assetZipEntry, requireActivity(), this, whenComplete, getView())
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as AppCompatActivity
        val actionBar = activity.supportActionBar!!
        actionBar.customView = null
        actionBar.setBackgroundDrawable(null)
    }

    override fun taskStart(task: Cancelable) {
        this.task = task
        cancelButton!!.visibility = View.VISIBLE
        progressBar!!.isIndeterminate = true
        progressTextView!!.text = ""
        messageTextView!!.text = ""
    }

    override fun taskFinish(result: Boolean) {
        task = null
        cancelButton!!.visibility = View.GONE
        progressBar!!.isIndeterminate = false
        progressBar!!.max = 100
        progressBar!!.progress = 100
        messageTextView!!.setText(if (result) R.string.result_success else R.string.result_fail)
    }

    override fun taskProgress(progress: Pair<Number, Number>) {
        val longProgress = progress.first.toLong()
        val longLength = progress.second.toLong()
        val indeterminate = longLength == -1L
        progressTextView!!.text = formatAsInformationString(longProgress) + '/' + formatAsInformationString(longLength)
        progressBar!!.isIndeterminate = indeterminate
        if (!indeterminate) {
            val percent = (longProgress * 100f / longLength).toInt()
            progressBar!!.max = 100
            progressBar!!.progress = percent
        }
    }

    override fun taskUpdate(status: CharSequence) {
        statusTextView!!.text = status
    }

    fun setTitle(title: CharSequence): TaskObserver<Pair<Number, Number>> {
        titleTextView!!.text = title
        return this
    }

    fun setMessage(message: CharSequence): TaskObserver<Pair<Number, Number>> {
        messageTextView!!.text = message
        return this
    }

    companion object {

        private const val TAG = "AssetF"
    }
}
