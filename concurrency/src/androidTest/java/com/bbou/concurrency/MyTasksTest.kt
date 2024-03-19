/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.concurrency

import org.junit.Test

class MyTasksTest {

    /**
     * Test Task
     */
    @Test
    fun taskTest() {
        val t = MyTask().execute(Parameters(25, 500))
        val r = t.get()
        println("done $r")
    }
}
