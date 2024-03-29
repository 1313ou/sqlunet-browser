/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * FrameNet frame element query
 *
 * @param connection connection
 * @param frameId    target frame id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class FnFrameElementQueryFromFrameId(connection: SQLiteDatabase, frameId: Long) : DBQuery(connection, QUERY) {

    init {
        setParams(frameId)
    }

    /**
     * Fe type id from the result set
     */
    val fETypeId: Long
        get() = cursor!!.getLong(0)

    /**
     * Fe type from the result set
     */
    val fEType: String
        get() = cursor!!.getString(1)

    /**
     * Fe id from the result set
     */
    val fEId: Long
        get() = cursor!!.getLong(2)

    /**
     * Fe definition from the result set
     */
    val fEDefinition: String
        get() = cursor!!.getString(3)

    /**
     * Fe abbrev from the result set
     */
    val fEAbbrev: String
        get() = cursor!!.getString(4)

    /**
     * Fe core type from the result set
     */
    val fECoreType: String
        get() = cursor!!.getString(5)

    /**
     * Fe sem types from the result set
     */
    val semTypes: String
        get() = cursor!!.getString(6)

    /**
     * Fe core membership from the result set
     */
    val isCore: Boolean
        get() = cursor!!.getInt(7) != 0

    /**
     * Get the fe core set from the result set
     *
     * @return the fe coreset from the result set or 0 if node
     */
    val coreSet: Int
        get() = cursor!!.getInt(8)

    companion object {

        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.FrameNetFEQueryFromFrameId
    }
}
