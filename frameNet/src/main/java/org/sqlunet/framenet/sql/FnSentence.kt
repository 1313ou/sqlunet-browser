/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * Sentence
 *
 * @param sentenceId sentence id
 * @param text       sentence text
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnSentence internal constructor(
    @JvmField val sentenceId: Long,
    @JvmField val text: String,
) {
    companion object {

        /**
         * Make sets of sentences from query built from frameId
         *
         * @param connection connection
         * @param sentenceId is the sentence id to build query from
         * @return sentence
         */
        @JvmStatic
        fun make(connection: SQLiteDatabase, sentenceId: Long): FnSentence? {
            FnSentenceQuery(connection, sentenceId).use { query ->
                query.execute()
                if (query.next()) {
                    // var sentenceId = query.sentenceId
                    val text = query.text
                    return FnSentence(sentenceId, text)
                }
            }
            return null
        }

        /**
         * Make sets of sentences from query built from lex unit id
         *
         * @param connection connection
         * @param luId       is the lex unit id to build query from
         * @return list of sentences
         */
        @JvmStatic
        fun makeFromLexicalUnit(connection: SQLiteDatabase, luId: Long): List<FnSentence?>? {
            var result: MutableList<FnSentence?>? = null
            FnSentenceQueryFromLexUnitId(connection, luId).use { query ->
                query.execute()
                while (query.next()) {
                    val sentenceId = query.sentenceId
                    val text = query.text
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(FnSentence(sentenceId, text))
                }
            }
            return result
        }
    }
}
