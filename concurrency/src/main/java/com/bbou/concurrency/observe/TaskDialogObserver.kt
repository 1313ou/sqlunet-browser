/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.concurrency.observe

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
import com.bbou.concurrency.Cancelable
import com.bbou.concurrency.R
import com.bbou.concurrency.observe.Formatter.formatAsString

/**
 * Task dialog observer
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class TaskDialogObserver<Progress : Pair<Number, Number>>(private val fragmentManager: FragmentManager) : TaskObserver<Progress> {

    private val progressDialogFragment: ProgressDialogFragment = ProgressDialogFragment.make()

    override fun taskStart(task: Cancelable) {
        if (!fragmentManager.isDestroyed) {
            progressDialogFragment.setTask(task)
            progressDialogFragment.show(fragmentManager, "tag")
        }
    }

    override fun taskProgress(progress: Progress) {
        val longProgress = progress.first.toLong()
        val longLength = progress.second.toLong()
        val indeterminate = longLength == -1L

        // progress string
        if (progressDialogFragment.progressTextView != null) {
            progressDialogFragment.progressTextView!!.text = formatAsString(longProgress, longLength, null)
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

    override fun taskUpdate(status: CharSequence) {
        if (progressDialogFragment.statusTextView != null) {
            progressDialogFragment.statusTextView!!.text = status
        }
    }

    override fun taskFinish(result: Boolean) {
        if (progressDialogFragment.isAdded && result) {
            progressDialogFragment.dismissAllowingStateLoss()
        }
    }

    /**
     * Sey title
     *
     * @param title title
     * @return task dialog observer
     */
    fun setTitle(title: CharSequence): TaskDialogObserver<Progress> {
        progressDialogFragment.setTitle(title)
        return this
    }

    /**
     * Set message
     *
     * @param message message
     * @return task dialog observer
     */
    fun setMessage(message: CharSequence): TaskDialogObserver<Progress> {
        progressDialogFragment.setMessage(message)
        return this
    }

    /**
     * Dialog fragment
     */
    class ProgressDialogFragment : DialogFragment() {

        /**
         * Progress text view
         */
        var progressTextView: TextView? = null

        /**
         * Progress bar
         */
        var progressBar: ProgressBar? = null

        /**
         * Status text view
         */
        var statusTextView: TextView? = null

        private lateinit var titleTextView: TextView
        private lateinit var messageTextView: TextView
        private lateinit var title: CharSequence
        private lateinit var message: CharSequence
        private lateinit var task: Cancelable

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
         * @param view
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
                val result = task.cancel(true)
                Log.d(TAG, "Cancel task @" + (Integer.toHexString(task.hashCode())) + ' ' + result)
                dismiss()
            }
            return builder.create()
        }

        /**
         * Set task
         */
        fun setTask(task: Cancelable) {
            this.task = task
        }

        /**
         * Set title
         */
        fun setTitle(title: CharSequence) {
            this.title = title
        }

        /**
         * Set message
         */
        fun setMessage(message: CharSequence) {
            this.message = message
        }

        companion object {

            /**
             * Make dialog fragment
             *
             * @return dialog
             */
            fun make(): ProgressDialogFragment {
                return ProgressDialogFragment()
            }
        }
    }

    companion object {

        private const val TAG = "TaskDObserver"
    }
}
