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
    val number: String,
    val xTag: String,
    val description1: String,
    val description2: String,
    val syntax: String,
    val semantics: String,
    vararg val examples: String,
)
