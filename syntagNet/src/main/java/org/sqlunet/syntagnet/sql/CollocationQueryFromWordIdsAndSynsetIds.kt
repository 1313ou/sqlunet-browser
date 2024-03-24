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
 * @param word2id     target word 2 id
 * @param synset2id   target synset 2 id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class CollocationQueryFromWordIdsAndSynsetIds(connection: SQLiteDatabase?, wordid: Long, synsetid: Long, word2id: Long, synset2id: Long) : BaseCollocationQuery(connection, QUERY) {

    init {
        setParams(wordid, synsetid, word2id, synset2id)
    }

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.SyntagNetCollocationQueryFromWordIdsAndSynsetIds
    }
}
