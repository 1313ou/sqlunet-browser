/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.syntagnet.sql.SqLiteDialect.SyntagNetBaseCollocationOrder
import org.sqlunet.syntagnet.sql.SqLiteDialect.SyntagNetBaseCollocationQuery

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
internal class CollocationNullableQueryFromWordIdsAndSynsetIds(connection: SQLiteDatabase, wordid: Long?, synsetid: Long?, word2id: Long?, synset2id: Long?) : BaseCollocationQuery(connection, getQuery(wordid, synsetid, word2id, synset2id)) {

    init {
        val ids = listOf(wordid, synsetid, word2id, synset2id)
        val params = ids
            .asSequence()
            .withIndex()
            .filter { (i, _) -> ids[i] != null && ids[i]!! > 0L }
            .map { (_, v) -> v!! }
            .toList()
            .toTypedArray()
        setParams(*params)
    }

    companion object {

        val wheres = mapOf(
            0 to SqLiteDialect.SyntagNetCollocationWhereWordIdClause,
            1 to SqLiteDialect.SyntagNetCollocationWhereSynsetIdClause,
            2 to SqLiteDialect.SyntagNetCollocationWhereWord2IdClause,
            3 to SqLiteDialect.SyntagNetCollocationWhereSynset2IdClause,
        )

        private fun getQuery(wordid: Long?, synsetid: Long?, word2id: Long?, synset2id: Long?): String {
            val ids = listOf(wordid, synsetid, word2id, synset2id)
            val where =  "$SyntagNetBaseCollocationQuery WHERE " + ids
                .asSequence()
                .withIndex()
                .filter { (i, _) -> ids[i] != null && ids[i]!! > 0L }
                .joinToString(separator = " AND ") { (i, _) -> wheres[i].toString() }
            return "$where $SyntagNetBaseCollocationOrder"
        }
    }
}
