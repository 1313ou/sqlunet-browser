/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.framenet.FnFlags
import org.sqlunet.sql.DBQuery
import java.util.Locale

/**
 * FrameNet lex unit query
 *
 * @param connection connection
 * @param wordId     target word id
 * @param pos        target pos or null
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class FnLexUnitQueryFromWordId(connection: SQLiteDatabase, wordId: Long, pos: Char?) : DBQuery(
    connection,
    if (pos != null)
        (if (FnFlags.standAlone) QUERYWITHPOSFN else QUERYWITHPOS)
    else if (FnFlags.standAlone) QUERYFN else QUERY
) {

    init {
        setParams(wordId, pos?.toString()?.uppercase(Locale.getDefault())!!)
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
    val incorporatedFe: String?
        get() = cursor!!.getString(5)

    /**
     * Frame id from the result set
     */
    val frameId: Long
        get() = cursor!!.getInt(6).toLong()

    /**
     * Frame from the result set or null if none
     */
    val frame: String
        get() = cursor!!.getString(7)

    /**
     * Frame description from the result set or null if none
     */
    val frameDescription: String
        get() = cursor!!.getString(8)

    companion object {

        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.FrameNetLexUnitQueryFromWordId
        private const val QUERYFN = SqLiteDialect.FnFrameNetLexUnitQueryFromWordId

        /**
         * `QUERYWITHPOS` is the SQL statement with Pos input
         */
        private const val QUERYWITHPOS = SqLiteDialect.FrameNetLexUnitQueryFromWordIdAndPos
        private const val QUERYWITHPOSFN = SqLiteDialect.FnFrameNetLexUnitQueryFromWordIdAndPos
    }
}
