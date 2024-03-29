/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * Query for a relation enumeration
 *
 * @param connection connection
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class RelationsQuery(connection: SQLiteDatabase) : DBQuery(connection, QUERY) {

    /**
     * Relation id value from the result set
     */
    val id: Int
        get() = cursor!!.getInt(0)

    /**
     * Relation name value from the result set
     */
    val name: String
        get() = cursor!!.getString(1)

    /**
     * Relation recurse capability value from the result set
     */
    val recurse: Boolean
        get() = cursor!!.getInt(2) != 0

    companion object {

        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.RelationsQuery
    }
}