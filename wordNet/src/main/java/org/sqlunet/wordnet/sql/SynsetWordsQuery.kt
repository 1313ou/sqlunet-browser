/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * Query for words in synset
 *
 * @param connection connection
 * @param synsetId   target synset id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class SynsetWordsQuery(connection: SQLiteDatabase, synsetId: Long) : DBQuery(connection, QUERY) {

    init {
        setParams(synsetId)
    }

    /**
     * Get Word in the result set
     */
    val word: String
        get() = cursor!!.getString(0)

    /**
     * Database id in the result set
     */
    val id: Long
        get() = cursor!!.getLong(1)

    companion object {

        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.SynsetWordsQuery
    }
}