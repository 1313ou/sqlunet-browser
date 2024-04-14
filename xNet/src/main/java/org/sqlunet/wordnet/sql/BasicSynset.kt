/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

/**
 * BasicSynset
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class BasicSynset protected constructor(
    /**
     * the synset's id in the database
     */
    val synsetId: Long,

    /**
     * `the synset's definition
     */
    val definition: String,

    /**
     * the synset's domain
     */
    val domainId: Int,

    /**
     * a string concatenating the synset's samples
     */
    val sample: String?,
)
