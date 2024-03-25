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
    @JvmField val exampleId: Long,
    @JvmField val text: String,
    @JvmField val rel: String,
    @JvmField val args: List<PbArg>?,
    @JvmField val aspect: String,
    @JvmField val form: String,
    @JvmField val tense: String,
    @JvmField val voice: String,
    @JvmField val person: String,
) {
    companion object {

        /**
         * Make a list of examples from query built from roleSet id
         *
         * @param connection connection
         * @return list of PropBank examples
         */
        @JvmStatic
        fun make(connection: SQLiteDatabase, roleSetId: Long): List<PbExample?>? {
            var result: MutableList<PbExample?>? = null
            PbExampleQueryFromRoleSetId(connection, roleSetId).use { query ->
                query.execute()
                while (query.next()) {
                    // data from result set
                    val exampleId = query.exampleId
                    val text = query.text
                    val rel = query.rel
                    val args = query.args
                    val aspect = query.aspect
                    val form = query.form
                    val tense = query.tense
                    val voice = query.voice
                    val person = query.person
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(PbExample(exampleId, text, rel, args, aspect, form, tense, voice, person))
                }
            }
            return result
        }
    }
}