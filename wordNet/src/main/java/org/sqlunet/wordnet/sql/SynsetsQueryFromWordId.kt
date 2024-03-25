/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * Query for synsets containing a given word
 *
 * @param connection connection
 * @param wordId     target word id
 *
 * @author Bernard
 */
class SynsetsQueryFromWordId(connection: SQLiteDatabase, wordId: Long) : DBQuery(connection!!, QUERY) {

    init {
        setParams(wordId)
    }

    /**
     * Synset id
     */
    val synsetId: Long
        get() = cursor!!.getLong(0)

    /**
     * Synset definition
     */
    val definition: String
        get() = cursor!!.getString(1)

    /**
     * Synset pos id
     */
    val pos: String
        get() = cursor!!.getString(2)

    /**
     * Synset domain id
     */
    val domainId: Int
        get() = cursor!!.getInt(3)

    /**
     * Samples as a semicolon-separated string
     */
    val sample: String
        get() = cursor!!.getString(4)

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.SynsetsQueryFromWordId // 
    }
}