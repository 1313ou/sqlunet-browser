/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery
import java.sql.Types

/**
 * Query for VerbNet roles from sense
 *
 * @param connection connection
 * @param classId    target class id
 * @param wordId     target word id
 * @param synsetId   target synset id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class VnRoleQueryFromClassIdAndSense(connection: SQLiteDatabase, classId: Long, wordId: Long, synsetId: Long?) : DBQuery(connection, QUERY) {

    init {
        setParams(classId, wordId)
        if (synsetId != null) {
            statement.setLong(2, synsetId)
        } else {
            statement.setNull(2, Types.DECIMAL)
        }
    }

    /**
     * Role id from the result set
     */
    val roleId: Long
        get() = cursor!!.getLong(0)

    /**
     * Role type id from the result set

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

    /**
     * Quality from the result set
     */
    val quality: Int
        get() = cursor!!.getInt(4)

    /**
     * Synset-specific flag from the result set
     */
    val synsetSpecific: Boolean
        get() = cursor!!.getInt(5) == 0

    companion object {
        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.VerbNetThematicRolesQueryFromClassIdAndSense
    }
}
