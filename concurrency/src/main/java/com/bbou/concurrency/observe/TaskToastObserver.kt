/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.concurrency.observe

import android.content.Context
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.bbou.concurrency.Cancelable
import com.bbou.concurrency.R

/**
 * Task toast observer
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class TaskToastObserver<Progress : Pair<Number, Number>>(private val appContext: Context) : TaskObserver<Progress> {

    override fun taskStart(task: Cancelable) {
        Toast.makeText(appContext, R.string.status_task_start_toast, Toast.LENGTH_SHORT).show()
    }

    override fun taskFinish(result: Boolean) {
        Toast.makeText(appContext, R.string.status_task_done_toast, Toast.LENGTH_SHORT).show()
    }

    override fun taskProgress(progress: Progress) {
    }

    override fun taskUpdate(status: CharSequence) {
    }

    /**
     * Toast + TextView observer
     */
    @Suppress("unused")
    class WithStatus<Progress : Pair<Number, Number>>(appContext: Context, private val status: TextView) : TaskToastObserver<Progress>(appContext) {

        override fun taskStart(task: Cancelable) {
            super.taskStart(task)
            status.setText(R.string.status_task_running)
        }

        override fun taskUpdate(status: CharSequence) {
            super.taskUpdate(status)
            this.status.text = status
        }

        override fun taskFinish(result: Boolean) {
            super.taskFinish(result)
            status.setText(if (result) R.string.status_task_done else R.string.status_task_failed)
        }
    }

    /**
     * Toast + Progress bar observer
     */
    @Suppress("unused")
    class WithProgress<Progress : Pair<Number, Number>>(appContext: Context, private val progress: ProgressBar) : TaskToastObserver<Progress>(appContext) {

        override fun taskStart(task: Cancelable) {
            super.taskStart(task)
            progress.isIndeterminate = true
        }

        override fun taskProgress(progress: Progress) {
            super.taskProgress(progress)
            val indeterminate = progress.second.toLong() == -1L
            this.progress.isIndeterminate = indeterminate
            if (!indeterminate) {
                this.progress.max = progress.second.toInt()
                this.progress.progress = progress.first.toInt()
            }
        }

        override fun taskFinish(result: Boolean) {
            super.taskFinish(result)
            val done = if (result) 0 else 100
            progress.max = done
            progress.progress = done
        }
    }
}
