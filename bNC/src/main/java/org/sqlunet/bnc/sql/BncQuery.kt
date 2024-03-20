/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.bnc.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * BNC query
 *
 * @param connection connection
 * @param params     parameters
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class BncQuery(connection: SQLiteDatabase?, vararg params: Any) : DBQuery(connection, if (params.size > 1) QUERYWITHPOS else QUERY) {

    init {
        setParams(*params)
    }

    private val pos: String
        get() = cursor!!.getString(0)

    private val posName: String
        get() = cursor!!.getString(1)

    private val freq: Int?
        get() = if (cursor!!.isNull(2)) null else cursor!!.getInt(2)

    private val range: Int?
        get() = if (cursor!!.isNull(3)) null else cursor!!.getInt(3)

    private val disp: Float?
        get() = if (cursor!!.isNull(4)) null else cursor!!.getFloat(4)

    // conversation / task

    private val convFreq: Int?
        get() = if (cursor!!.isNull(5)) null else cursor!!.getInt(5)

    private val convRange: Int?
        get() = if (cursor!!.isNull(6)) null else cursor!!.getInt(6)

    private val convDisp: Float?
        get() = if (cursor!!.isNull(7)) null else cursor!!.getFloat(7)

    private val taskFreq: Int?
        get() = if (cursor!!.isNull(8)) null else cursor!!.getInt(8)

    private val taskRange: Int?
        get() = if (cursor!!.isNull(9)) null else cursor!!.getInt(9)

    private val taskDisp: Float?
        get() = if (cursor!!.isNull(10)) null else cursor!!.getFloat(10)

    // imagination / information

    private val imagFreq: Int?
        get() = if (cursor!!.isNull(11)) null else cursor!!.getInt(11)

    private val imagRange: Int?
        get() = if (cursor!!.isNull(12)) null else cursor!!.getInt(12)

    private val imagDisp: Float?
        get() = if (cursor!!.isNull(13)) null else cursor!!.getFloat(13)

    private val infFreq: Int?
        get() = if (cursor!!.isNull(14)) null else cursor!!.getInt(14)

    private val infRange: Int?
        get() = if (cursor!!.isNull(15)) null else cursor!!.getInt(15)

    private val infDisp: Float?
        get() = if (cursor!!.isNull(16)) null else cursor!!.getFloat(16)

    // spoken / written

    private val spokenFreq: Int?
        get() = if (cursor!!.isNull(17)) null else cursor!!.getInt(17)

    private val spokenRange: Int?
        get() = if (cursor!!.isNull(18)) null else cursor!!.getInt(18)

    private val spokenDisp: Float?
        get() = if (cursor!!.isNull(19)) null else cursor!!.getFloat(19)

    private val writtenFreq: Int?
        get() = if (cursor!!.isNull(20)) null else cursor!!.getInt(20)

    private val writtenRange: Int?
        get() = if (cursor!!.isNull(21)) null else cursor!!.getInt(21)

    private val writtenDisp: Float?
        get() = if (cursor!!.isNull(22)) null else cursor!!.getFloat(22)

    val data: BncData
        get() {
            return BncData(
                pos, posName,
                freq, range, disp, convFreq, convRange, convDisp, taskFreq, taskRange, taskDisp,
                imagFreq, imagRange, imagDisp, infFreq, infRange, infDisp,
                spokenFreq, spokenRange, spokenDisp, writtenFreq, writtenRange, writtenDisp
            )
        }

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.BNCQueryFromWordId

        /**
         * `QUERYWITHPOS` is the SQL statement
         */
        private const val QUERYWITHPOS = SqLiteDialect.BNCQueryFromWordIdAndPos
    }
}
