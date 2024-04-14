/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix.provider

/**
 * Queries factory, which will execute on the development machine (host).
 */
object Expected {

    fun expected(code: Int, uriLast: String, projection0: Array<String>, selection0: String?, selectionArgs0: Array<String>?): PredicateMatrixControl.Result? {
        val table: String
        var projection = projection0
        var selection = selection0
        var selectionArgs = selectionArgs0
        var groupBy: String? = null

        when (code) {
            PredicateMatrixControl.PM -> table = Q.PM.TABLE
            PredicateMatrixControl.PM_X -> table = Q.PM_X.TABLE
            else -> return null
        }
        return PredicateMatrixControl.Result(table, projection, selection, selectionArgs, groupBy)
    }
}