/*
 * Copyright (c) 2024. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

object ExecuteAsync {

    /**
     * Execute a runnable
     *
     * @param runnable runnable
     * @return job
     */
    private fun CoroutineScope.execute(runnable: Runnable): Job {
        return launch {
            withContext(Dispatchers.Default) {
                runnable.run()
            }
        }
    }

    /**
     * Suspendable execute a runnable
     *
     * @param runnable runnable
     * @return job
     */
    suspend fun execute(runnable: Runnable): Job {
        return coroutineScope {
            return@coroutineScope execute(runnable)
        }
    }

    /**
     * Suspendable execute a runnable and wait
     *
     * @param runnable runnable
     */
    suspend fun executeAndWait(runnable: Runnable) {
        coroutineScope {
            val job = execute(runnable)
            job.join()
        }
    }


    // S C O P E   F A C T O R Y

    /**
     * Factory function, no context provided
     *
     * @return CoroutineScope
     */
    fun CoroutineScope(): CoroutineScope = CoroutineScope(Job())

    /**
     * Factory function
     *
     * @param context coroutine context to preserve if it has Job else add a job to it
     * @return CoroutineScope
     */
    private fun CoroutineScope(context: CoroutineContext): CoroutineScope = ContextScope(if (context[Job] != null) context else context + Job())

    /**
     * Context scope
     *
     * @constructor
     * @param context coroutine context
     */
    internal class ContextScope(context: CoroutineContext) : CoroutineScope {
        override val coroutineContext: CoroutineContext = context
        override fun toString(): String = "CoroutineScope(coroutineContext=$coroutineContext)"
    }
}