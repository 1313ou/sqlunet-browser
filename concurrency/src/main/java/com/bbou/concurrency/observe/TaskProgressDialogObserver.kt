/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
@file:Suppress("DEPRECATION")

package com.bbou.concurrency.observe

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.util.Log
import com.bbou.concurrency.Cancelable
import com.bbou.concurrency.R
import com.bbou.concurrency.observe.Formatter.formatAsString

/**
 * Task DialogProgress observer
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class TaskProgressDialogObserver<Progress : Pair<Number, Number>>(activity: Activity, title: CharSequence, message: CharSequence) : TaskObserver<Progress> {

    private var task: Cancelable? = null
    private val progressDialog: ProgressDialog

    /**
     * Init
     */
    init {
        progressDialog = makeDialog(activity, title, message)
    }

    override fun taskStart(task: Cancelable) {
        this.task = task
        progressDialog.setMessage("")
        progressDialog.isIndeterminate = true
        progressDialog.show()
    }

    override fun taskProgress(progress: Progress) {
        val longProgress = progress.first.toLong()
        val longLength = progress.second.toLong()
        val indeterminate = longLength == -1L
        progressDialog.isIndeterminate = indeterminate
        if (indeterminate) {
            progressDialog.setProgressNumberFormat(null)
            progressDialog.setProgressPercentFormat(null)
        }
        progressDialog.setMessage(formatAsString(longProgress, longLength, null))
        if (!indeterminate) {
            progressDialog.max = longLength.toInt()
            progressDialog.progress = longProgress.toInt()
        }
    }

    override fun taskUpdate(status: CharSequence) {
        progressDialog.setMessage(status)
    }

    override fun taskFinish(result: Boolean) {
        progressDialog.dismiss()
    }

    /**
     * Sey title
     *
     * @param title title
     * @return task dialog observer
     */
    fun setTitle(title: CharSequence): TaskObserver<Progress> {
        progressDialog.setTitle(title)
        return this
    }

    /**
     * Set message
     *
     * @param message message
     * @return task dialog observer
     */
    fun setMessage(message: CharSequence): TaskObserver<Progress> {
        progressDialog.setMessage(message)
        return this
    }

    /**
     * Make dialog
     *
     * @param activity activity
     * @param title    title
     * @param message  message
     * @return dialog
     */
    private fun makeDialog(activity: Activity, title: CharSequence, message: CharSequence): ProgressDialog {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle(title)
        progressDialog.setMessage(message)
        progressDialog.isIndeterminate = true
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setCancelable(true)
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(R.string.action_dismiss)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getString(R.string.action_abort)) { dialog: DialogInterface, which: Int ->
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                val result = task != null && task!!.cancel(true)
                Log.d(TAG, "Cancel task @" + Integer.toHexString(if (task == null) 0 else task.hashCode()) + ' ' + result)
                dialog.dismiss()
            }
        }
        return progressDialog
    }

    companion object {

        private const val TAG = "TaskDPObserver"
    }
}
