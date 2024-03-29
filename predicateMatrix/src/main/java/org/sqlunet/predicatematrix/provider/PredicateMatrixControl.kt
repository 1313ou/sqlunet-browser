/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix.provider

/**
 * PredicateMatrix query control
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object PredicateMatrixControl {

    // table codes
    const val PM = 10

    // join codes
    const val PM_X = 11

    @JvmStatic
    fun queryMain(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): Result? {
        val groupBy: String? = null
        val table: String = when (code) {
            PM -> Q.PM.TABLE
            PM_X -> Q.PM_X.TABLE
            else -> return null
        }
        return Result(table, projection0, selection0, selectionArgs0, groupBy)
    }

    class Result(@JvmField val table: String, @JvmField val projection: Array<String>?, @JvmField val selection: String?, @JvmField val selectionArgs: Array<String>?, @JvmField val groupBy: String?)
}
