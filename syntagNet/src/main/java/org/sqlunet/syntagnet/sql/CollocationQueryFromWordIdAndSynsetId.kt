/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * SyntagNet Collocation query from word id and synset id
 *
 * @param connection connection
 * @param wordid     target word id
 * @param synsetid   target synset id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class CollocationQueryFromWordIdAndSynsetId(connection: SQLiteDatabase?, wordid: Long, synsetid: Long) : BaseCollocationQuery(connection, QUERY) {

    init {
        setParams(wordid, synsetid, wordid, synsetid)
    }

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.SyntagNetCollocationQueryFromWordIdAndSynsetId
    }
}
