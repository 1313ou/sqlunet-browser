/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * PropBank roles query
 *
 * @param connection connection
 * @param roleSetId roleset id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class PbRoleQueryFromRoleSetId(connection: SQLiteDatabase, roleSetId: Long) : DBQuery(connection!!, QUERY) {

    init {
        setParams(roleSetId)
    }

    /**
     * Set id parameters in prepared SQL statement
     *
     * @param roleSetId target role set id
     */
    fun setId(roleSetId: Long) {
        statement.setLong(0, roleSetId)
    }

    /**
     *Role id from the result set
     */
    val roleId: Long
        get() = cursor!!.getLong(0)

    /**
     * Role description from the result set
     */
    val roleDescr: String
        get() = cursor!!.getString(1).lowercase()

    /**
     * Role N
     */
    val argType: String
        get() = cursor!!.getString(2)

    /**
     * Role F
     */
    val roleFunc: String
        get() = cursor!!.getString(3)

    /**
     * Role VerbNet theta
     */
    val roleTheta: String
        get() = cursor!!.getString(4)

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.PropBankRolesQueryFromRoleSetId
    }
}
