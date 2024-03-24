/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * SyntagNet collocations query from word
 *
 * @param connection connection
 * @param word       target word
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class CollocationQueryFromWord(connection: SQLiteDatabase, word: String) : BaseCollocationQuery(connection, QUERY) {

    init {
        setParams(word, word)
    }

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.SyntagNetCollocationQueryFromWord
    }
}
