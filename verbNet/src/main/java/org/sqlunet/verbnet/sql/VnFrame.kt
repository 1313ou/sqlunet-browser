/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

/**
 * VerbNet frame
 *
 * @param number       is the frame name
 * @param xTag         is the frame xtag
 * @param description1 is the frame major descriptor
 * @param description2 is the frame minor descriptor
 * @param syntax       is the syntax data
 * @param semantics    is the semantic data
 * @param examples     examples

 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class VnFrame(
    @JvmField val number: String,
    @JvmField val xTag: String,
    @JvmField val description1: String,
    @JvmField val description2: String,
    @JvmField val syntax: String,
    @JvmField val semantics: String,
    vararg examples: String,
) {
    @JvmField
    val examples: Array<out String>

    init {
        this.examples = examples
    }
}
