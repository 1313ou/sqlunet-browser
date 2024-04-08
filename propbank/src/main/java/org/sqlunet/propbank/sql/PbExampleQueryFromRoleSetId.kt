/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * PropBank example query
 *
 * @param connection connection
 * @param roleSetId  role set id to build query from
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class PbExampleQueryFromRoleSetId(connection: SQLiteDatabase, roleSetId: Long) : DBQuery(connection, QUERY) {

    init {
        setParams(roleSetId)
    }

    /**
     * Example id from cursor
     */
    val exampleId: Long
        get() = cursor!!.getLong(0)

    /**
     * Text from cursor
     */
    val text: String
        get() = cursor!!.getString(1)

    /**
     * Rel from cursor
     */
    val rel: String
        get() = cursor!!.getString(2)

    /**
     * Args from cursor
     */
    val args: List<PbArg>?
        get() {
            val concatArg = cursor!!.getString(3) ?: return null
            val args: MutableList<PbArg> = ArrayList()
            for (arg in concatArg.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                val argFields = arg.split("~".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                args.add(PbArg(*argFields))
            }
            return args
        }

    /**
     * Aspect from cursor
     */
    val aspect: String?
        get() = cursor!!.getString(4)

    /**
     * Form from cursor
     */
    val form: String?
        get() = cursor!!.getString(5)

    /**
     * Tense from cursor
     */
    val tense: String?
        get() = cursor!!.getString(6)

    /**
     * Get voice from cursor
     */
    val voice: String?
        get() = cursor!!.getString(7)

    /**
     * Person from cursor
     */
    val person: String?
        get() = cursor!!.getString(8)

    companion object {

        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.PropBankExamplesQueryFromRoleSetId
    }
}
