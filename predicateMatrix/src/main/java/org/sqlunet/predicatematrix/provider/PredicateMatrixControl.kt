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

    fun queryMain(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): Result? {
        val groupBy: String? = null
        val table: String = when (code) {
            PM -> Q.PM.TABLE
            PM_X -> Q.PM_X.TABLE
            else -> return null
        }
        return Result(table, projection0, selection0, selectionArgs0, groupBy)
    }

    data class Result(val table: String, val projection: Array<String>?, val selection: String?, val selectionArgs: Array<String>?, val groupBy: String?) {

        override fun toString(): String {
            return "table='$table'\nprojection=${projection.contentToString()}\nselection='$selection'\nselectionArgs=${selectionArgs.contentToString()}\ngroupBy=$groupBy"
        }
    }
}
