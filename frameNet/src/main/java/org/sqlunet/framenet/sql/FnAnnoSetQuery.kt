/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery
import org.sqlunet.sql.Utils.toIds

/**
 * FrameNet annoSet query
 *
 * @param connection connection
 * @param annoSetId  target annoSet id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class FnAnnoSetQuery(connection: SQLiteDatabase, annoSetId: Long) : DBQuery(connection, QUERY) {

    init {
        setParams(annoSetId)
    }

    /**
     * Sentence id from the result set
     */
    val sentenceId: Long
        get() = cursor!!.getLong(0)

    /**
     * Sentence text from the result set
     */
    val sentenceText: String
        get() = cursor!!.getString(1)

    /**
     * AnnoSet ids from the result set
     */
    @Suppress("unused")
    val annoSetIds: LongArray
        get() = toIds(cursor!!.getString(2))

    companion object {

        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.FrameNetAnnoSetQuery
    }
}
