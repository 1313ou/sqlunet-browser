/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

/**
 * Relation, utility class to encapsulate relation data
 *
 * @param id       relation id
 * @param name     relation name
 * @param recurses whether the relation recurses
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class Relation(
    val id: Int,
    val name: String,
    val recurses: Boolean,
) {

    var pos = 0
}
