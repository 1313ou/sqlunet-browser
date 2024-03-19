/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.concurrency.observe

import com.bbou.concurrency.Cancelable

/**
 * Observer class
 */
open class BaseObserver<Progress : Pair<Number, Number>> : TaskObserver<Progress> {

    /**
     * Start event
     */
    override fun taskStart(task: Cancelable) {}

    /**
     * Finish event
     *
     * @param result result
     */
    override fun taskFinish(result: Boolean) {}

    /**
     * Intermediate progress event
     *
     * @param progress progress value
     */
    override fun taskProgress(progress: Progress) {}

    /**
     * Intermediate update event
     *
     * @param status status
     */
    override fun taskUpdate(status: CharSequence) {}
}