/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * Word query
 *
 * @param connection connection
 * @param wordId     is the word id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class WordQuery(connection: SQLiteDatabase, wordId: Long) : DBQuery(connection, QUERY) {

    init {
        setParams(wordId)
    }

    /**
     * Word id from the result set
     */
    val id: Int
        get() = cursor!!.getInt(0)

    /**
     * Word string value from the result set
     */
    val word: String
        get() = cursor!!.getString(1)

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.WordQuery
    }
}
