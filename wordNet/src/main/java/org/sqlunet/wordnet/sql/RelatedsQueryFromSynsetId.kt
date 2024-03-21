/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * Query for related synsets
 *
 * @param connection connection
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class RelatedsQueryFromSynsetId(connection: SQLiteDatabase?) : DBQuery(connection!!, QUERY) {

    /**
     * Relation type id
     */
    val relationId: Int
        get() = cursor!!.getInt(0)

    /**
     * Synset id
     */
    val synsetId: Long
        get() = cursor!!.getLong(1)

    /**
     * Synset definition
     */
    val definition: String
        get() = cursor!!.getString(2)

    /**
     * Synset domain id
     */
    val domainId: Int
        get() = cursor!!.getInt(3)

    /**
     * Samples in a bar-separated string
     */
    val samples: String
        get() = cursor!!.getString(4)

    /**
     * Target word ids
     */
    val wordIds: LongArray?
        get() {
            val resultString = cursor!!.getString(5) ?: return null
            val resultStrings = resultString.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val result = LongArray(resultStrings.size)
            for (i in result.indices) {
                result[i] = resultStrings[i].toLong()
            }
            return result
        }

    /**
     * Target words
     */
    val words: Array<String>?
        get() {
            assert(cursor != null)
            val results = cursor!!.getString(6) ?: return null
            return results.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }

    /**
     * Source synset id
     */
    var fromSynset: Long
        get() = cursor!!.getLong(7)
        set(synsetId) {
            statement.setLong(0, synsetId)
            statement.setLong(1, synsetId)
        }

    /**
     * Source word
     */
    var fromWord: Long
        get() = cursor!!.getLong(8)
        set(wordId) {
            statement.setLong(2, wordId)
            statement.setLong(3, wordId)
        }

    companion object {
        /**
         * `QUERY` SQL statement
         */
        private const val QUERY = SqLiteDialect.RelatedsQueryFromSynsetId
    }
}