/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.concurrency

/**
 * Cancelable interface
 *
 */
interface Cancelable {

    /**
     * Cancel
     *
     * @param mayInterruptIfRunning may interrupt if running
     * @return cancel status
     */
    fun cancel(mayInterruptIfRunning: Boolean): Boolean

    /**
     * Cancel status
     *
     * @return true if it has been cancelled
     */
    fun isCancelled(): Boolean
}
