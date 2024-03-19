package com.bbou.coroutines

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Progress and Result callbacks
 *
 * @param Progress progress type
 * @param Result result type
 */
interface Callbacks<Progress, Result> : ResultCallback<Result> {

    /**
     * On progress update, fired when progress has been fired and is now received from the channel
     *
     * @param progress progress
     */
    // @UiThread
    fun onProgress(progress: Progress)
}

/**
 * With no consumer, the standard callbacks
 */
abstract class TaskWithCallbacks<Params, Progress, Result> : Task<Params, Progress, Result>(), Callbacks<Progress, Result> {

    // API extension

    /**
     * Post execute on UI thread
     */
    // @UiThread
    override fun onDone(result: Result) {
    }

    /**
     * Handle progress on UI thread
     */
    // @UiThread
    override fun onProgress(progress: Progress) {
        println("Progress $progress")
    }

    /**
     * Run implementation that uses callbacks
     *
     * @param dispatcher dispatcher for job
     * @param params parameters for job
     * @return result
     */
    suspend fun runAndCallback(dispatcher: CoroutineDispatcher, params: Params): Result {
        return runObservedAndConsumeResult(
            dispatcher,
            params,
            { progress -> onProgress(progress) }
        ) { result -> onDone(result) }
    }
}
