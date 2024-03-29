/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * Query for synsets of a given part-of-speech or domain type and containing a given word
 *
 * @param connection     connection
 * @param domainBased is whether the query is domain based
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SynsetsQueryFromWordIdAndCondition(connection: SQLiteDatabase, domainBased: Boolean) : DBQuery(connection, (if (domainBased) QUERYWITHDOMAIN else QUERYWITHPOS)) {

    /**
     * Set word parameter in prepared SQL statement
     *
     * @param wordId target word
     */
    fun setWordId(wordId: Long) {
        statement.setLong(0, wordId)
    }

    /**
     * Set part-of-speech type parameter in prepared SQL statement
     *
     * @param type target part-of-speech type
     */
    fun setPosType(type: Int) {
        val pos = type.toChar().toString()
        statement.setString(1, pos)
    }

    /**
     * Set domain type parameter in prepared SQL statement
     *
     * @param type target domain type
     */
    fun setDomainType(type: Int) {
        statement.setInt(1, type)
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
     * Sample data
     */
    val sample: String
        get() = cursor!!.getString(3)

    companion object {

        /**
         * `QUERYWITHPOS` is the (part-of-speech based) SQL statement
         */
        private const val QUERYWITHPOS: String = SqLiteDialect.SynsetsQueryFromWordIdAndDomainId

        /**
         * `QUERYWITHDOMAIN` is the (domain based) SQL statement
         */
        private const val QUERYWITHDOMAIN: String = SqLiteDialect.SynsetsQueryFromWordIdAndPos
    }
}
