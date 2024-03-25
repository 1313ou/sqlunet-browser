/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * Query for VerbNet roles
 *
 * @param connection connection
 * @param classId    target classId
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class VnRoleQueryFromClassId(connection: SQLiteDatabase, classId: Long) : DBQuery(connection!!, QUERY) {

    init {
        setParams(classId)
    }

    /**
     * Role id from the result set
     */
    val roleId: Long
        get() = cursor!!.getLong(0)

    /**
     * Role type id from the result set
     *
     * @return the role type id from the result set
     */
    val roleTypeId: Long
        get() = cursor!!.getLong(1)

    /**
     * Role type from the result set
     */
    val roleType: String
        get() = cursor!!.getString(2)

    /**
     * Role selectional restriction from the result set
     */
    val selectionRestriction: String
        get() = cursor!!.getString(3)

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.VerbNetThematicRolesQueryFromClassId
    }
}
