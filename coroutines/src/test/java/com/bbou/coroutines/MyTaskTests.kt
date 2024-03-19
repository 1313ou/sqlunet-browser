package com.bbou.coroutines.task

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test


class MyTaskTests {

    private val repeatTimes = 10
    private val pulse = 1000L
    private val cancelAfter = 3000L

    @Test
    fun baseTaskTest() {
        println("-----")
        val b = MyBaseTask()
        run(b)
    }

    @Test
    fun taskTest() {
        println("-----")
        val b = MyTask()
        run(b)
    }

    @Test
    fun withCallbacksTaskTest() {
        println("-----")
        val t = MyTaskWithCallbacks()
        run(t)
    }

    // C A N C E L

    @Test(expected = CancellationException::class)
    fun baseTaskCancelTest() {
        println("-----")
        val b = MyBaseTask()
        runAndCancel(b)
    }

    @Test(expected = CancellationException::class)
    fun taskCancelTest() {
        println("-----")
        val t = MyTask()
        runAndCancel(t)
    }

    @Test(expected = CancellationException::class)
    fun withCallbacksTaskCancelTest() {
        println("-----")
        val t = MyTaskWithCallbacks()
        runAndCancel(t)
    }

    // R E P E A T

    @Test(expected = IllegalStateException::class)
    fun baseTaskRepeatTest() {
        println("-----")
        val t = MyBaseTask()
        run(t)
        println(".....")
        run(t)
    }

    @Test(expected = IllegalStateException::class)
    fun taskRepeatTest() {
        println("-----")
        val t = MyTask()
        run(t)
        println(".....")
        run(t)
    }

    @Test(expected = IllegalStateException::class)
    fun withCallBacksTaskRepeatTest() {
        println("-----")
        val t = MyTaskWithCallbacks()
        run(t)
        println(".....")
        run(t)
    }

    private fun run(t: MyBaseTask) {
        try {
            runBlocking {
                println("Run ${where()}")
                val result = t.run(Dispatchers.IO, Parameters(repeatTimes, pulse))
                println("Done '$result' ${where()}")
                assertEquals(retValue, result)
            }
            println("End ${where()}")
        } catch (ce: CancellationException) {
            println("Caught $ce ${where()}")
        } finally {
            println("Exit ${where()}")
        }
    }

    private fun run(t: MyTask) {
        try {
            runBlocking {
                println("Run ${where()}")
                val result = t.runObserved(Dispatchers.IO, Parameters(repeatTimes, pulse)) {
                    assertTrue("thread was '${Thread.currentThread()}' and main '${t.mainThread}'", t.isOnMainThread())
                    println("my progress $it")
                }
                println("Done '$result' ${where()}")
                assertEquals(retValue, result)
            }
            println("End ${where()}")
        } catch (ce: CancellationException) {
            println("Caught $ce ${where()}")
        } finally {
            println("Exit ${where()}")
        }
    }

    private fun run(t: MyTaskWithCallbacks) {
        try {
            runBlocking {
                println("Run ${where()}")
                val result = t.runAndCallback(Dispatchers.IO, Parameters(repeatTimes, pulse))
                println("Done '$result' ${where()}")
                assertEquals(retValue, result)
            }
            println("End ${where()}")
        } catch (ce: CancellationException) {
            println("Caught $ce ${where()}")
        } finally {
            println("Exit ${where()}")
        }
    }

    private fun runAndCancel(t: MyBaseTask) {
        try {
            runBlocking {
                println("Run ${where()}")
                launch {
                    delay(cancelAfter)
                    println("Cancel now ${where()}")
                    t.cancel()
                }
                val result = t.run(Dispatchers.IO, Parameters(times = repeatTimes, lapse = pulse))
                println("Done '$result' ${where()}")
                fail("Should have been cancelled")
            }
            println("End ${where()}")
        } catch (ce: CancellationException) {
            println("Caught $ce ${where()}")
            throw ce
        } finally {
            println("Exit ${where()}")
        }
    }

    private fun runAndCancel(t: MyTask) {
        try {
            runBlocking {
                println("Run ${where()}")
                launch {
                    delay(cancelAfter)
                    println("Cancel now ${where()}")
                    t.cancel()
                }
                val result = t.runObserved(Dispatchers.IO, Parameters(times = repeatTimes, lapse = pulse), ::println)
                println("Done '$result' ${where()}")
                fail("Should have been cancelled")
            }
            println("End ${where()}")
        } catch (ce: CancellationException) {
            println("Caught $ce ${where()}")
            throw ce
        } finally {
            println("Exit ${where()}")
        }
    }

    private fun runAndCancel(t: MyTaskWithCallbacks) {
        try {
            runBlocking {
                println("Run ${where()}")
                launch {
                    delay(cancelAfter)
                    println("Cancel now ${where()}")
                    t.cancel()
                }

                val result = t.runAndCallback(Dispatchers.IO, Parameters(times = repeatTimes, lapse = pulse))
                println("Done '$result' ${where()}")
                fail("Should have been cancelled")
            }
            println("End ${where()}")
        } catch (ce: CancellationException) {
            println("Caught $ce ${where()}")
            throw ce
        } finally {
            println("Exit ${where()}")
        }
    }
}
