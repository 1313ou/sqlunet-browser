/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.concurrency

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread

/**
 * Abstract forwardable asynchronous task.
 * Has a build in receiver to handle result and progress on the main thread.
 * The latter events can be forwarded to a delegating task.
 */
abstract class Task<Params, Progress, Result> protected constructor() : BaseTask<Params, Progress, Result>() {

    // F O R W A R D

    /**
     * Task to forward progress abd result to. Null is interpreted as this task.
     */
    private var forward: Task<Params, Progress, Result>?

    // C O N S T R U C T

    init {
        forward = null
    }

    // D E L E G A T I O N

    /**
     * Set this task as delegate and forward termination and progress signals to delegating wrapping task
     *
     * @param delegating delegating task
     */
    fun setForward(delegating: Task<Params, Progress, Result>) {
        forward = delegating
    }

    // P U B L I S H    R E S U L T   A N D   P R O G R E S S

    /**
     * Call this function to signal termination and result.
     * This is called on the worker thread.
     * It must ensure onPostExecute is called on the main thread.
     */
    @WorkerThread
    @CallSuper
    override fun postResult(result: Result?) {
        super.postResult(result)
        handler!!
            .obtainMessage(MESSAGE_POST_RESULT, (forward ?: this) to result)
            .sendToTarget()
    }

    /**
     * Call this function to signal progress.
     * This is called on the worker thread.
     * It must ensure onPostExecute ins called on the main thread.
     */
    @WorkerThread
    @CallSuper
    override fun postProgress(progress: Progress) {
        super.postProgress(progress)
        if (!isCancelled()) {
            handler!!
                .obtainMessage(MESSAGE_POST_PROGRESS, (forward ?: this) to progress)
                .sendToTarget()
        }
    }

    // O V E R R I D A B L E

    /**
     * Called  on main thread after execution completes
     *
     * @param result result
     */
    @UiThread
    open fun onDone(result: Result?) {
    }

    /**
     * Called  on main thread as progress is emitted
     *
     * @param progress progress
     */
    @UiThread
    open fun onProgress(progress: Progress) {
    }

    /**
     * Called  on main thread as cancel call back
     */
    @UiThread
    open fun onCancelled() {
    }

    companion object {

        /**
         * Result message type
         */
        private const val MESSAGE_POST_RESULT = 1

        /**
         * Progress message type
         */
        private const val MESSAGE_POST_PROGRESS = 2

        /**
         * Handler that multiplexes handling of posted task progress and task result
         */
        private class TaskHandler<Params, Progress, Result> : Handler(Looper.getMainLooper()) {

            /**
             * Handle message on main thread
             */
            @UiThread
            override fun handleMessage(msg: Message) {
                when (msg.what) {

                    MESSAGE_POST_RESULT -> {
                        @Suppress("UNCHECKED_CAST")
                        val payload = msg.obj as Pair<Task<Params, Progress, Result>, Result>
                        if (payload.first.isCancelled()) {
                            payload.first.onCancelled()
                        } else {
                            payload.first.onDone(payload.second)
                        }
                        payload.first.status = Status.FINISHED
                    }

                    MESSAGE_POST_PROGRESS -> {
                        @Suppress("UNCHECKED_CAST")
                        val payload2 = msg.obj as Pair<Task<Params, Progress, Result>, Progress>
                        payload2.first.onProgress(payload2.second)
                    }
                }
            }
        }

        /**
         * Singleton handler object that ports result, progress, cancellation on to the main thread.
         */
        private var handler: TaskHandler<*, *, *>? = null
            get() {
                synchronized(Task::class.java) {
                    if (field == null) {
                        field = TaskHandler<Any, Any, Any>()
                    }
                    return field
                }
            }
    }
}
