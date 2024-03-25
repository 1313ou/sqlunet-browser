/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * AnnoSet
 *
 * @param annoSetId annoSet id
 * @param sentence  sentence
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnAnnoSet private constructor(
    @JvmField val annoSetId: Long,
    @JvmField val sentence: FnSentence,
) {
    companion object {

        /**
         * Make annoSet
         *
         * @param connection connection
         * @param annoSetId  annoSet id
         * @return annoSet
         */
        @JvmStatic
        fun make(connection: SQLiteDatabase, annoSetId: Long): FnAnnoSet? {
            FnAnnoSetQuery(connection, annoSetId).use { query ->
                query.execute()
                if (query.next()) {
                    val sentenceId = query.sentenceId
                    val text = query.sentenceText
                    val sentence = FnSentence(sentenceId, text)
                    return FnAnnoSet(annoSetId, sentence)
                }
            }
            return null
        }
    }
}