/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.provider

import android.app.SearchManager

/**
 * Queries factory, which will execute on the development machine (host).
 */
object Expected {

    fun expected(code: Int, uriLast: String, projection0: Array<String>, selection0: String?, selectionArgs0: Array<String>?): PropBankControl.Result? {
        val table: String
        var projection = projection0
        var selection = selection0
        var selectionArgs = selectionArgs0
        var groupBy: String? = null

        when (code) {
            PropBankControl.PBROLESET1 -> {
                table = Q.PBROLESET1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += V.ROLESETID + " = " + uriLast
            }

            PropBankControl.PBROLESETS -> table = Q.PBROLESETS.TABLE
            PropBankControl.PBROLESETS_X -> table = Q.PBROLESETS_X.TABLE
            PropBankControl.WORDS_PBROLESETS -> table = Q.WORDS_PBROLESETS.TABLE
            PropBankControl.PBROLESETS_PBROLES -> table = Q.PBROLESETS_PBROLES.TABLE
            PropBankControl.PBROLESETS_PBEXAMPLES -> table = Q.PBROLESETS_PBEXAMPLES.TABLE
            PropBankControl.LOOKUP_FTS_EXAMPLES -> table = Q.LOOKUP_FTS_EXAMPLES.TABLE
            PropBankControl.LOOKUP_FTS_EXAMPLES_X -> table = Q.LOOKUP_FTS_EXAMPLES_X.TABLE

            PropBankControl.PBROLESETS_X_BY_ROLESET -> {
                table = Q.PBROLESETS_X_BY_ROLESET.TABLE; groupBy = Q.PBROLESETS_X_BY_ROLESET.GROUPBY
            }

            PropBankControl.PBROLESETS_PBEXAMPLES_BY_EXAMPLE -> {
                table = Q.PBROLESETS_PBEXAMPLES_BY_EXAMPLE.TABLE; groupBy = Q.PBROLESETS_PBEXAMPLES_BY_EXAMPLE.GROUPBY
            }

            PropBankControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE -> {
                table = Q.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE.TABLE; groupBy = Q.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE.GROUPBY
            }

            PropBankControl.SUGGEST_WORDS -> {
                projection = Q.SUGGEST_WORDS.PROJECTION
                projection[1] = projection[1].replace("#\\{suggest_text_1\\}".toRegex(), SearchManager.SUGGEST_COLUMN_TEXT_1)
                projection[2] = projection[2].replace("#\\{suggest_query\\}".toRegex(), SearchManager.SUGGEST_COLUMN_QUERY)
                selection = Q.SUGGEST_WORDS.SELECTION
                selectionArgs = arrayOf(Q.SUGGEST_WORDS.ARGS[0].replace("#\\{uri_last\\}".toRegex(), uriLast))
                table = Q.SUGGEST_WORDS.TABLE
            }

            PropBankControl.SUGGEST_FTS_WORDS -> {
                projection = Q.SUGGEST_FTS_WORDS.PROJECTION
                projection[1] = projection[1].replace("#\\{suggest_text_1\\}".toRegex(), SearchManager.SUGGEST_COLUMN_TEXT_1)
                projection[2] = projection[2].replace("#\\{suggest_query\\}".toRegex(), SearchManager.SUGGEST_COLUMN_QUERY)
                selection = Q.SUGGEST_FTS_WORDS.SELECTION
                selectionArgs = arrayOf(Q.SUGGEST_FTS_WORDS.ARGS[0].replace("#\\{uri_last\\}".toRegex(), uriLast))
                table = Q.SUGGEST_FTS_WORDS.TABLE
            }

            else -> return null
        }
        return PropBankControl.Result(table, projection, selection, selectionArgs, groupBy)
    }
}