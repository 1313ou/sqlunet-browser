/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * VerbNet class query
 *
 * @param connection connection
 * @param classId    target class id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class VnClassQuery(connection: SQLiteDatabase, classId: Long) : DBQuery(connection, QUERY) {

    init {
        setParams(classId)
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
     * (|-separated) Groupings from the result set
     */
    val groupings: String
        get() = cursor!!.getString(2)

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.VerbNetClassQuery
    }
}
