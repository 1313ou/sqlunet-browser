/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.provider

import android.app.SearchManager

object QueriesLegacy {

    fun queryLegacy(code: Int, uriLast: String, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): VerbNetControl.Result? {
        var r = queryLegacyMain(code, uriLast, projection0, selection0, selectionArgs0)
        if (r == null) {
            r = queryLegacySearch(code, projection0, selection0, selectionArgs0)
            if (r == null) {
                r = queryLegacySuggest(code, uriLast)
            }
        }
        return r
    }

    private fun queryLegacyMain(code: Int, uriLast: String, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): VerbNetControl.Result? {
        val table: String
        var selection = selection0
        var groupBy: String? = null
        when (code) {
            VerbNetControl.VNCLASSES -> table = VerbNetContract.VnClasses.TABLE
            VerbNetControl.VNCLASSES_X_BY_VNCLASS -> {
                groupBy = "classid"
                table = "vn_classes " +
                        "LEFT JOIN vn_members_groupings USING (classid) " + "LEFT JOIN vn_groupings USING (groupingid)"
            }

            VerbNetControl.VNCLASS1 -> {
                table = VerbNetContract.VnClasses.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += VerbNetContract.VnClasses.CLASSID + " = " + uriLast
            }

            VerbNetControl.WORDS_VNCLASSES ->
                table = "words " +
                        "INNER JOIN vn_words USING (wordid) " +
                        "INNER JOIN vn_members_senses USING (vnwordid, wordid) " + "LEFT JOIN vn_classes USING (classid)"

            VerbNetControl.VNCLASSES_VNMEMBERS_X_BY_WORD -> {
                groupBy = "vnwordid"
                table = "words " +
                        "INNER JOIN vn_words USING (wordid) " +
                        "INNER JOIN vn_members_senses USING (vnwordid, wordid) " +
                        "LEFT JOIN vn_members_groupings USING (classid, vnwordid) " +
                        "LEFT JOIN vn_groupings USING (groupingid) " + "LEFT JOIN synsets USING (synsetid)"
            }

            VerbNetControl.VNCLASSES_VNROLES_X_BY_VNROLE -> {
                groupBy = "roleid"
                table = "vn_classes " +
                        "INNER JOIN vn_roles USING (classid) " +
                        "INNER JOIN vn_roletypes USING (roletypeid) " + "LEFT JOIN vn_restrs USING (restrsid)"
            }

            VerbNetControl.VNCLASSES_VNFRAMES_X_BY_VNFRAME -> {
                groupBy = "frameid"
                table = "vn_classes_frames " +
                        "INNER JOIN vn_frames USING (frameid) " +
                        "LEFT JOIN vn_framenames USING (framenameid) " +
                        "LEFT JOIN vn_framesubnames USING (framesubnameid) " +
                        "LEFT JOIN vn_syntaxes USING (syntaxid) " +
                        "LEFT JOIN vn_semantics USING (semanticsid) " +
                        "LEFT JOIN vn_frames_examples USING (frameid) " + "LEFT JOIN vn_examples USING (exampleid)"
            }

            else -> return null
        }
        return VerbNetControl.Result(table, projection0, selection, selectionArgs0, groupBy)
    }

    private fun queryLegacySearch(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): VerbNetControl.Result? {
        val table: String
        var groupBy: String? = null
        when (code) {
            VerbNetControl.LOOKUP_FTS_EXAMPLES -> table = "vn_examples_example_fts4"
            VerbNetControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE -> {
                groupBy = "exampleid"
                table = "vn_examples_example_fts4 " +
                        "LEFT JOIN vn_classes USING (classid)"
            }

            VerbNetControl.LOOKUP_FTS_EXAMPLES_X -> table = "vn_examples_example_fts4 " +
                    "LEFT JOIN vn_classes USING (classid)"

            else -> return null
        }
        return VerbNetControl.Result(table, projection0, selection0, selectionArgs0, groupBy)
    }

    private fun queryLegacySuggest(code: Int, uriLast: String): VerbNetControl.Result? {
        val table: String
        val projection: Array<String>
        val selection: String
        val selectionArgs: Array<String>
        when (code) {
            VerbNetControl.SUGGEST_WORDS -> {
                if (SearchManager.SUGGEST_URI_PATH_QUERY == uriLast) {
                    return null
                }
                table = "vn_words INNER JOIN words USING (wordid)"
                projection = arrayOf(
                    "vnwordid AS _id",
                    "word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1,
                    "word AS " + SearchManager.SUGGEST_COLUMN_QUERY
                )
                selection = "word LIKE ? || '%'"
                selectionArgs = arrayOf(uriLast)
            }

            VerbNetControl.SUGGEST_FTS_WORDS -> {
                if (SearchManager.SUGGEST_URI_PATH_QUERY == uriLast) {
                    return null
                }
                table = "vn_words_word_fts4"
                projection = arrayOf(
                    "vnwordid AS _id",
                    "word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1,
                    "word AS " + SearchManager.SUGGEST_COLUMN_QUERY
                )
                selection = "word MATCH ?"
                selectionArgs = arrayOf("$uriLast*")
            }

            else -> return null
        }
        return VerbNetControl.Result(table, projection, selection, selectionArgs, null)
    }
}
