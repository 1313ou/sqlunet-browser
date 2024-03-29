/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * Query for domain enumeration
 *
 * @param connection connection
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class DomainsQuery(connection: SQLiteDatabase) : DBQuery(connection, QUERY) {

    /**
     * Domain id value from the result set
     */
    val id: Int
        get() = cursor!!.getInt(0)

    /**
     * Domain name (with pos prefix) from the result set
     */
    val posDomainName: String
        get() = cursor!!.getString(1)

    /**
     * Part-of-speech value from the result set (in the range n,v,a,r)
     */
    val pos: Int
        get() = cursor!!.getString(2)?.get(0)?.code ?: 0

    companion object {

        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.DomainsQuery
    }
}