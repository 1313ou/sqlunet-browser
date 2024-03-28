/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log

/**
 * Synset
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class Synset : BasicSynset {

    val posName: String
        get() = Mapping.getDomainPosName(domainId)

    val domainName: String
        get() = Mapping.getDomainName(domainId)

    /**
     * Constructor from data
     *
     * @param synsetId   synset id
     * @param definition definition
     * @param domainId   domain id
     * @param sample     sample
     */
    constructor(synsetId: Long, definition: String?, domainId: Int, sample: String?) : super(synsetId, definition!!, domainId, sample!!)

    /**
     * Constructor from query for synsets
     *
     * @param query query for synsets
     */
    constructor(query: SynsetsQueryFromWordId) : super(query.synsetId, query.definition, query.domainId, query.sample)

    /**
     * Constructor from query for synset
     *
     * @param query query for synset
     */
    constructor(query: SynsetQuery) : super(query.synsetId, query.definition, query.domainId, query.sample)

    /**
     * Constructor from query for synsets of a given type
     *
     * @param query query for synsets of a given type
     */
    constructor(query: SynsetsQueryFromWordIdAndCondition) : super(query.synsetId, query.definition, query.domainId, query.sample)

    /**
     * Constructor from query for related synsets
     *
     * @param query query for related synsets
     */
    internal constructor(query: RelatedsQueryFromSynsetId) : super(query.synsetId, query.definition, query.domainId, query.samples)

    /**
     * Constructor from query for synsets related through a given relation type id
     *
     * @param query query for synsets related through a given relation type id
     */
    internal constructor(query: RelatedsQueryFromSynsetIdAndRelationId) : super(query.synsetId, query.definition, query.domainId, query.samples)

    /**
     * Get words in the synset as a list
     *
     * @param connection connection to the database
     * @return list of words in synset
     */
    fun getSynsetWords(connection: SQLiteDatabase): List<Word>? {
        try {
            SynsetWordsQuery(connection, synsetId).use {
                it.execute()
                val words: MutableList<Word> = ArrayList()
                while (it.next()) {
                    val word = it.word
                    val id = it.id
                    words.add(Word(word, id))
                }
                return words
            }
        } catch (e: SQLException) {
            Log.e(TAG, "While querying synset words", e)
            return null
        }
    }

    /**
     * Get words in the synset as a string
     *
     * @param connection connection to the database
     * @return list of words in synset as a comma-separated string
     */
    fun getSynsetWordsAsString(connection: SQLiteDatabase): String {
        val sb = StringBuilder()

        // synset words
        val words = getSynsetWords(connection)

        // stringify
        if (words != null) {
            for (i in words.indices) {
                val word = words[i]
                val word2 = word.word.replace('_', ' ')
                if (i != 0) {
                    sb.append(',')
                }
                sb.append(word2)
            }
        }
        return sb.toString()
    }

    /**
     * Get synsets related to the synset
     *
     * @param connection connection
     * @param wordId     word id (for lexical relations)
     * @return list of synsets related to the synset
     */
    open fun getRelateds(connection: SQLiteDatabase, wordId: Long): List<Related>? {
        try {
            RelatedsQueryFromSynsetId(connection).use {
                it.fromSynset = synsetId
                it.fromWord = wordId
                it.execute()
                val relateds: MutableList<Related> = ArrayList()
                while (it.next()) {
                    val related = Related(it)
                    relateds.add(related)
                }
                return relateds
            }
        } catch (e: SQLException) {
            Log.e(TAG, "While querying relateds", e)
            return null
        }
    }

    /**
     * Get synsets related to the synset through a given relation type id
     *
     * @param connection connection
     * @param wordId     word id (for lexical relations)
     * @param relationId relation type id
     * @return list of synsets related to the synset through a given relation type
     */
    fun getTypedRelateds(connection: SQLiteDatabase, wordId: Long, relationId: Int): List<Related>? {
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
            Log.e(TAG, "While querying typed relations", e)
            return null
        }
    }

    companion object {
        private const val TAG = "Synset"
    }
}
