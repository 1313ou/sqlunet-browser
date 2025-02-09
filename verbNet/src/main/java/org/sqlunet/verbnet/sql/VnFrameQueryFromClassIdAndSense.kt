/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery
import java.sql.Types

/**
 * VerbNet frame query from sense
 *
 * @param connection connection
 * @param classId    target class id
 * @param wordId     target word id
 * @param synsetId   target synset id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class VnFrameQueryFromClassIdAndSense(connection: SQLiteDatabase, classId: Long, wordId: Long, synsetId: Long?) : DBQuery(connection, QUERY) {

    init {
        setParams(classId, wordId)
        if (synsetId != null) {
            statement.setLong(2, synsetId)
        } else {
            statement.setNull(2, Types.DECIMAL)
        }
    }

    /**
     * Frame id from the result set
     */
    @Suppress("unused")
    val frameId: Long
        get() = cursor!!.getLong(0)

    /**
     * Frame number from the result set
     */
    val number: String
        get() = cursor!!.getString(1)

    /**
     * Frame xtag from the result set
     */
    val xTag: String
        get() = cursor!!.getString(2)

    /**
     * Frame major description from the result set
     */
    val description1: String
        get() = cursor!!.getString(3)

    /**
     * Frame minor description from the result set
     */
    val description2: String
        get() = cursor!!.getString(4)

    /**
     * Frame syntax from the result set
     */
    val syntax: String
        get() = cursor!!.getString(5)

    /**
     * Frame semantics from the result set
     */
    val semantics: String
        get() = cursor!!.getString(6)

    /**
     * Frame example from the result set
     */
    val example: String
        get() = cursor!!.getString(7)

    /**
     * Frame quality from the result set
     */
    val quality: Int
        get() = cursor!!.getInt(8)

    /**
     * Synset-specific flag from the result set
     */
    @Suppress("unused")
    val synsetSpecific: Boolean
        get() =
            cursor!!.getInt(9) == 0

    companion object {

        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.VerbNetFramesQueryFromClassIdAndSense
    }
}
