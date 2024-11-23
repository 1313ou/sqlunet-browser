/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.sql

/**
 * Argument
 *
 * @param argType     n
 * @param f           f
 * @param description description
 * @param vnRole      VerbNet role
 * @param subText     sub text
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class PbArg private constructor(
    val argType: String,
    val f: String?,
    val description: String,
    val vnRole: String?,
    val subText: String,
) {

    /**
     * Constructor from argument fields
     *
     * @param argFields argument fields
     */
    constructor(vararg argFields: String) : this(
        argFields[0], if ("*" == argFields[1])
            null else argFields[1], argFields[2].lowercase(), if ("*" == argFields[3])
            null else argFields[3], argFields[4]
    )
}
