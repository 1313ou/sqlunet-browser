/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.concurrency

import androidx.annotation.CallSuper
import com.bbou.concurrency.observe.TaskObserver

/**
 * Task that hosts an observer fires events to its observer
 */
abstract class ObservedTask<Params, Progress : Pair<Number, Number>, Result>(private val observer: TaskObserver<Progress>) : Task<Params, Progress, Result>() {

    @CallSuper
    override fun onDone(result: Result?) {
        super.onDone(result)
        observer.taskFinish(true)
    }

    @CallSuper
    override fun onCancelled() {
        super.onCancelled()
        observer.taskFinish(false)
    }

    @CallSuper
    override fun onProgress(progress: Progress) {
        super.onProgress(progress)
        observer.taskProgress(progress)
    }
}
