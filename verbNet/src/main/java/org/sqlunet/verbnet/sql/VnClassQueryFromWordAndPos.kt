/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * Query for VerbNet selector
 *
 * @param connection connection
 * @param word       target word
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class VnClassQueryFromWordAndPos(connection: SQLiteDatabase, word: String) : DBQuery(connection, QUERY) {

    init {
        setParams(word)
    }

    /**
     * Word id from the result set
     */
    val wordId: Long
        get() = cursor!!.getLong(0)

    /**
     * Synset-specific tag from the result set
     */
    val synsetSpecific: Boolean
        get() = cursor!!.getInt(1) != 0

    /**
     * Synset id from the result set
     */
    val synsetId: Long
        get() = cursor!!.getLong(2)

    /**
     * Synset definition from the result set
     */
    val definition: String
        get() = cursor!!.getString(3)

    /**
     * Synset domain id from the result set
     */
    val domainId: Int
        get() = cursor!!.getInt(4)

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.VerbNetClassQueryFromWordAndPos
    }
}
