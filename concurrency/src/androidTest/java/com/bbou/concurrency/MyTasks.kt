package com.bbou.concurrency

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

data class Parameters(val times: Int, val lapse: Long)

private fun longBlocking(howLong: Long) {
    try {
        Thread.sleep(howLong)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
}

@Suppress("SameReturnValue")
fun dummyWork(params: Parameters?, emit: ((Int) -> Unit)? = null): String {
    println("Job> ${where()}")
    for (count in 1..params!!.times) {
        longBlocking(params.lapse)
        if (emit != null) {
            emit(count)
        }
    }
    println("Job< ${where()}")
    return retValue
}

fun fibonacciWork(n: Int): Long {
    return if (n <= 1) {
        n.toLong()
    } else fibonacciWork(n - 1) + fibonacciWork(n - 2)
}

const val retValue = "Kilroy was there"

open class MyTask : Task<Parameters, Int, String>() {

    /**
     * Background job
     *
     * @param params parameters
     */
    override fun doJob(params: Parameters?): String {
        assertIsNotOnMainThread()
        return dummyWork(params!!, this::postProgress)
    }

    /**
     * Result posting callback
     *
     * @param result result
     */
    override fun postResult(result: String?) {
        super.postResult(result)
        assertEquals(retValue, result)
        assertIsNotOnMainThread()
    }

    /**
     * Progress posting callback
     *
     * @param progress progress values
     */
    override fun postProgress(progress: Int) {
        super.postProgress(progress)
        assertIsNotOnMainThread()
    }

    /**
     * Result listener callback
     *
     * @param result result
     */
    override fun onDone(result: String?) {
        println("result '$result'")
        assertEquals(retValue, result)
        assertIsOnMainThread()
    }

    /**
     * Progress listener callback
     *
     * @param progress progress values
     */
    override fun onProgress(progress: Int) {
        println("progress '$progress'")
        assertIsOnMainThread()
    }
}

fun where(): String {
    return " @ ${Thread.currentThread()}"
}

fun isOnMainThread(): Boolean {
    val t = Thread.currentThread().toString()
    return t.matches(Regex("Thread\\[main.*$"))
}

fun assertIsOnMainThread() {
    assertTrue("thread was '${Thread.currentThread()}'", isOnMainThread())
}

fun assertIsNotOnMainThread() {
    assertFalse("thread was '${Thread.currentThread()}'", isOnMainThread())
}
