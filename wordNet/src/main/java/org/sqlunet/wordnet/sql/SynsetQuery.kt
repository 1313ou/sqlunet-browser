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
 * @param synsetId   target synset id
 *
 * @author Bernard
 */
class SynsetQuery(connection: SQLiteDatabase, synsetId: Long) : DBQuery(connection, QUERY) {

    init {
        setParams(synsetId)
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
     * Synset domain id
     */
    val domainId: Int
        get() = cursor!!.getInt(2)

    /**
     * Samples as a semicolon-separated string
     */
    val sample: String
        get() = cursor!!.getString(3)

    companion object {

        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.SynsetQuery
    }
}