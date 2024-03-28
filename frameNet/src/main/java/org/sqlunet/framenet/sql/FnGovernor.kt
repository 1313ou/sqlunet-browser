/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * Governor
 *
 * @param governorId governor id
 * @param wordId     word id
 * @param governor   governor
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class FnGovernor private constructor(
    @JvmField val governorId: Long,
    @JvmField val wordId: Long,
    @JvmField val governor: String,
) {
    companion object {

        /**
         * Make set of governors from query built from lex unit
         *
         * @param connection connection
         * @param luId       target lex unit id
         * @return list of governors
         */
        @JvmStatic
        fun make(connection: SQLiteDatabase, luId: Long): List<FnGovernor?>? {
            var result: MutableList<FnGovernor?>? = null
            FnGovernorQueryFromLexUnitId(connection, luId).use {
                it.execute()
                while (it.next()) {
                    val governorId = it.governorId
                    val wordId = it.wordId
                    val governor = it.governor
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(FnGovernor(governorId, wordId, governor))
                }
            }
            return result
        }
    }
}
