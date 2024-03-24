/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * FrameNet layer query from annoSet
 *
 * @param connection      connection
 * @param targetAnnoSetId target annoSet id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class FnLayerQueryFromAnnoSetId(connection: SQLiteDatabase, targetAnnoSetId: Long) : FnLayerQuery(connection, targetAnnoSetId, QUERY) {

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.FrameNetLayerQueryFromAnnoSetId
    }
}
