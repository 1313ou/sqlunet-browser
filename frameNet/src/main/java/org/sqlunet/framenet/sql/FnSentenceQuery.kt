/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * FrameNet sentence query
 *
 * @param connection connection
 * @param sentenceId target sentence id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class FnSentenceQuery(connection: SQLiteDatabase, sentenceId: Long) : DBQuery(connection, QUERY) {

    init {
        setParams(sentenceId)
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
        private const val QUERY = SqLiteDialect.FrameNetSentenceQuery
    }
}