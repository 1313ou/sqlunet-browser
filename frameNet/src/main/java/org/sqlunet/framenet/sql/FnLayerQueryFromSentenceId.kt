/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * FrameNet layer query from sentence
 *
 * @param connection       connection
 * @param targetSentenceId target sentence id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class FnLayerQueryFromSentenceId(connection: SQLiteDatabase, targetSentenceId: Long) : FnLayerQuery(connection, targetSentenceId, QUERY) {

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.FrameNetLayerQueryFromSentenceId
    }
}
