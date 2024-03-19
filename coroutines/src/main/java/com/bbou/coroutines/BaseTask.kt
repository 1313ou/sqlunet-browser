package com.bbou.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer

/**
 * Base task, has a worker, but no observer
 */
abstract class BaseTask<Params, Result> {

    /**
     * Status
     */
    protected var hasRun = AtomicBoolean(false)

    /**
     * Scope for worker job)
     */
    protected lateinit var scope: CoroutineScope

    /**
     * Worker
     */
    protected lateinit var worker: Deferred<Result>

    // A P I

    /**
     * Do in the background
     *
     * @param params params
     * @return result
     */
    abstract suspend fun doJob(params: Params): Result

    // R U N

    /**
     * Execute
     *
     * @param dispatcher dispatcher for worker
     * @param params parameters for bg worker
     * @return worker
     */
    private suspend fun exec(
        dispatcher: CoroutineDispatcher,
        params: Params,
    ): Deferred<Result> {

        // check
        if (hasRun.getAndSet(true)) {
            throw IllegalStateException("The task has already run.")
        }

        // run task
        coroutineScope {

            scope = this

            // run worker job
            withContext(dispatcher) {
                worker = async {
                    doJob(params)
                }
            }
        }

        // deferred result
        return worker
    }

    /**
     * Run (execute and wait for result)
     *
     * @param dispatcher dispatcher for worker
     * @param params parameters for bg worker
     * @return result
     */
    suspend fun run(
        dispatcher: CoroutineDispatcher,
        params: Params,
    ): Result {

        val worker = exec(dispatcher, params)

        // await worker, get result
        return worker.await()
    }

    /**
     * Run (execute, wait for result and pass it to consumer)
     *
     * @param dispatcher dispatcher for worker
     * @param params parameters for job
     * @param resultConsumer result consumer
     * @return result
     */
    suspend fun runAndConsumeResult(
        dispatcher: CoroutineDispatcher,
        params: Params,
        resultConsumer: Consumer<Result>,
    ): Result {
        val r: Result = run(dispatcher, params)
        resultConsumer.accept(r)
        return r
    }

    /**
     * Cancel
     */
    // @CallSuper
    open fun cancel() {
        println("Cancel")
        scope.cancel()
    }
}
