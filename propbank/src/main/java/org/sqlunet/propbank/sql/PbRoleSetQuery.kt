/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * PropBank role sets query
 *
 * @param connection connection
 * @param roleSetId  target role set id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class PbRoleSetQuery(connection: SQLiteDatabase, roleSetId: Long) : DBQuery(connection, QUERY) {

    init {
        setParams(roleSetId)
    }

    /**
     * Role set id from the result set
     */
    val roleSetId: Long
        get() = cursor!!.getLong(0)

    /**
     * Role set name from the result set
     */
    val roleSetName: String
        get() = cursor!!.getString(1)

    /**
     *Role set head
     */
    val roleSetHead: String
        get() = cursor!!.getString(2)

    /**
     * Role set description
     *
     * @return role set description
     */
    val roleSetDescr: String
        get() = cursor!!.getString(3)

    companion object {

        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.PropBankRoleSetQuery
    }
}
