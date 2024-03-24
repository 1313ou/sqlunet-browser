/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * FrameNet lex unit query
 *
 * @param connection connection
 * @param targetLuId target luId
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class FnLexUnitQuery(connection: SQLiteDatabase, targetLuId: Long) : DBQuery(connection, QUERY) {

    init {
        setParams(targetLuId)
    }

    /**
     * Frame id from the result set
     */
    val luId: Long
        get() = cursor!!.getLong(0)

    /**
     * Frame from the result set
     */
    val lexUnit: String
        get() = cursor!!.getString(1)

    /**
     * Pos from the result set
     */
    val pos: String
        get() = cursor!!.getString(2)

    /**
     * Definition from the result set
     */
    val definition: String
        get() = cursor!!.getString(3)

    /**
     * Definition dictionary from the result set
     */
    val dictionary: String
        get() = cursor!!.getString(4)

    val incorporatedFe: String
        /**
         * Incorporated FE from the result set or null if none
         */
        get() = cursor!!.getString(5)

    val frameId: Long
        /**
         * annoSet Id from the result set
         */
        get() = cursor!!.getInt(6).toLong()

    val frame: String
        /**
         * Frame from the result set or null if none
         */
        get() = cursor!!.getString(7)

    val frameDescription: String
        /**
         * Frame description from the result set or null if none
         */
        get() = cursor!!.getString(8)

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.FrameNetLexUnitQuery
    }
}
