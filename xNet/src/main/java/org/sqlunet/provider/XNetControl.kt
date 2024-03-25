/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.provider

import org.sqlunet.xnet.provider.Q
import org.sqlunet.xnet.provider.V

/**
 * XNet query control
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object XNetControl {

    // table codes
    const val PREDICATEMATRIX = 200
    const val PREDICATEMATRIX_VERBNET = 210
    const val PREDICATEMATRIX_PROPBANK = 220
    const val PREDICATEMATRIX_FRAMENET = 230

    // join codes
    const val WORDS_FNWORDS_PBWORDS_VNWORDS = 100
    const val WORDS_PRONUNCIATIONS_FNWORDS_PBWORDS_VNWORDS = 101
    const val WORDS_PBWORDS_VNWORDS = 110
    const val WORDS_VNWORDS_VNCLASSES = 310
    const val WORDS_VNWORDS_VNCLASSES_U = 311
    const val WORDS_VNWORDS_VNCLASSES_1 = 312
    const val WORDS_VNWORDS_VNCLASSES_2 = 313
    const val WORDS_VNWORDS_VNCLASSES_1U2 = 314
    const val WORDS_PBWORDS_PBROLESETS = 320
    const val WORDS_PBWORDS_PBROLESETS_U = 321
    const val WORDS_PBWORDS_PBROLESETS_1 = 322
    const val WORDS_PBWORDS_PBROLESETS_2 = 323
    const val WORDS_PBWORDS_PBROLESETS_1U2 = 324
    const val WORDS_FNWORDS_FNFRAMES_U = 331
    const val WORDS_FNWORDS_FNFRAMES_1 = 332
    const val WORDS_FNWORDS_FNFRAMES_2 = 333
    const val WORDS_FNWORDS_FNFRAMES_1U2 = 334
    const val SOURCES = 400
    const val META = 500

    @JvmStatic
    fun queryMain(code: Int, @Suppress("unused") uriLast: String?, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): Result? {
        val table: String
        var projection = projection0
        var selection = selection0
        var selectionArgs = selectionArgs0
        var groupBy: String? = null
        val orderBy: String? = null
        when (code) {
            PREDICATEMATRIX -> table = Q.PREDICATEMATRIX.TABLE

            PREDICATEMATRIX_VERBNET -> table = Q.PREDICATEMATRIX_VERBNET.TABLE

            PREDICATEMATRIX_PROPBANK -> table = Q.PREDICATEMATRIX_PROPBANK.TABLE

            PREDICATEMATRIX_FRAMENET -> table = Q.PREDICATEMATRIX_FRAMENET.TABLE

            SOURCES -> table = Q.SOURCES.TABLE

            META -> table = Q.META.TABLE

            WORDS_FNWORDS_PBWORDS_VNWORDS -> {
                table = Q.WORDS_FNWORDS_PBWORDS_VNWORDS.TABLE
                groupBy = Q.WORDS_FNWORDS_PBWORDS_VNWORDS.GROUPBY
            }

            WORDS_PRONUNCIATIONS_FNWORDS_PBWORDS_VNWORDS -> {
                table = Q.WORDS_PRONUNCIATIONS_FNWORDS_PBWORDS_VNWORDS.TABLE
                groupBy = Q.WORDS_PRONUNCIATIONS_FNWORDS_PBWORDS_VNWORDS.GROUPBY
            }

            WORDS_PBWORDS_VNWORDS -> {
                table = Q.WORDS_PBWORDS_VNWORDS.TABLE
                groupBy = Q.WORDS_PBWORDS_VNWORDS.GROUPBY
            }

            WORDS_VNWORDS_VNCLASSES -> {
                table = Q.WORDS_VNWORDS_VNCLASSES.TABLE
                groupBy = Q.WORDS_VNWORDS_VNCLASSES.GROUPBY
            }

            WORDS_PBWORDS_PBROLESETS -> {
                table = Q.WORDS_PBWORDS_PBROLESETS.TABLE
                groupBy = String.format("%s,%s,%s", V.WORDID, V.SYNSETID, V.ROLESETID)
            }

            WORDS_VNWORDS_VNCLASSES_1 -> {
                table = Q.WORDS_VNWORDS_VNCLASSES_1.TABLE
                projection = Q.WORDS_VNWORDS_VNCLASSES_1.PROJECTION
            }

            WORDS_VNWORDS_VNCLASSES_2 -> {
                table = Q.WORDS_VNWORDS_VNCLASSES_2.TABLE
                projection = Q.WORDS_VNWORDS_VNCLASSES_2.PROJECTION
            }

            WORDS_VNWORDS_VNCLASSES_1U2 -> table = Q.WORDS_VNWORDS_VNCLASSES_1U2.TABLE //.replaceAll("#\\{selection\\}", selection)

            WORDS_VNWORDS_VNCLASSES_U -> {
                table = Q.WORDS_VNWORDS_VNCLASSES_1U2.TABLE.replace("#\\{selection\\}".toRegex(), selection!!)
                projection = BaseProvider.prependProjection(projection, "GROUP_CONCAT(DISTINCT source) AS sources")
                selection = null
                selectionArgs = Utils.unfoldSelectionArgs(selectionArgs)
                groupBy = String.format("%s,%s,%s", V.WORDID, V.SYNSETID, V.CLASSID)
            }

            WORDS_PBWORDS_PBROLESETS_1 -> {
                table = Q.WORDS_PBWORDS_PBROLESETS_1.TABLE
                projection = Q.WORDS_PBWORDS_PBROLESETS_1.PROJECTION
            }

            WORDS_PBWORDS_PBROLESETS_2 -> {
                table = Q.WORDS_PBWORDS_PBROLESETS_2.TABLE
                projection = Q.WORDS_PBWORDS_PBROLESETS_2.PROJECTION
            }

            WORDS_PBWORDS_PBROLESETS_1U2 -> table = Q.WORDS_PBWORDS_PBROLESETS_1U2.TABLE //.replaceAll("#\\{selection\\}", selection)

            WORDS_PBWORDS_PBROLESETS_U -> {
                table = Q.WORDS_PBWORDS_PBROLESETS_1U2.TABLE.replace("#\\{selection\\}".toRegex(), selection!!)
                projection = BaseProvider.prependProjection(projection, "GROUP_CONCAT(DISTINCT source) AS sources")
                selection = null
                selectionArgs = Utils.unfoldSelectionArgs(selectionArgs)
                groupBy = String.format("%s,%s,%s", V.WORDID, V.SYNSETID, V.ROLESETID)
            }

            WORDS_FNWORDS_FNFRAMES_1 -> {
                table = Q.WORDS_FNWORDS_FNFRAMES_1.TABLE
                projection = Q.WORDS_FNWORDS_FNFRAMES_1.PROJECTION
            }

            WORDS_FNWORDS_FNFRAMES_2 -> {
                table = Q.WORDS_FNWORDS_FNFRAMES_2.TABLE
                projection = Q.WORDS_FNWORDS_FNFRAMES_2.PROJECTION
            }

            WORDS_FNWORDS_FNFRAMES_1U2 -> table = Q.WORDS_FNWORDS_FNFRAMES_1U2.TABLE //.replaceAll("\\$\\{selection\\}", selection)
            WORDS_FNWORDS_FNFRAMES_U -> {
                table = Q.WORDS_FNWORDS_FNFRAMES_1U2.TABLE.replace("#\\{selection\\}".toRegex(), selection!!)
                projection = BaseProvider.prependProjection(projection, "GROUP_CONCAT(DISTINCT source) AS sources")
                selection = null
                selectionArgs = Utils.unfoldSelectionArgs(selectionArgs)
                groupBy = String.format("%s,%s,%s", V.WORDID, V.SYNSETID, V.FRAMEID)
            }

            else -> return null
        }
        return Result(table, projection, selection, selectionArgs, groupBy, orderBy)
    }

    class Result(@JvmField val table: String, @JvmField val projection: Array<String>?, @JvmField val selection: String?, @JvmField val selectionArgs: Array<String>?, @JvmField val groupBy: String?, @JvmField val orderBy: String?)
}
