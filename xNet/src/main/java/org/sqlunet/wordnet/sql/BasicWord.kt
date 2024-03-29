/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

/**
 * BasicWord
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class BasicWord(

    /**
     * `word` is the string
     */
    @JvmField val word: String,
    /**
     * `id` is the word id
     */
    @JvmField val id: Long
) 