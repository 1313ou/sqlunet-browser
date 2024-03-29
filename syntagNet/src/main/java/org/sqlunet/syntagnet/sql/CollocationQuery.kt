/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * SyntagNet collocation query
 *
 * @param connection connection
 * @param collocationId  target collocation id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class CollocationQuery(connection: SQLiteDatabase, collocationId: Long) : BaseCollocationQuery(connection, QUERY) {

    init {
        setParams(collocationId)
    }

    companion object {

        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.SyntagNetCollocationQuery
    }
}
