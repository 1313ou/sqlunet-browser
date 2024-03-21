/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet

/**
 * Has synset-id interface
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
fun interface HasSynsetId {
    /**
     * Get synset id
     *
     * @return synset id
     */
    fun getSynsetId(): Long
}
