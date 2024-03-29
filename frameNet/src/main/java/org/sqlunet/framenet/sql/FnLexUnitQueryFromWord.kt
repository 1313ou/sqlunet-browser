/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * FrameNet lex unit query from word
 *
 * @param connection connection
 * @param standalone standalone query
 * @param word       target word
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class FnLexUnitQueryFromWord(connection: SQLiteDatabase, standalone: Boolean, word: String) : DBQuery(connection, if (standalone) QUERYFN else QUERY) {

    init {
        setParams(word)
    }

    /**
     * Word id from the result set
     */
    val wordId: Long
        get() = cursor!!.getLong(0)

    /**
     * Lexical unit id from the result set
     */
    val luId: Long
        get() = cursor!!.getLong(1)

    /**
     * Get the lex unit from the result set
     *
     * @return the lex unit from the result set
     */
    val lexUnit: String
        get() = cursor!!.getString(2)

    /**
     * Pos from the result set
     */
    val pos: String
        get() = cursor!!.getString(3)

    /**
     * Lex unit definition from the result set
     */
    val lexUnitDefinition: String
        get() = cursor!!.getString(4)

    /**
     * Lex unit dictionary from the result set
     */
    val lexUnitDictionary: String
        get() = cursor!!.getString(5)

    /**
     * Incorporated fe from the result set
     */
    val incorporatedFe: String
        get() = cursor!!.getString(6)

    /**
     * Frame id from the result set
     */
    val frameId: Long
        get() = cursor!!.getLong(7)

    /**
     * Frame
     */
    val frame: String
        get() = cursor!!.getString(8)

    /**
     * Frame definition
     */
    val frameDefinition: String
        get() = cursor!!.getString(9)

    companion object {

        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.FrameNetLexUnitQueryFromWord
        private const val QUERYFN = SqLiteDialect.FnFrameNetLexUnitQueryFromWord
    }
}
