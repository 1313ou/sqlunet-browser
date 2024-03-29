/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet

/**
 * Has 2 synset-ids interface
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
interface Has2SynsetId : HasSynsetId {

    /**
     * Get synset 2 id
     *
     * @return synset 2 id
     */
    fun getSynset2Id(): Long
}
