/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * SyntagNet Collocation query from word id
 *
 * @param connection connection
 * @param wordid     target word id
 * @param word2id    target word 2 id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class CollocationQueryFromWordIds(connection: SQLiteDatabase, wordid: Long, word2id: Long) : BaseCollocationQuery(connection, QUERY) {

    init {
        setParams(wordid, word2id)
    }

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.SyntagNetCollocationQueryFromWordIds
    }
}
