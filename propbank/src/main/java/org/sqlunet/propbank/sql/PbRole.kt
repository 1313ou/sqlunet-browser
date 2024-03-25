/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.sql

import android.database.sqlite.SQLiteDatabase

/**
 * PropBank role
 *
 * @param roleId    role id
 * @param roleDescr role description
 * @param argType   role arg type
 * @param roleFunc  role f
 * @param roleTheta role theta
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class PbRole private constructor(
    @JvmField val roleId: Long,
    @JvmField val roleDescr: String,
    @JvmField val argType: String,
    @JvmField val roleFunc: String,
    @JvmField val roleTheta: String,
) {
    companion object {

        /**
         * Make sets of PropBank roles from query built from roleSetId
         *
         * @param connection connection
         * @param roleSetId  role set id to build query from
         * @return list of PropBank roles
         */
        @JvmStatic
        fun make(connection: SQLiteDatabase, roleSetId: Long): List<PbRole?>? {
            var result: MutableList<PbRole?>? = null
            PbRoleQueryFromRoleSetId(connection, roleSetId).use { query ->
                query.execute()
                while (query.next()) {
                    val roleId = query.roleId
                    val roleDescr = query.roleDescr
                    val roleArgType = query.argType
                    val roleFunc = query.roleFunc
                    val roleTheta = query.roleTheta
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(PbRole(roleId, roleDescr, roleArgType, roleFunc, roleTheta))
                }
            }
            return result
        }
    }
}