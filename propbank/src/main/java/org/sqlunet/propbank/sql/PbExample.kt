/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.sql

import android.database.sqlite.SQLiteDatabase

/**
 * Examples attached to a PropBank role set
 *
 * @param exampleId is the example id
 * @param text      is the text of the example
 * @param rel       is the relation
 * @param args      is the list of arguments
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class PbExample private constructor(
    val exampleId: Long,
    val text: String,
    val rel: String,
    val args: List<PbArg>?,
) {

    companion object {

        /**
         * Make a list of examples from query built from roleSet id
         *
         * @param connection connection
         * @return list of PropBank examples
         */
        fun make(connection: SQLiteDatabase, roleSetId: Long): List<PbExample>? {
            var result: MutableList<PbExample>? = null
            PbExampleQueryFromRoleSetId(connection, roleSetId).use {
                it.execute()
                while (it.next()) {
                    // data from result set
                    val exampleId = it.exampleId
                    val text = it.text
                    val rel = it.rel
                    val args = it.args
                    if (result == null) {
                        result = ArrayList()
                    }
                    result.add(PbExample(exampleId, text, rel, args))
                }
            }
            return result
        }
    }
}
