/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.wordnet.sql.BasicWord

/**
 * VerbNet entry
 *
 * @param word    word string
 * @param synsets list of synsets attached to this entry
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class VnEntry private constructor(
    @JvmField val word: BasicWord,
    @JvmField val synsets: List<VnSynset>?,
) {
    companion object {
        /**
         * Make entry
         *
         * @param connection connection
         * @param word       target word
         * @return new entry or null
         */
        @JvmStatic
        fun make(connection: SQLiteDatabase, word: String): VnEntry? {
            VnClassQueryFromWordAndPos(connection, word).use {
                it.execute()
                var wordId: Long = 0
                var synsets: MutableList<VnSynset>? = null
                while (it.next()) {
                    wordId = it.wordId
                    if (synsets == null) {
                        synsets = ArrayList()
                    }
                    synsets.add(VnSynset(it))
                }
                if (wordId != 0L) {
                    return VnEntry(BasicWord(word, wordId), synsets)
                }
            }
            return null
        }
    }
}
