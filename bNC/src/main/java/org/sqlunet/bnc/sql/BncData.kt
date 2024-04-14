/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.bnc.sql

import android.database.sqlite.SQLiteDatabase

data class BncData(

    var pos: String? = null,

    var posName: String? = null,

    var freq: Int? = null,

    var range: Int? = null,

    var disp: Float? = null,

    var convFreq: Int? = null,

    var convRange: Int? = null,

    var convDisp: Float? = null,

    var taskFreq: Int? = null,

    var taskRange: Int? = null,

    var taskDisp: Float? = null,

    var imagFreq: Int? = null,

    var imagRange: Int? = null,

    var imagDisp: Float? = null,

    var infFreq: Int? = null,

    var infRange: Int? = null,

    var infDisp: Float? = null,

    var spokenFreq: Int? = null,

    var spokenRange: Int? = null,

    var spokenDisp: Float? = null,

    var writtenFreq: Int? = null,

    var writtenRange: Int? = null,

    var writtenDisp: Float? = null,
) {

    companion object {

        fun makeData(connection: SQLiteDatabase, targetWord: String): List<BncData?>? {
            var result: MutableList<BncData?>? = null
            BncQuery(connection, targetWord).use {
                it.execute()
                while (it.next()) {
                    val data = it.data
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(data)
                }
            }
            return result
        }

        fun makeData(connection: SQLiteDatabase, targetWordId: Long, targetPos: Char?): List<BncData?>? {
            var result: MutableList<BncData?>? = null
            val bncQuery = if (targetPos != null) BncQuery(connection, targetWordId, targetPos) else BncQuery(connection, targetWordId)
            bncQuery.use {
                it.execute()
                while (it.next()) {
                    val data = it.data
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
