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
            FnGovernorQueryFromLexUnitId(connection, luId).use { query ->
                query.execute()
                while (query.next()) {
                    val governorId = query.governorId
                    val wordId = query.wordId
                    val governor = query.governor
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
