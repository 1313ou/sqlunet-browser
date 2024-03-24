/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * FrameNet governor query
 *
 * @param connection connection
 * @param luId       target lex unit id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class FnGovernorQueryFromLexUnitId(connection: SQLiteDatabase, luId: Long) : DBQuery(connection, QUERY) {

    init {
        setParams(luId)
    }

    /**
     * Governor id from the result set
     */
    val governorId: Long
        get() = cursor!!.getLong(0)

    /**
     * Word id from the result set
     */
    val wordId: Long
        get() = cursor!!.getLong(1)

    /**
     * Governor from the result set
     */
    val governor: String
        get() = cursor!!.getString(2)

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.FrameNetGovernorQueryFromLexUnitId
    }
}
