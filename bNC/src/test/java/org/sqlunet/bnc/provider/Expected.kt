/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.bnc.provider

/**
 * Queries factory, which will execute on the development machine (host).
 */
object Expected {

    fun expected(code: Int, uriLast: String, projection0: Array<String>, selection0: String?, selectionArgs0: Array<String>?): BNCControl.Result? {
        val table: String
        var projection = projection0
        var selection = selection0
        var selectionArgs = selectionArgs0
        var groupBy: String? = null

        when (code) {
            BNCControl.BNC -> {
                table = Q.BNCS.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += Q.BNCS.SELECTION
            }

            BNCControl.WORDS_BNC -> table = Q.WORDS_BNCS.TABLE

            else -> return null
        }
        return BNCControl.Result(table, projection, selection, selectionArgs, groupBy)
    }
}