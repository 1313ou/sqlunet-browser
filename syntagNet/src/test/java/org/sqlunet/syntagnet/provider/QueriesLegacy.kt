/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.provider

object QueriesLegacy {

    fun queryLegacy(code: Int, uriLast: String?, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): SyntagNetControl.Result? {
        return queryLegacyMain(code, uriLast, projection0, selection0, selectionArgs0)
    }

    private fun queryLegacyMain(code: Int, @Suppress("unused") uriLast: String?, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): SyntagNetControl.Result? {
        val table: String
        var selection = selection0
        val groupBy: String? = null
        when (code) {
            SyntagNetControl.COLLOCATIONS -> {
                table = SyntagNetContract.SnCollocations.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += SyntagNetContract.SnCollocations.COLLOCATIONID + " = ?"
            }

            SyntagNetControl.COLLOCATIONS_X ->
                table = "sn_syntagms " +
                        "JOIN words AS " + SyntagNetContract.AS_WORDS1 + " ON (word1id = " + SyntagNetContract.AS_WORDS1 + ".wordid) " +
                        "JOIN words AS " + SyntagNetContract.AS_WORDS2 + " ON (word2id = " + SyntagNetContract.AS_WORDS2 + ".wordid) " +
                        "JOIN synsets AS " + SyntagNetContract.AS_SYNSETS1 + " ON (synset1id = " + SyntagNetContract.AS_SYNSETS1 + ".synsetid) " +
                        "JOIN synsets AS " + SyntagNetContract.AS_SYNSETS2 + " ON (synset2id = " + SyntagNetContract.AS_SYNSETS2 + ".synsetid)"

            else -> return null
        }
        return SyntagNetControl.Result(table, projection0, selection, selectionArgs0, groupBy)
    }
}
