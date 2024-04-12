/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.provider

/**
 * SyntagNet query control
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object SyntagNetControl {

    // table codes
    const val COLLOCATIONS = 10
    const val COLLOCATION1 = 11

    // join codes
    const val COLLOCATIONS_X = 100

    @JvmStatic
    fun queryMain(code: Int, uriLast: String, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): Result? {
        val table: String
        var selection = selection0
        when (code) {

            COLLOCATIONS -> table = Q.COLLOCATIONS.TABLE
            COLLOCATIONS_X -> table = Q.COLLOCATIONS_X.TABLE
            COLLOCATION1 -> {
                table = Q.COLLOCATIONS.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += Q.COLLOCATION1.SELECTION.replace("#\\{uri_last\\}".toRegex(), uriLast) // V.SYNTAGMID + " = " + uriLast
            }

            else -> return null
        }
        return Result(table, projection0, selection, selectionArgs0, null)
    }

    data class Result(@JvmField val table: String, @JvmField val projection: Array<String>?, @JvmField val selection: String?, @JvmField val selectionArgs: Array<String>?, @JvmField val groupBy: String?) {

        override fun toString(): String {
            return "table='$table'\nprojection=${projection.contentToString()}\nselection='$selection'\nselectionArgs=${selectionArgs.contentToString()}\ngroupBy=$groupBy"
        }
    }
}
