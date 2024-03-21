/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.bnc.sql

import android.database.sqlite.SQLiteDatabase

data class BncData(
    @JvmField
    var pos: String? = null,
    @JvmField
    var posName: String? = null,
    @JvmField
    var freq: Int? = null,
    @JvmField
    var range: Int? = null,
    @JvmField
    var disp: Float? = null,
    @JvmField
    var convFreq: Int? = null,
    @JvmField
    var convRange: Int? = null,
    @JvmField
    var convDisp: Float? = null,
    @JvmField
    var taskFreq: Int? = null,
    @JvmField
    var taskRange: Int? = null,
    @JvmField
    var taskDisp: Float? = null,
    @JvmField
    var imagFreq: Int? = null,
    @JvmField
    var imagRange: Int? = null,
    @JvmField
    var imagDisp: Float? = null,
    @JvmField
    var infFreq: Int? = null,
    @JvmField
    var infRange: Int? = null,
    @JvmField
    var infDisp: Float? = null,
    @JvmField
    var spokenFreq: Int? = null,
    @JvmField
    var spokenRange: Int? = null,
    @JvmField
    var spokenDisp: Float? = null,
    @JvmField
    var writtenFreq: Int? = null,
    @JvmField
    var writtenRange: Int? = null,
    @JvmField
    var writtenDisp: Float? = null,
) {

    companion object {
        @JvmStatic
        fun makeData(connection: SQLiteDatabase, targetWord: String): List<BncData?>? {
            var result: MutableList<BncData?>? = null
            BncQuery(connection, targetWord).use { query ->
                query.execute()
                while (query.next()) {
                    val data = query.data
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(data)
                }
            }
            return result
        }

        @JvmStatic
        fun makeData(connection: SQLiteDatabase, targetWordId: Long, targetPos: Char?): List<BncData?>? {
            var result: MutableList<BncData?>? = null
            val bncQuery = if (targetPos != null) BncQuery(connection, targetWordId, targetPos) else BncQuery(connection, targetWordId)
            bncQuery.use { query ->
                query.execute()
                while (query.next()) {
                    val data = query.data
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(data)
                }
            }
            return result
        }
    }
}
