/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.coroutines.observe

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bbou.coroutines.R
import com.bbou.coroutines.Task
import com.bbou.coroutines.observe.Formatter.formatAsString
import java.util.function.Consumer

/**
 * Task dialog observer
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class TaskDialogObserver<Progress : Pair<Number, Number>>(private val fragmentManager: FragmentManager, task: Task<out Any, out Any, out Any?>) : Consumer<Progress> {

    private val progressDialogFragment: ProgressDialogFragment = ProgressDialogFragment(task)

    /**
     * Consumer accept
     *
     * @param progress observed progress
     */
    override fun accept(progress: Progress) {
        val unit: String? = null
        val longProgress = progress.first.toLong()
        val longLength = progress.second.toLong()
        val indeterminate = longLength == -1L

        // progress string
        if (progressDialogFragment.progressTextView != null) {
            progressDialogFragment.progressTextView!!.text = formatAsString(longProgress, longLength, unit)
        }

        // progress
        if (progressDialogFragment.progressBar != null) {
            progressDialogFragment.progressBar!!.isIndeterminate = indeterminate
            if (!indeterminate) {
                val percent = (longProgress * 100f / longLength).toInt()
                progressDialogFragment.progressBar!!.max = 100
                progressDialogFragment.progressBar!!.progress = percent
            }
        }
    }

    /**
     * Show dialog
     */
    fun show(): TaskDialogObserver<Progress> {
        if (!fragmentManager.isDestroyed) {
            progressDialogFragment.show(fragmentManager, "tag")
        }
        return this
    }

    /**
     * Dismiss dialog
     *
     * @param result observed result
     */
    fun dismiss(result: Boolean) {
        if (progressDialogFragment.isAdded && result) {
            progressDialogFragment.dismissAllowingStateLoss()
        }
    }

    /**
     * Set status
     */
    fun setStatus(status: CharSequence): TaskDialogObserver<Progress> {
        if (progressDialogFragment.statusTextView != null) {
            progressDialogFragment.statusTextView!!.text = status
        }
        return this
    }

    /**
     * Set title
     */
    fun setTitle(title: CharSequence): TaskDialogObserver<Progress> {
        progressDialogFragment.title = title
        return this
    }

    /**
     * Set message
     */
    fun setMessage(message: CharSequence): TaskDialogObserver<Progress> {
        progressDialogFragment.message = message
        return this
    }

    /**
     * Dialog fragment
     */
    class ProgressDialogFragment(private val task: Task<out Any, out Any, out Any?>) : DialogFragment() {

        private lateinit var titleTextView: TextView

        private lateinit var messageTextView: TextView

        /**
         * Component that holds title
         */
        lateinit var title: CharSequence

        /**
         * Component that holds message
         */
        lateinit var message: CharSequence

        /**
         * Component that holds progress
         */
        var progressTextView: TextView? = null

        /**
         * Component that holds progress bar
         */
        var progressBar: ProgressBar? = null

        /**
         * Component that holds
         */
        var statusTextView: TextView? = null

        /**
         * onCreate
         *
         * @param savedInstanceState saved instance state
         */
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            Log.d(TAG, "onCreate")
        }

        /**
         * onViewCreated
         *
         * @param view view
         * @param savedInstanceState saved instance state
         */
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            Log.d(TAG, "onViewCreated")
        }

        /**
         * onCreateDialog
         *
         * @param savedInstanceState saved instance state
         * @return dialog
         */
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val activity: Activity = requireActivity()
            val inflater = activity.layoutInflater
            val view = inflater.inflate(R.layout.dialog_progress, null)
            progressBar = view.findViewById(R.id.progressBar)
            progressTextView = view.findViewById(R.id.progressProgress)
            statusTextView = view.findViewById(R.id.status)
            titleTextView = view.findViewById(R.id.title)
            messageTextView = view.findViewById(R.id.message)
            titleTextView.text = title
            messageTextView.text = message

            val builder = AlertDialog.Builder(activity)
            builder.setView(view)
            builder.setNegativeButton(R.string.action_cancel) { _: DialogInterface?, _: Int ->

                // cancelled.
                val result = task.cancel()
                Log.d(TAG, "Cancel task @" + (Integer.toHexString(task.hashCode())) + ' ' + result)
                dismiss()
            }
            return builder.create()
        }
    }

    companion object {

        private const val TAG = "DialogObserver"
    }
}
