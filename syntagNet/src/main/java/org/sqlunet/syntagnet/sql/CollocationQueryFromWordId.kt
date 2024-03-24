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
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class CollocationQueryFromWordId(connection: SQLiteDatabase, wordid: Long) : BaseCollocationQuery(connection, QUERY) {

    init {
        setParams(wordid, wordid)
    }

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.SyntagNetCollocationQueryFromWordId
    }
}
