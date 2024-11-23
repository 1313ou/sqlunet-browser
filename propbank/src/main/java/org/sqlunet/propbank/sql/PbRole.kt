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
 * @param roleVn    role vn
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class PbRole private constructor(
    val roleId: Long,
    val roleDescr: String,
    val argType: String,
    val roleFunc: String,
    val roleVn: String?,
) {

    companion object {

        /**
         * Make sets of PropBank roles from query built from roleSetId
         *
         * @param connection connection
         * @param roleSetId  role set id to build query from
         * @return list of PropBank roles
         */
        fun make(connection: SQLiteDatabase, roleSetId: Long): List<PbRole?>? {
            var result: MutableList<PbRole?>? = null
            PbRoleQueryFromRoleSetId(connection, roleSetId).use {
                it.execute()
                while (it.next()) {
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(PbRole(it.roleId, it.roleDescr, it.argType, it.roleFunc, it.roleVn))
                }
            }
            return result
        }
    }
}