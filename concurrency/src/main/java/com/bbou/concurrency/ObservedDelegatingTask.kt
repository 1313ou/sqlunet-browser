/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.concurrency

import com.bbou.concurrency.observe.TaskObserver

/**
 * Task that
 * -wraps a task and drives it, delegates work and call backs to that delegate
 * -hosts an observer fires events to its observer
 * The delegate task is set to forward posted (progress and result) messages to this wrapper.
 */
class ObservedDelegatingTask<Params, Progress : Pair<Number, Number>, Result>(private val delegateTask: Task<Params, Progress, Result>, observer: TaskObserver<Progress>) : Task<Params, Progress, Result>() {

    private val observer: TaskObserver<Progress>

    init {
        // termination and progress signals are forwarded to this delegating task
        delegateTask.setForward(this)
        this.observer = observer
    }

    private val task: Task<Params, Progress, Result>
        get() = this

    override fun doJob(params: Params?): Result {
        return delegateTask.doJob(params)
    }

    override fun onPreExecute() {
        delegateTask.onPreExecute()
        observer.taskStart(this)
    }

    override fun onDone(result: Result?) {
        delegateTask.onDone(result)
        observer.taskFinish(true)
    }

    override fun onCancelled() {
        delegateTask.onCancelled()
        observer.taskFinish(false)
    }

    override fun onProgress(progress: Progress) {
        delegateTask.onProgress(progress)
        observer.taskProgress(progress) // current, total
    }
}
