/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.bnc.provider

object QueriesLegacy {

    fun queryLegacy(code: Int, uriLast: String?, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): BNCControl.Result? {
        return queryLegacyMain(code, uriLast, projection0, selection0, selectionArgs0)
    }

    private fun queryLegacyMain(code: Int, @Suppress("unused") uriLast: String?, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): BNCControl.Result? {
        val table: String
        var selection = selection0
        val groupBy: String? = null
        when (code) {
            BNCControl.BNC -> {
                table = BNCContract.BNCs.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += BNCContract.BNCs.POSID + " = ?"
            }

            BNCControl.WORDS_BNC ->
                table = "bnc_bncs " +
                        "LEFT JOIN bnc_spwrs USING (wordid, posid) " +
                        "LEFT JOIN bnc_convtasks USING (wordid, posid) " + "LEFT JOIN bnc_imaginfs USING (wordid, posid) "

            else -> return null
        }
        return BNCControl.Result(table, projection0, selection, selectionArgs0, groupBy)
    }
}
