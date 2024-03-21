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
internal class Related : Synset {

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
        // construct synset

        // relation data
        val words = query.getWords()
        val wordIds = query.getWordIds()
        relationId = query.getRelationId()
        word = if (words == null) null else if (words.size == 1) words[0] else null
        wordId = if (wordIds == null) 0 else if (wordIds.size == 1) wordIds[0] else 0
        fromSynsetId = query.getFromSynset()
        fromWordId = query.getFromWord()
    }

    /**
     * Constructor from query for synsets related to a given synset through a given relation type id
     *
     * @param query is a query for synsets related to a given synset through a given relation type id
     */
    constructor(query: RelatedsQueryFromSynsetIdAndRelationId) : super(query) {
        // construct synset

        // relation data
        val words = query.getWords()
        val wordIds = query.getWordIds()
        relationId = query.getRelationId()
        word = if (words == null) null else if (words.size == 1) words[0] else null
        wordId = if (wordIds == null) 0 else if (wordIds.size == 1) wordIds[0] else 0
        fromSynsetId = query.getFromSynset()
        fromWordId = query.getFromWord()
    }

    val relationName: String
        /**
         * Get relation name
         *
         * @return relation name
         */
        get() = getRelationName(relationId)

    /**
     * Get whether relation can recurse
     *
     * @return true if the relation can recurse
     */
    fun canRecurse(): Boolean {
        return canRecurse(relationId)
    }

    /**
     * Override : recurse only on relations of the same relation type
     */
    override fun getRelateds(connection: SQLiteDatabase, wordId: Long): List<Related>? {
        try {
            RelatedsQueryFromSynsetIdAndRelationId(connection).use { query ->
                query.setFromSynset(synsetId)
                query.setFromWord(wordId)
                query.setRelation(relationId)
                query.execute()
                val relateds: MutableList<Related> = ArrayList()
                while (query.next()) {
                    val related = Related(query)
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