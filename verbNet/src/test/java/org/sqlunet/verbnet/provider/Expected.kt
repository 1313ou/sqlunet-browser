/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.provider

import android.app.SearchManager

/**
 * Queries factory, which will execute on the development machine (host).
 */
object Expected {

    fun expected(code: Int, uriLast: String, projection0: Array<String>, selection0: String?, selectionArgs0: Array<String>?): VerbNetControl.Result? {
        val table: String
        var projection = projection0
        var selection = selection0
        var selectionArgs = selectionArgs0
        var groupBy: String? = null

        when (code) {
            VerbNetControl.VNCLASS1 -> {
                table = Q.VNCLASS1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += V.CLASSID + " = " + uriLast
            }

            VerbNetControl.VNCLASSES -> table = Q.VNCLASSES.TABLE
            VerbNetControl.WORDS_VNCLASSES -> table = Q.WORDS_VNCLASSES.TABLE
            VerbNetControl.LOOKUP_FTS_EXAMPLES -> table = Q.LOOKUP_FTS_EXAMPLES.TABLE
            VerbNetControl.LOOKUP_FTS_EXAMPLES_X -> table = Q.LOOKUP_FTS_EXAMPLES_X.TABLE

            VerbNetControl.VNCLASSES_VNMEMBERS_X_BY_WORD -> {
                table = Q.VNCLASSES_VNMEMBERS_X_BY_WORD.TABLE
                groupBy = Q.VNCLASSES_VNMEMBERS_X_BY_WORD.GROUPBY
            }

            VerbNetControl.VNCLASSES_X_BY_VNCLASS -> {
                table = Q.VNCLASSES_X_BY_VNCLASS.TABLE
                groupBy = Q.VNCLASSES_X_BY_VNCLASS.GROUPBY
            }

            VerbNetControl.VNCLASSES_VNROLES_X_BY_VNROLE -> {
                table = Q.VNCLASSES_VNROLES_X_BY_VNROLE.TABLE
                groupBy = Q.VNCLASSES_VNROLES_X_BY_VNROLE.GROUPBY
            }

            VerbNetControl.VNCLASSES_VNFRAMES_X_BY_VNFRAME -> {
                table = Q.VNCLASSES_VNFRAMES_X_BY_VNFRAME.TABLE
                groupBy = Q.VNCLASSES_VNFRAMES_X_BY_VNFRAME.GROUPBY
            }

            VerbNetControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE -> {
                table = Q.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE.TABLE
                groupBy = Q.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE.GROUPBY
            }

            VerbNetControl.SUGGEST_WORDS -> {
                projection = Q.SUGGEST_WORDS.PROJECTION
                projection[1] = projection[1].replace("#\\{suggest_text_1\\}".toRegex(), SearchManager.SUGGEST_COLUMN_TEXT_1)
                projection[2] = projection[2].replace("#\\{suggest_query\\}".toRegex(), SearchManager.SUGGEST_COLUMN_QUERY)
                selection = Q.SUGGEST_WORDS.SELECTION
                selectionArgs = arrayOf(Q.SUGGEST_WORDS.ARGS[0].replace("#\\{uri_last\\}".toRegex(), uriLast))
                table = Q.SUGGEST_WORDS.TABLE
            }

            VerbNetControl.SUGGEST_FTS_WORDS -> {
                projection = Q.SUGGEST_FTS_WORDS.PROJECTION
                projection[1] = projection[1].replace("#\\{suggest_text_1\\}".toRegex(), SearchManager.SUGGEST_COLUMN_TEXT_1)
                projection[2] = projection[2].replace("#\\{suggest_query\\}".toRegex(), SearchManager.SUGGEST_COLUMN_QUERY)
                selection = Q.SUGGEST_FTS_WORDS.SELECTION
                selectionArgs = arrayOf(Q.SUGGEST_FTS_WORDS.ARGS[0].replace("#\\{uri_last\\}".toRegex(), uriLast))
                table = Q.SUGGEST_FTS_WORDS.TABLE
            }

            else -> return null
        }
        return VerbNetControl.Result(table, projection, selection, selectionArgs, groupBy)
    }
}