/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.concurrency.observe

import com.bbou.concurrency.Cancelable

/**
 * Observer interface
 */
interface TaskObserver<Progress : Pair<Number, Number>> {

    /**
     * Start event
     */
    fun taskStart(task: Cancelable)

    /**
     * Finish event
     *
     * @param result result
     */
    fun taskFinish(result: Boolean)

    /**
     * Intermediate progress event
     *
     * @param progress progress value
     */
    fun taskProgress(progress: Progress)

    /**
     * Intermediate update event
     *
     * @param status status
     */
    fun taskUpdate(status: CharSequence)
}