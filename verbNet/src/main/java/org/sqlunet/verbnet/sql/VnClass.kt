/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * VerbNet class
 *
 * @param className class name
 * @param classId   class id
 * @param groupings groupings
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class VnClass private constructor(
    val className: String,
    val classId: Long,
    val groupings: String?,
) {

    companion object {

        /**
         * Make sets of VerbNet class from query built from classId
         *
         * @param connection connection
         * @param classId    is the class id to build the query from
         * @return list of VerbNet classes
         */
        fun make(connection: SQLiteDatabase, classId: Long): VnClass? {
            VnClassQuery(connection, classId).use {
                it.execute()
                if (it.next()) {
                    val className = it.className
                    val groupings = it.groupings
                    return VnClass(className, classId, groupings)
                }
            }
            return null
        }
    }
}
