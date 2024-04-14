/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.provider

import android.app.SearchManager

/**
 * VerbNet query control
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object VerbNetControl {

    // table codes
    const val VNCLASS1 = 10
    const val VNCLASSES = 11
    const val VNCLASSES_X_BY_VNCLASS = 20

    // join codes
    const val WORDS_VNCLASSES = 100
    const val VNCLASSES_VNMEMBERS_X_BY_WORD = 110
    const val VNCLASSES_VNROLES_X_BY_VNROLE = 120
    const val VNCLASSES_VNFRAMES_X_BY_VNFRAME = 130

    // search text codes
    const val LOOKUP_FTS_EXAMPLES = 501
    const val LOOKUP_FTS_EXAMPLES_X = 511
    const val LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE = 512

    // suggest
    const val SUGGEST_WORDS = 601
    const val SUGGEST_FTS_WORDS = 602

    fun queryMain(code: Int, uriLast: String, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): Result? {
        val table: String
        var selection = selection0
        var groupBy: String? = null
        when (code) {
            VNCLASSES -> table = Q.VNCLASSES.TABLE
            VNCLASSES_X_BY_VNCLASS -> {
                table = Q.VNCLASSES_X_BY_VNCLASS.TABLE
                groupBy = Q.VNCLASSES_X_BY_VNCLASS.GROUPBY
            }

            VNCLASS1 -> {
                table = Q.VNCLASS1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += Q.VNCLASS1.SELECTION.replace("#\\{uri_last\\}".toRegex(), uriLast) // V.CLASSID + " = " + uriLast
            }

            WORDS_VNCLASSES -> table = Q.WORDS_VNCLASSES.TABLE
            VNCLASSES_VNMEMBERS_X_BY_WORD -> {
                table = Q.VNCLASSES_VNMEMBERS_X_BY_WORD.TABLE
                groupBy = V.VNWORDID
            }

            VNCLASSES_VNROLES_X_BY_VNROLE -> {
                table = Q.VNCLASSES_VNROLES_X_BY_VNROLE.TABLE
                groupBy = V.ROLEID
            }

            VNCLASSES_VNFRAMES_X_BY_VNFRAME -> {
                table = Q.VNCLASSES_VNFRAMES_X_BY_VNFRAME.TABLE
                groupBy = V.FRAMEID
            }

            else -> return null
        }
        return Result(table, projection0, selection, selectionArgs0, groupBy)
    }

    fun querySearch(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): Result? {
        val table: String
        var groupBy: String? = null
        when (code) {
            LOOKUP_FTS_EXAMPLES -> table = Q.LOOKUP_FTS_EXAMPLES.TABLE
            LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE -> {
                table = Q.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE.TABLE
                groupBy = V.EXAMPLEID
            }

            LOOKUP_FTS_EXAMPLES_X -> table = Q.LOOKUP_FTS_EXAMPLES_X.TABLE
            else -> return null
        }
        return Result(table, projection0, selection0, selectionArgs0, groupBy)
    }

    fun querySuggest(code: Int, uriLast: String): Result? {
        val table: String
        val projection: Array<String>
        val selection: String
        val selectionArgs: Array<String>
        when (code) {
            SUGGEST_WORDS -> {
                if (SearchManager.SUGGEST_URI_PATH_QUERY == uriLast) {
                    return null
                }
                table = Q.SUGGEST_WORDS.TABLE
                projection = Q.SUGGEST_WORDS.PROJECTION
                projection[1] = projection[1].replace("#\\{suggest_text_1\\}".toRegex(), SearchManager.SUGGEST_COLUMN_TEXT_1)
                projection[2] = projection[2].replace("#\\{suggest_query\\}".toRegex(), SearchManager.SUGGEST_COLUMN_QUERY)
                selection = Q.SUGGEST_WORDS.SELECTION
                selectionArgs = arrayOf(Q.SUGGEST_WORDS.ARGS[0].replace("#\\{uri_last\\}".toRegex(), uriLast))
            }

            SUGGEST_FTS_WORDS -> {
                if (SearchManager.SUGGEST_URI_PATH_QUERY == uriLast) {
                    return null
                }
                table = Q.SUGGEST_FTS_WORDS.TABLE
                projection = Q.SUGGEST_FTS_WORDS.PROJECTION
                projection[1] = projection[1].replace("#\\{suggest_text_1\\}".toRegex(), SearchManager.SUGGEST_COLUMN_TEXT_1)
                projection[2] = projection[2].replace("#\\{suggest_query\\}".toRegex(), SearchManager.SUGGEST_COLUMN_QUERY)
                selection = Q.SUGGEST_FTS_WORDS.SELECTION
                selectionArgs = arrayOf(Q.SUGGEST_FTS_WORDS.ARGS[0].replace("#\\{uri_last\\}".toRegex(), uriLast))
            }

            else -> return null
        }
        return Result(table, projection, selection, selectionArgs, null)
    }

    data class Result(val table: String, val projection: Array<String>?, val selection: String?, val selectionArgs: Array<String>?, val groupBy: String?) {

        override fun toString(): String {
            return "table='$table'\nprojection=${projection.contentToString()}\nselection='$selection'\nselectionArgs=${selectionArgs.contentToString()}\ngroupBy=$groupBy"
        }
    }
}
