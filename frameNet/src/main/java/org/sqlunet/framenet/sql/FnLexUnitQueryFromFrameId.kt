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
 * @param luId       target lex unit id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class FnLexUnitQueryFromFrameId(connection: SQLiteDatabase, luId: Long) : DBQuery(connection, QUERY) {

    init {
        setParams(luId)
    }

    /**
     * Lex unit id from the result set
     */
    val luId: Long
        get() = cursor!!.getLong(0)

    /**
     * Lex unit from the result set
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

    /**
     * Incorporated FE from the result set or null if none
     */
    val incorporatedFe: String
        get() = cursor!!.getString(5)

    /**
     * Frame id from the result set
     */
    val frameId: Long
        get() = cursor!!.getInt(6).toLong()

    companion object {

        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.FrameNetLexUnitQueryFromFrameId
    }
}
