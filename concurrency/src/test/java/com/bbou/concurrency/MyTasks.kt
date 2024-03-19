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
    for (count in 1..params!!.times) {
        longBlocking(params.lapse)
        if (emit != null) {
            emit(count)
        }
    }
    return retValue
}

fun fibonacciWork(n: Int): Long {
    return if (n <= 1) {
        n.toLong()
    } else fibonacciWork(n - 1) + fibonacciWork(n - 2)
}

const val retValue = "Kilroy was there"

open class MyBaseTask : BaseTask<Parameters, Int, String>() {

    /**
     * Background job
     *
     * @param params parameters
     */
    override fun doJob(params: Parameters?): String {
        assertIsNotOnMainThread()
        return dummyWork(params!!, null)
    }

    /**
     * Result posting callback
     *
     * @param result result
     */
    override fun postResult(result: String?) {
        assertEquals(retValue, result)
        assertIsNotOnMainThread()
    }

    /**
     * Progress posting callback
     *
     * @param progress progress values
     */
    override fun postProgress(@Suppress("unused") progress: Int) {
        assertIsNotOnMainThread()
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
