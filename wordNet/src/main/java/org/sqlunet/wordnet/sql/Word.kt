/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log

/**
 * Word
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class Word : BasicWord {

    /**
     * Word
     *
     * @param word word string
     * @param id   database id
     */
    constructor(word: String?, id: Long) : super(word!!, id)

    /**
     * Constructor
     *
     * @param query database query
     */
    private constructor(query: WordQuery) : super(query.word, query.id.toLong())

    /**
     * Constructor
     *
     * @param query database query
     */
    private constructor(query: WordQueryFromWord) : super(query.word, query.id.toLong())

    /**
     * Get synsets containing a given word
     *
     * @param connection connection
     * @return list of synsets containing a given word
     */
    fun getSynsets(connection: SQLiteDatabase): List<Synset>? {
        try {
            SynsetsQueryFromWordId(connection, id).use {
                it.execute()
                val synsets: MutableList<Synset> = ArrayList()
                while (it.next()) {
                    val synset = Synset(it)
                    synsets.add(synset)
                }
                return synsets
            }
        } catch (e: SQLException) {
            Log.e(TAG, "While querying synsets", e)
            return null
        }
    }

    /**
     * Get synsets containing a given word and of a given part-of-speech or domain id
     *
     * @param connection  connection
     * @param targetType  target type to restrict search to
     * @param domainBased is whether the query is domain based
     * @return list of synsets for a given word
     */
    fun getTypedSynsets(connection: SQLiteDatabase, targetType: Int, domainBased: Boolean): List<Synset>? {
        try {
            SynsetsQueryFromWordIdAndCondition(connection, domainBased).use {
                it.setWordId(id)
                if (domainBased) {
                    it.setDomainType(targetType)
                } else {
                    it.setPosType(targetType)
                }
                it.execute()
                val synsets: MutableList<Synset> = ArrayList()
                while (it.next()) {
                    val synset = Synset(it)
                    synsets.add(synset)
                }
                return synsets
            }
        } catch (e: SQLException) {
            Log.e(TAG, "While querying typed synsets", e)
            return null
        }
    }

    companion object {

        private const val TAG = "Word"

        /**
         * Make word
         *
         * @param connection connection
         * @param str        target string
         * @return Word or null
         */
        fun make(connection: SQLiteDatabase, str: String): Word? {
            try {
                WordQueryFromWord(connection, str).use {
                    it.execute()
                    return if (it.next()) {
                        Word(it)
                    } else null
                }
            } catch (e: SQLException) {
                Log.e(TAG, "While querying word", e)
                return null
            }
        }

        /**
         * Make word
         *
         * @param connection connection
         * @param wordId     target id
         * @return Word or null
         */
        fun make(connection: SQLiteDatabase, wordId: Long): Word? {
            try {
                WordQuery(connection, wordId).use {
                    it.execute()
                    return if (it.next()) {
                        Word(it)
                    } else null
                }
            } catch (e: SQLException) {
                Log.e(TAG, "While querying word", e)
                return null
            }
        }
    }
}