package com.bbou.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.function.Consumer

/**
 * Progress emitter interface
 *
 * @param Progress progress type
 */
interface ProgressEmitter<Progress> {

    /**
     * Emit progress
     *
     * @param progress progress
     */
    suspend fun emitProgress(progress: Progress)
}

/**
 * Task, has a progress channel and an observer
 *
 * @param Params params type
 * @param Progress progress
 * @param Result result type
 */
abstract class Task<Params, Progress, Result> : BaseTask<Params, Result>(), ProgressEmitter<Progress> {

    /**
     * Job (prejob + worker job)
     */
    @Suppress("unused")
    private lateinit var job: Job

    /**
     * Observer
     */
    private lateinit var observer: Job

    /**
     * Progress Channel
     */
    private var progressChannel = Channel<Progress>()

    // R U N

    /**
     * Execute
     *
     * @param dispatcher dispatcher for worker
     * @param params parameters for bg worker
     * @param progressConsumer progress consumer
     * @return worker as deferred result
     */
    private suspend fun exec(
        dispatcher: CoroutineDispatcher,
        params: Params,
        progressConsumer: Consumer<Progress>,
    ): Deferred<Result> {

        // check
        if (hasRun.getAndSet(true)) {
            throw IllegalStateException("The task has already run.")
        }

        // run task
        coroutineScope {

            scope = this

            // observer
            observer = launch {
                for (progress in progressChannel) {
                    progressConsumer.accept(progress)
                }
            }

            // run bg job
            worker = async {
                withContext(dispatcher) {
                    val deferred = doJob(params)
                    progressChannel.close()
                    deferred
                }
            }
        }

        // deferred
        return worker
    }

    /**
     * Run while consuming progress
     *
     * @param dispatcher dispatcher for worker
     * @param params parameters for bg worker
     * @param progressConsumer progress consumer
     * @return result
     */
    suspend fun runObserved(
        dispatcher: CoroutineDispatcher,
        params: Params,
        progressConsumer: Consumer<Progress>,
    ): Result {
        val worker = exec(dispatcher, params, progressConsumer)
        // await worker, get result
        return worker.await()
    }

    /**
     * Run (execute, wait for result and pass it to consumer)
     *
     * @param dispatcher dispatcher for worker
     * @param params parameters for job
     * @param progressConsumer progress consumer
     * @param resultConsumer result consumer
     * @return result
     */
    suspend fun runObservedAndConsumeResult(
        dispatcher: CoroutineDispatcher,
        params: Params,
        progressConsumer: Consumer<Progress>,
        resultConsumer: Consumer<Result>,
    ): Result {
        val result: Result = runObserved(dispatcher, params, progressConsumer)
        resultConsumer.accept(result)
        return result
    }

    /**
     * Publish progress
     *
     * @param progress progress
     */
    override suspend fun emitProgress(progress: Progress) {
        progressChannel.send(progress)
    }
}
