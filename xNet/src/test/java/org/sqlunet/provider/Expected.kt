/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.provider

import org.sqlunet.xnet.provider.Q

/**
 * Queries factory, which will execute on the development machine (host).
 */
object Expected {

    @JvmStatic
    fun expected(code: Int, uriLast: String, projection0: Array<String>, selection0: String?, selectionArgs0: Array<String>?): XNetControl.Result? {
        val table: String
        var projection = projection0
        var selection = selection0
        var selectionArgs = selectionArgs0
        var groupBy: String? = null
        var orderBy: String? = null

        when (code) {
            XNetControl.PREDICATEMATRIX -> table = Q.PREDICATEMATRIX.TABLE
            XNetControl.PREDICATEMATRIX_VERBNET -> table = Q.PREDICATEMATRIX_VERBNET.TABLE
            XNetControl.PREDICATEMATRIX_PROPBANK -> table = Q.PREDICATEMATRIX_PROPBANK.TABLE
            XNetControl.PREDICATEMATRIX_FRAMENET -> table = Q.PREDICATEMATRIX_FRAMENET.TABLE

            XNetControl.WORDS_FNWORDS_PBWORDS_VNWORDS -> {
                table = Q.WORDS_FNWORDS_PBWORDS_VNWORDS.TABLE
                groupBy = Q.WORDS_FNWORDS_PBWORDS_VNWORDS.GROUPBY
            }

            XNetControl.WORDS_PRONUNCIATIONS_FNWORDS_PBWORDS_VNWORDS -> table = Q.WORDS_PRONUNCIATIONS_FNWORDS_PBWORDS_VNWORDS.TABLE

            XNetControl.WORDS_PBWORDS_VNWORDS -> {
                table = Q.WORDS_PBWORDS_VNWORDS.TABLE
                groupBy = Q.WORDS_PBWORDS_VNWORDS.GROUPBY
            }

            XNetControl.WORDS_VNWORDS_VNCLASSES -> {
                table = Q.WORDS_VNWORDS_VNCLASSES.TABLE
                groupBy = Q.WORDS_VNWORDS_VNCLASSES.GROUPBY
            }

            XNetControl.WORDS_VNWORDS_VNCLASSES_U -> {
                table = Q.WORDS_VNWORDS_VNCLASSES_U.TABLE
                //groupBy = Q.WORDS_VNWORDS_VNCLASSES_U.GROUPBY
            }

            XNetControl.WORDS_VNWORDS_VNCLASSES_1 -> table = Q.WORDS_VNWORDS_VNCLASSES_1.TABLE
            XNetControl.WORDS_VNWORDS_VNCLASSES_2 -> table = Q.WORDS_VNWORDS_VNCLASSES_2.TABLE
            XNetControl.WORDS_VNWORDS_VNCLASSES_1U2 -> table = Q.WORDS_VNWORDS_VNCLASSES_1U2.TABLE
            XNetControl.WORDS_PBWORDS_PBROLESETS -> {
                table = Q.WORDS_PBWORDS_PBROLESETS.TABLE
                groupBy = Q.WORDS_PBWORDS_PBROLESETS.GROUPBY
            }
            XNetControl.WORDS_PBWORDS_PBROLESETS_U -> table = Q.WORDS_PBWORDS_PBROLESETS_U.TABLE
            XNetControl.WORDS_PBWORDS_PBROLESETS_1 -> table = Q.WORDS_PBWORDS_PBROLESETS_1.TABLE
            XNetControl.WORDS_PBWORDS_PBROLESETS_2 -> table = Q.WORDS_PBWORDS_PBROLESETS_2.TABLE
            XNetControl.WORDS_PBWORDS_PBROLESETS_1U2 -> table = Q.WORDS_PBWORDS_PBROLESETS_1U2.TABLE

            XNetControl.WORDS_FNWORDS_FNFRAMES_U -> table = Q.WORDS_FNWORDS_FNFRAMES_U.TABLE
            XNetControl.WORDS_FNWORDS_FNFRAMES_1 -> table = Q.WORDS_FNWORDS_FNFRAMES_1.TABLE
            XNetControl.WORDS_FNWORDS_FNFRAMES_2 -> table = Q.WORDS_FNWORDS_FNFRAMES_2.TABLE
            XNetControl.WORDS_FNWORDS_FNFRAMES_1U2 -> table = Q.WORDS_FNWORDS_FNFRAMES_1U2.TABLE
            XNetControl.SOURCES -> table = Q.SOURCES.TABLE
            XNetControl.META -> table = Q.META.TABLE

            else -> return null
        }
        return XNetControl.Result(table, projection, selection, selectionArgs, groupBy, orderBy)
    }
}