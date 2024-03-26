/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * VerbNet frame query
 *
 * @param connection connection
 * @param classId    target classId
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class VnFrameQueryFromClassId(connection: SQLiteDatabase, classId: Long) : DBQuery(connection, QUERY) {

    init {
        setParams(classId)
    }

    /**
     * Frame id from the result set
     */
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
    val examples: String
        get() = cursor!!.getString(7)

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.VerbNetFramesQueryFromClassId
    }
}
