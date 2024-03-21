/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * Query for synsets containing a given word
 *
 * @author Bernard
 */
internal class SynsetsQueryFromWordId(connection: SQLiteDatabase?, wordId: Long) : DBQuery(connection!!, QUERY) {
    /**
     * Constructor
     *
     * @param connection connection
     * @param wordId     target word id
     */
    init {
        setParams(wordId)
    }

    val synsetId: Long
        /**
         * Get synset id
         *
         * @return synset id
         */
        get() {
            assert(cursor != null)
            return cursor!!.getLong(0)
        }
    val definition: String
        /**
         * Get synset definition
         *
         * @return definition
         */
        get() {
            assert(cursor != null)
            return cursor!!.getString(1)
        }
    val pos: String
        /**
         * Get synset pos id
         *
         * @return pos
         */
        get() {
            assert(cursor != null)
            return cursor!!.getString(2)
        }
    val domainId: Int
        /**
         * Get synset domain id
         *
         * @return synset domain id
         */
        get() {
            assert(cursor != null)
            return cursor!!.getInt(3)
        }
    val sample: String
        /**
         * Get sample data
         *
         * @return samples as a semicolon-separated string
         */
        get() {
            assert(cursor != null)
            return cursor!!.getString(4)
        }

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.SynsetsQueryFromWordId // ;
    }
}