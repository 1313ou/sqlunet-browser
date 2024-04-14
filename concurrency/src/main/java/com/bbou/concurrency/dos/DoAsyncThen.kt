/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.concurrency.dos

import com.bbou.concurrency.Task

/**
 * Do in background, then run something on main thread
 */
@Suppress("unused")
object DoAsyncThen {

    /**
     * Run main runnable then post runnable
     */
    fun run(main: Runnable, post: Runnable): Task<Void?, Unit, Unit> {
        val t: Task<Void?, Unit, Unit> = object : Task<Void?, Unit, Unit>() {

            override fun doJob(params: Void?) {
                main.run()
            }

            override fun onDone(result: Unit?) {
                post.run()
            }
        }
        t.execute(null)
        return t
    }
}
