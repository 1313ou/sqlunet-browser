/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * FrameNet sentence query from lex unit
 *
 * @param connection connection
 * @param luId       target lex unit id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class FnSentenceQueryFromLexUnitId(connection: SQLiteDatabase, luId: Long) : DBQuery(connection, QUERY) {
    /**
     * Constructor
     */
    init {
        setParams(luId)
    }

    /**
     * Sentence id from the result set
     */
    val sentenceId: Long
        get() = cursor!!.getLong(0)

    /**
     * Text from the result set
     */
    val text: String
        get() = cursor!!.getString(1)

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.FrameNetSentencesQueryFromLexUnitId
    }
}