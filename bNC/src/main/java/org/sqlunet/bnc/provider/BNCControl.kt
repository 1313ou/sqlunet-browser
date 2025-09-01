/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.bnc.provider

/**
 * BNC query control
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object BNCControl {

    // table codes
    const val BNC = 11

    // join tables
    const val WORDS_BNC = 100

    fun queryMain(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): Result? {
        val table: String
        var selection = selection0
        when (code) {
            BNC -> {
                table = Q.BNCS.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += Q.BNCS.SELECTION
            }

            WORDS_BNC -> table = Q.WORDS_BNCS.TABLE
            else -> return null
        }
        return Result(table, projection0, selection, selectionArgs0, null)
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
