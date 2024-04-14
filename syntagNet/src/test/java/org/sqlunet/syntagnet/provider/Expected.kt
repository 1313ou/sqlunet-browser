/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.provider

/**
 * Queries factory, which will execute on the development machine (host).
 */
object Expected {

    fun expected(code: Int, uriLast: String, projection0: Array<String>, selection0: String?, selectionArgs0: Array<String>?): SyntagNetControl.Result? {
        val table: String
        var projection = projection0
        var selection = selection0
        var selectionArgs = selectionArgs0
        var groupBy: String? = null

        when (code) {
            SyntagNetControl.COLLOCATIONS -> table = Q.COLLOCATIONS.TABLE
            SyntagNetControl.COLLOCATIONS_X -> table = Q.COLLOCATIONS_X.TABLE

            SyntagNetControl.COLLOCATION1 -> {
                table = Q.COLLOCATION1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += V.SYNTAGMID + " = " + uriLast
            }

            else -> return null
        }
        return SyntagNetControl.Result(table, projection, selection, selectionArgs, groupBy)
    }
}