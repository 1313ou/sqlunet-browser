/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.concurrency

import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Callable
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.FutureTask
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.Volatile

/**
 * Abstract asynchronous task.
 * Executes job on background thread.
 */
abstract class BaseTask<Params, Progress, Result> : Cancelable {

    // B O D Y

    /**
     * Callable whose input is Params and output Result
     *
     * @param Params parameter type
     * @param Result result type
     */
    private abstract class JobBody<Params, Result> : Callable<Result> {
        var param: Params? = null
    }

    private val body: JobBody<Params, Result>

    // F U T U R E

    /**
     * A cancellable asynchronous computation.
     * The result can only be retrieved when the computation has completed; the get methods will block if the computation has not yet completed.
     */
    private val future: FutureTask<Result>

    // S T A T U S

    /**
     * Status enum
     */
    enum class Status {
        /**
         * Pending
         */
        PENDING,

        /**
         * Running
         */
        RUNNING,

        /**
         * Finished
         */
        FINISHED
    }

    /**
     * Status, can only be set by this class
     */
    @Volatile
    protected var status: Status

    /**
     * Cancel flag
     */
    protected val cancelled: AtomicBoolean

    /**
     * Task invocation flag
     */
    protected val taskInvoked: AtomicBoolean

    // C O N S T R U C T

    init {
        status = Status.PENDING
        cancelled = AtomicBoolean()
        taskInvoked = AtomicBoolean()

        // wraps doJob as a callable
        body = object : JobBody<Params, Result>() {

            override fun call(): Result? {
                taskInvoked.set(true)
                try {
                    return doJob(param)
                } catch (t: Throwable) {
                    cancelled.set(true)
                    throw t
                } finally {
                }
            }
        }

        // future
        future = object : FutureTask<Result>(body) {
            /**
             * Invoked when this task transitions to state isDone, on the same thread as the body
             */
            override fun done() {
                try {
                    val result = this.get()
                    postResult(if (taskInvoked.get()) result else null)
                } catch (ce: CancellationException) {
                    postResult(null)
                } catch (_: InterruptedException) {
                    // do nothing
                } catch (ee: ExecutionException) {
                    throw RuntimeException("An error occurred while executing background job", ee.cause)
                } catch (t: Throwable) {
                    throw RuntimeException("An error occurred while executing background job", t)
                }
            }
        }
    }

    // O V E R R I D A B L E

    /**
     * Called  on main thread before execution
     */
    @UiThread
    open fun onPreExecute() {
    }

    /**
     * Do background job
     *
     * @param params parameters
     */
    @WorkerThread
    abstract fun doJob(params: Params? = null): Result

    // C A N C E L

    /**
     * Cancel
     *
     * @param mayInterruptIfRunning may interrupt if running
     * @return true if cancelled
     */
    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        cancelled.set(true)
        return future.cancel(mayInterruptIfRunning)
    }

    /**
     * Cancel status
     *
     * @return true of task has been cancelled
     */
    override fun isCancelled(): Boolean {
        return cancelled.get() || future.isCancelled
    }

    // G E T

    /**
     * Get result. Awaits completion.
     * The result can only be retrieved when the computation has completed;
     * the get methods will block if the computation has not yet completed.
     */
    @Throws(InterruptedException::class, ExecutionException::class)
    fun get(): Result {
        return future.get()
    }

    /**
     * Get result. Awaits completion or timeout
     */
    @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
    operator fun get(timeout: Long, unit: TimeUnit?): Result {
        return future[timeout, unit!!]
    }

    // R E S U L T

    /**
     * Call this function to signal termination and result.
     * This is called on the worker thread.
     */
    @WorkerThread
    protected open fun postResult(result: Result?) {
    }

    // P R O G R E S S

    /**
     * Call this function to signal progress.
     * This is called on the worker thread.
     */
    @WorkerThread
    protected open fun postProgress(progress: Progress) {
    }

    // E X E C U T E

    /**
     * Execute
     *
     * @param params parameters
     * @return task
     */
    fun execute(params: Params): BaseTask<Params, Progress, Result> {
        return executeOnExecutor(defaultExecutor, params)
    }

    /**
     * Execute on given executor
     *
     * @param executor executor
     * @param param parameter
     * @return task
     */
    fun executeOnExecutor(executor: Executor, param: Params): BaseTask<Params, Progress, Result> {
        return if (status != Status.PENDING) {
            when (status) {
                Status.RUNNING -> throw IllegalStateException("Cannot execute task: the task is already running.")
                Status.FINISHED -> throw IllegalStateException("Cannot execute task: the task has already been executed (a task can be executed only once)")
                else -> throw IllegalStateException("We should never reach this state")
            }
        } else {
            status = Status.RUNNING
            onPreExecute()
            body.param = param
            executor.execute(future)
            this
        }
    }

    companion object {

        // E X E C U T O R

        private const val CORE_POOL_SIZE = 5
        private const val MAXIMUM_POOL_SIZE = 128
        private const val KEEP_ALIVE = 1

        private val THREAD_FACTORY: ThreadFactory = object : ThreadFactory {
            private val count = AtomicInteger(1)
            override fun newThread(runnable: Runnable): Thread {
                return Thread(runnable, "WorkerTask #" + count.getAndIncrement())
            }
        }

        private val POOL_WORK_QUEUE: BlockingQueue<Runnable> = LinkedBlockingQueue(10)

        private var THREAD_POOL_EXECUTOR: Executor = ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE.toLong(),
            TimeUnit.SECONDS,
            POOL_WORK_QUEUE,
            THREAD_FACTORY
        )

        @Volatile
        private var defaultExecutor: Executor = THREAD_POOL_EXECUTOR

        /**
         * Set default executor
         *
         * @param executor new default executor
         */
        fun setDefaultExecutor(executor: Executor) {
            defaultExecutor = executor
        }

        /**
         * Execute a runnable on default executor
         */
        fun execute(runnable: Runnable) {
            defaultExecutor.execute(runnable)
        }
    }
}
