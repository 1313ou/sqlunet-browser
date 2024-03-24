/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.framenet.Utils.parseLabels
import org.sqlunet.sql.DBQuery

/**
 * FrameNet layer query
 *
 * @param connection connection
 * @param targetId   target id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal open class FnLayerQuery(connection: SQLiteDatabase, targetId: Long, query: String) : DBQuery(connection, query) {

    init {
        setParams(targetId)
    }

    /**
     * Layer id from the result set
     */
    val layerId: Long
        get() = cursor!!.getLong(0)

    /**
     * Layer type from the result set
     */
    val layerType: String
        get() = cursor!!.getString(1)

    /**
     * Layer annoSet id from the result set
     */
    val annoSetId: Long
        get() = cursor!!.getLong(2)

    /**
     * Layer rank from the result set
     */
    val rank: Int
        get() = cursor!!.getInt(3)

    /**
     * Labels from the result set
     */
    val labels: List<FnLabel>?
        get() = parseLabels(cursor!!.getString(4))
}
