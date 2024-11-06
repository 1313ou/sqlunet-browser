/*
 * Copyright (c) 2024. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.coroutines

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Progress callback
 *
 * @param Result result type
 */
interface ResultCallback<Result> {

    /**
     * On done (post execute), fired when result becomes available or task fails
     *
     * @param result result
     */
    // @UiThread
    fun onDone(result: Result)
}

/**
 * With no consumer, the standard callbacks
 */
abstract class BaseTaskWithCallback<Params, Result> : BaseTask<Params, Result>(), ResultCallback<Result> {

    // API extension

    /**
     * Post execute on UI thread
     */
    // @UiThread
    override fun onDone(result: Result) {
    }

    /**
     * Run implementation that uses callbacks
     *
     * @param dispatcher dispatcher for job
     * @param params parameters for job
     * @return result
     */
    suspend fun runAndCallback(dispatcher: CoroutineDispatcher, params: Params): Result {
        return runAndConsumeResult(
            dispatcher,
            params,
        ) { result -> onDone(result) }
    }
}
