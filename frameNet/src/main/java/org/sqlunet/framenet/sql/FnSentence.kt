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
    val sentenceId: Long,
    val text: String,
) {

    companion object {

        /**
         * Make sets of sentences from query built from frameId
         *
         * @param connection connection
         * @param sentenceId is the sentence id to build query from
         * @return sentence
         */
        fun make(connection: SQLiteDatabase, sentenceId: Long): FnSentence? {
            FnSentenceQuery(connection, sentenceId).use {
                it.execute()
                if (it.next()) {
                    // var sentenceId = query.sentenceId
                    val text = it.text
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
        fun makeFromLexicalUnit(connection: SQLiteDatabase, luId: Long): List<FnSentence?>? {
            var result: MutableList<FnSentence?>? = null
            FnSentenceQueryFromLexUnitId(connection, luId).use {
                it.execute()
                while (it.next()) {
                    val sentenceId = it.sentenceId
                    val text = it.text
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
