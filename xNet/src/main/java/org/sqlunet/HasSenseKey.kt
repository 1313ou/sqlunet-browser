/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet

/**
 * Has sensekey interface
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
fun interface HasSenseKey {

    /**
     * Get sensekey
     *
     * @return sensekey
     */
    fun getSenseKey(): String?
}
