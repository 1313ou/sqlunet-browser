/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.bnc.sql

import android.database.sqlite.SQLiteDatabase
import org.w3c.dom.Document

internal interface BncInterface {
    /**
     * Business method that returns BNC data as DOM document
     *
     * @param connection connection
     * @param word       the target word
     * @return BNC data as DOM document
     */
    fun queryDoc(connection: SQLiteDatabase, word: String): Document?

    /**
     * Business method that returns BNC data as XML
     *
     * @param connection connection
     * @param word       the target word
     * @return BNC data as XML
     */
    fun queryXML(connection: SQLiteDatabase, word: String): String

    /**
     * Business method that returns BNC data as DOM document
     *
     * @param connection connection
     * @param wordId     the word id to build query from
     * @param pos        the pos to build query from (null if any)
     * @return BNC data as DOM document
     */
    fun queryDoc(connection: SQLiteDatabase, wordId: Long, pos: Char? = null): Document?

    /**
     * Business method that returns BNC data as XML
     *
     * @param connection connection
     * @param wordId     the target word id
     * @param pos        the target pos (null if any)
     * @return BNC data as XML
     */
    fun queryXML(connection: SQLiteDatabase, wordId: Long, pos: Char? = null): String
}