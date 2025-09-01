/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.provider

import android.app.SearchManager
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_X

/**
 * PropBank query control
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object PropBankControl {

    // table codes
    const val PBROLESET1 = 10
    const val PBROLESETS = 11

    // join codes
    const val PBROLESETS_X = 100
    const val PBROLESETS_X_BY_ROLESET = 101
    const val WORDS_PBROLESETS = 110
    const val PBROLESETS_PBROLES = 120
    const val PBROLESETS_PBEXAMPLES = 130
    const val PBROLESETS_PBEXAMPLES_BY_EXAMPLE = 131

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
            PBROLESETS -> table = Q.PBROLESETS.TABLE
            PBROLESET1 -> {
                table = Q.PBROLESET1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += Q.PBROLESET1.SELECTION.replace("#\\{uri_last\\}".toRegex(), uriLast) // V.ROLESETID + " = " + uriLast
            }

            PBROLESETS_X_BY_ROLESET -> {
                table = Q.PBROLESETS_X.TABLE
                groupBy = PbRoleSets_X.ROLESETID
            }

            PBROLESETS_X -> table = Q.PBROLESETS_X.TABLE
            WORDS_PBROLESETS -> table = Q.WORDS_PBROLESETS.TABLE
            PBROLESETS_PBROLES -> table = Q.PBROLESETS_PBROLES.TABLE
            PBROLESETS_PBEXAMPLES_BY_EXAMPLE -> {
                table = Q.PBROLESETS_PBEXAMPLES_BY_EXAMPLE.TABLE
                groupBy = Q.PBROLESETS_PBEXAMPLES_BY_EXAMPLE.GROUPBY
            }

            PBROLESETS_PBEXAMPLES -> table = Q.PBROLESETS_PBEXAMPLES.TABLE
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
                groupBy = Q.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE.GROUPBY
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

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Result

            if (table != other.table) return false
            if (!projection.contentEquals(other.projection)) return false
            if (selection != other.selection) return false
            if (!selectionArgs.contentEquals(other.selectionArgs)) return false
            if (groupBy != other.groupBy) return false

            return true
        }

        override fun hashCode(): Int {
            var result = table.hashCode()
            result = 31 * result + (projection?.contentHashCode() ?: 0)
            result = 31 * result + (selection?.hashCode() ?: 0)
            result = 31 * result + (selectionArgs?.contentHashCode() ?: 0)
            result = 31 * result + (groupBy?.hashCode() ?: 0)
            return result
        }
    }
}
