/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * FrameNet frame query
 *
 * @param connection connection
 * @param frameId    target frame id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class FnFrameQuery(connection: SQLiteDatabase, frameId: Long) : DBQuery(connection, QUERY) {

    init {
        setParams(frameId)
    }

    /**
     * Frame Id from the result set
     */
    val frameId: Long
        get() = cursor!!.getInt(0).toLong()

    /**
     * Frame from the result set
     */
    val frame: String
        get() = cursor!!.getString(1)

    /**
     * Frame description from the result set
     *
     * @return the frame description from the result set
     */
    val frameDescription: String
        get() = cursor!!.getString(2)

    /**
     * Semtypes from the result set
     */
    val semTypes: String?
        get() = cursor!!.getString(3)

    /**
     * Get the related frames from the result set or null if none
     */
    val relatedFrames: String?
        get() = cursor!!.getString(4)

    companion object {

        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.FrameNetFrameQuery
    }
}
