/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.provider

import android.app.SearchManager

object QueriesLegacy {

    fun queryLegacy(code: Int, uriLast: String, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): PropBankControl.Result? {
        var r = queryLegacyMain(code, projection0, selection0, selectionArgs0)
        if (r == null) {
            r = queryLegacySearch(code, projection0, selection0, selectionArgs0)
            if (r == null) {
                r = queryLegacySuggest(code, uriLast)
            }
        }
        return r
    }

    private fun queryLegacyMain(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): PropBankControl.Result? {
        val table: String
        var selection = selection0
        var groupBy: String? = null
        when (code) {
            PropBankControl.PBROLESET -> {
                table = PropBankContract.PbRoleSets.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += PropBankContract.PbRoleSets.ROLESETID + " = ?"
            }

            PropBankControl.PBROLESETS -> table = PropBankContract.PbRoleSets.TABLE
            PropBankControl.PBROLESETS_X_BY_ROLESET -> {
                groupBy = PropBankContract.PbRoleSets_X.ROLESETID
                table = "pb_rolesets " +
                        "LEFT JOIN pb_members AS " + PropBankContract.AS_MEMBERS + " USING (rolesetid) " +
                        "LEFT JOIN pb_words AS " + PropBankContract.AS_PBWORDS + " USING (pbwordid) " +
                        "LEFT JOIN words AS " + PropBankContract.AS_WORDS + " USING (wordid)"
            }

            PropBankControl.PBROLESETS_X -> table = "pb_rolesets " +
                    "LEFT JOIN pb_members AS " + PropBankContract.AS_MEMBERS + " USING (rolesetid) " +
                    "LEFT JOIN pb_words AS " + PropBankContract.AS_PBWORDS + " USING (pbwordid) " +
                    "LEFT JOIN words AS " + PropBankContract.AS_WORDS + " USING (wordid)"

            PropBankControl.WORDS_PBROLESETS ->
                table = "words " +
                        "INNER JOIN pb_words USING (wordid) " + "INNER JOIN pb_rolesets USING (pbwordid)"

            PropBankControl.PBROLESETS_PBROLES ->
                table = "pb_rolesets " +
                        "INNER JOIN pb_roles USING (rolesetid) " +
                        "LEFT JOIN pb_argtypes USING (argtypeid) " +
                        "LEFT JOIN pb_funcs USING (funcid) " + "LEFT JOIN pb_thetas USING (thetaid)"

            PropBankControl.PBROLESETS_PBEXAMPLES_BY_EXAMPLE -> {
                groupBy = PropBankContract.AS_EXAMPLES + ".exampleid"
                table = "pb_rolesets " +
                        "INNER JOIN pb_examples AS " + PropBankContract.AS_EXAMPLES + " USING (rolesetid) " +
                        "LEFT JOIN pb_rels AS " + PropBankContract.AS_RELATIONS + " USING (exampleid) " +
                        "LEFT JOIN pb_args AS " + PropBankContract.AS_ARGS + " USING (exampleid) " +
                        "LEFT JOIN pb_argtypes USING (argtypeid) " +
                        "LEFT JOIN pb_funcs AS " + PropBankContract.AS_FUNCS + " ON (" + PropBankContract.AS_ARGS + ".funcid = " + PropBankContract.AS_FUNCS + ".funcid) " +
                        "LEFT JOIN pb_aspects USING (aspectid) " +
                        "LEFT JOIN pb_forms USING (formid) " +
                        "LEFT JOIN pb_tenses USING (tenseid) " +
                        "LEFT JOIN pb_voices USING (voiceid) " +
                        "LEFT JOIN pb_persons USING (personid) " +
                        "LEFT JOIN pb_roles USING (rolesetid,argtypeid) " + "LEFT JOIN pb_thetas USING (thetaid)"
            }

            PropBankControl.PBROLESETS_PBEXAMPLES -> table = "pb_rolesets " +
                    "INNER JOIN pb_examples AS " + PropBankContract.AS_EXAMPLES + " USING (rolesetid) " +
                    "LEFT JOIN pb_rels AS " + PropBankContract.AS_RELATIONS + " USING (exampleid) " +
                    "LEFT JOIN pb_args AS " + PropBankContract.AS_ARGS + " USING (exampleid) " +
                    "LEFT JOIN pb_argtypes USING (argtypeid) " +
                    "LEFT JOIN pb_funcs AS " + PropBankContract.AS_FUNCS + " ON (" + PropBankContract.AS_ARGS + ".funcid = " + PropBankContract.AS_FUNCS + ".funcid) " +
                    "LEFT JOIN pb_aspects USING (aspectid) " +
                    "LEFT JOIN pb_forms USING (formid) " +
                    "LEFT JOIN pb_tenses USING (tenseid) " +
                    "LEFT JOIN pb_voices USING (voiceid) " +
                    "LEFT JOIN pb_persons USING (personid) " +
                    "LEFT JOIN pb_roles USING (rolesetid,argtypeid) " + "LEFT JOIN pb_thetas USING (thetaid)"

            else -> return null
        }
        return PropBankControl.Result(table, projection0, selection, selectionArgs0, groupBy)
    }

    private fun queryLegacySearch(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): PropBankControl.Result? {
        val table: String
        var groupBy: String? = null
        when (code) {
            PropBankControl.LOOKUP_FTS_EXAMPLES -> table = "pb_examples_text_fts4"
            PropBankControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE -> {
                groupBy = "exampleid"
                table = "pb_examples_text_fts4 " +
                        "LEFT JOIN pb_rolesets USING (rolesetid)"
            }

            PropBankControl.LOOKUP_FTS_EXAMPLES_X -> table = "pb_examples_text_fts4 " +
                    "LEFT JOIN pb_rolesets USING (rolesetid)"

            else -> return null
        }
        return PropBankControl.Result(table, projection0, selection0, selectionArgs0, groupBy)
    }

    private fun queryLegacySuggest(code: Int, uriLast: String): PropBankControl.Result? {
        val table: String
        val projection: Array<String>
        val selection: String
        val selectionArgs: Array<String>
        when (code) {
            PropBankControl.SUGGEST_WORDS -> {
                if (SearchManager.SUGGEST_URI_PATH_QUERY == uriLast) {
                    return null
                }
                table = "pb_words INNER JOIN words USING (wordid)"
                projection = arrayOf(
                        "pbwordid AS _id",
                        "word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1,
                        "word AS " + SearchManager.SUGGEST_COLUMN_QUERY
                )
                selection = "word LIKE ? || '%'"
                selectionArgs = arrayOf(uriLast)
            }

            PropBankControl.SUGGEST_FTS_WORDS -> {
                if (SearchManager.SUGGEST_URI_PATH_QUERY == uriLast) {
                    return null
                }
                table = "pb_words_word_fts4"
                projection = arrayOf(
                        "pbwordid AS _id",
                        "word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1,
                        "word AS " + SearchManager.SUGGEST_COLUMN_QUERY
                )
                selection = "word MATCH ?"
                selectionArgs = arrayOf("$uriLast*")
            }

            else -> return null
        }
        return PropBankControl.Result(table, projection, selection, selectionArgs, null)
    }
}
