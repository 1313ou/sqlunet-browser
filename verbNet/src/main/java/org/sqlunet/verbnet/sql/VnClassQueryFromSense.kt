/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * VerbNet class-with-sense query
 *
 * @param connection connection
 * @param wordId     target word id
 * @param synsetId   target synset id (null corresponds to no value)
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class VnClassQueryFromSense(connection: SQLiteDatabase, wordId: Long, synsetId: Long?) : DBQuery(connection, QUERY) {

    init {
        setParams(wordId, synsetId!!)
    }

    /**
     * Class id from the result set
     */
    val classId: Long
        get() = cursor!!.getLong(0)

    /**
     * Class name from the result set
     */
    val className: String
        get() = cursor!!.getString(1)

    /**
     * Frame xtag from the result set
     */
    val synsetSpecific: Boolean
        get() = cursor!!.getInt(2) == 0

    /**
     * Definition from the result set
     */
    val definition: String
        get() = cursor!!.getString(3)

    /**
     * Sensenum from the result set
     */
    val senseNum: Int
        get() = cursor!!.getInt(4)

    /**
     * Sensekey from the result set
     */
    val senseKey: String
        get() = cursor!!.getString(5)

    /**
     * quality from the result set
     */
    val quality: Float
        get() = cursor!!.getFloat(6)

    /**
     * (|-separated) Groupings from the result set
     */
    val groupings: String
        get() = cursor!!.getString(7)


    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.VerbNetClassQueryFromSense
    }
}
