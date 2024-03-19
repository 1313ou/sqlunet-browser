package com.bbou.coroutines.task

import com.bbou.coroutines.BaseTask
import com.bbou.coroutines.ProgressEmitter
import com.bbou.coroutines.Task
import com.bbou.coroutines.TaskWithCallbacks
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.yield
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import kotlin.coroutines.coroutineContext

data class Parameters(val times: Int, val lapse: Long)

private fun longBlocking(howLong: Long) {
    try {
        Thread.sleep(howLong)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
}

const val retValue = "Kilroy was there"

suspend fun dummyWork(params: Parameters?, emitter: ProgressEmitter<Int>? = null): String? {

    val job = coroutineContext[Job] ?: return null
    println("Job> ${where()}")
    for (count in 1..params!!.times) {
        job.ensureActive() // checks for cancellation
        longBlocking(params.lapse)
        emitter?.emitProgress(count)
        yield() // checks for cancellation
    }
    println("Job< ${where()}")
    return retValue
}

open class MyBaseTask : BaseTask<Parameters, String?>() {

    private val mainThread: String

    init {
        val t = Thread.currentThread().name
        mainThread = t.substringBefore('@', t).trim()
    }

    override suspend fun doJob(params: Parameters): String? {
        assertFalse("thread was '${Thread.currentThread()}'", isOnMainThread())
        return dummyWork(params)
    }

    private fun isOnMainThread(): Boolean {
        val t = Thread.currentThread().name
        return t.substringBefore('@', t).trim() == mainThread
    }
}

open class MyTask : Task<Parameters, Int, String?>() {

    val mainThread: String

    init {
        val t = Thread.currentThread().name
        mainThread = t.substringBefore('@', t).trim()
    }

    override suspend fun doJob(params: Parameters): String? {
        return dummyWork(params, emitter = this)
    }

    fun isOnMainThread(): Boolean {
        val t = Thread.currentThread().name
        return t.substringBefore('@', t).trim() == mainThread
    }
}

class MyTaskWithCallbacks : TaskWithCallbacks<Parameters, Int, String?>() {

    private val mainThread: String

    init {
        val t = Thread.currentThread().name
        mainThread = t.substringBefore('@', t).trim()
    }

    override suspend fun doJob(params: Parameters): String? {
        return dummyWork(params, emitter = this)
    }

    override fun onDone(result: String?) {
        assertTrue("thread was '${Thread.currentThread()}' and main '${mainThread}'", isOnMainThread())
        println("Post execute '${result}' ${where()}")
        println(result)
    }

    override fun onProgress(progress: Int) {
        assertTrue("thread was '${Thread.currentThread()}' and main '${mainThread}'", isOnMainThread())
        println("callback progress $progress ${where()}")
    }

    private fun isOnMainThread(): Boolean {
        val t = Thread.currentThread().name
        return t.substringBefore('@', t).trim() == mainThread
    }
}

fun where(): String {
    return " @ ${Thread.currentThread()}"
}
