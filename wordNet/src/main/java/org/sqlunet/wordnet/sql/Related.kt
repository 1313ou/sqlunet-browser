/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.sqlunet.wordnet.sql.Mapping.canRecurse
import org.sqlunet.wordnet.sql.Mapping.getRelationName

/**
 * Related, a related synset
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class Related : Synset {

    /**
     * `relationId` relation type id
     */
    private val relationId: Int

    /**
     * `word` related word (lexrelations)
     */
    @JvmField
    val word: String?

    /**
     * `wordId` related word id (lexrelations)
     */
    @JvmField
    val wordId: Long

    /**
     * `fromSynsetId` source synset id
     */
    private val fromSynsetId: Long

    /**
     * `fromWordId` source synset id
     */
    private val fromWordId: Long

    /**
     * Constructor from query for synsets related to a given synset
     *
     * @param query query for synsets related to a given synset
     */
    constructor(query: RelatedsQueryFromSynsetId) : super(query) {
        val words = query.words
        val wordIds = query.wordIds
        relationId = query.relationId
        word = if (words == null) null else if (words.size == 1) words[0] else null
        wordId = if (wordIds == null) 0 else if (wordIds.size == 1) wordIds[0] else 0
        fromSynsetId = query.fromSynset
        fromWordId = query.fromWord
    }

    /**
     * Constructor from query for synsets related to a given synset through a given relation type id
     *
     * @param query is a query for synsets related to a given synset through a given relation type id
     */
    constructor(query: RelatedsQueryFromSynsetIdAndRelationId) : super(query) {
        val words = query.words
        val wordIds = query.wordIds
        relationId = query.relationId
        word = if (words == null) null else if (words.size == 1) words[0] else null
        wordId = if (wordIds == null) 0 else if (wordIds.size == 1) wordIds[0] else 0
        fromSynsetId = query.fromSynset
        fromWordId = query.fromWord
    }

    /**
     * Relation name
     */
    val relationName: String
        get() = getRelationName(relationId)

    /**
     * Whether relation can recurse
     */
    fun canRecurse(): Boolean {
        return canRecurse(relationId)
    }

    /**
     * Override : recurse only on relations of the same relation type
     */
    override fun getRelateds(connection: SQLiteDatabase, wordId: Long): List<Related>? {
        try {
            RelatedsQueryFromSynsetIdAndRelationId(connection).use {
                it.fromSynset = synsetId
                it.fromWord = wordId
                it.fromRelationId = relationId
                it.execute()
                val relateds: MutableList<Related> = ArrayList()
                while (it.next()) {
                    val related = Related(it)
                    relateds.add(related)
                }
                return relateds
            }
        } catch (e: SQLException) {
            Log.e(TAG, "While querying", e)
            return null
        }
    }

    companion object {

        private const val TAG = "Related"
    }
}