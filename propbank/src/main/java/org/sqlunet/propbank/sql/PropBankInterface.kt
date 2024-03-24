/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.sql

import android.database.sqlite.SQLiteDatabase
import org.w3c.dom.Document

/**
 * Business methods fro PropBank interface
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal interface PropBankInterface {

    // S E L E C T O R

    /**
     * Business method that returns PropBank selector data as DOM document
     *
     * @param word target word
     * @return PropBank selector data as DOM document
     */
    fun querySelectorDoc(connection: SQLiteDatabase, word: String): Document?

    /**
     * Business method that returns PropBank selector data as XML
     *
     * @param word target word
     * @return PropBank selector data as XML
     */
    fun querySelectorXML(connection: SQLiteDatabase, word: String): String

    // D E T A I L

    /**
     * Business method that returns PropBank data as DOM document from word
     *
     * @param word target word
     * @return PropBank data as DOM document
     */
    fun queryDoc(connection: SQLiteDatabase, word: String): Document?

    /**
     * Business method that returns PropBank selector data as XML from word
     *
     * @param word target word
     * @return PropBank selector data as XML
     */
    fun queryXML(connection: SQLiteDatabase, word: String): String

    /**
     * Business method that returns PropBank data as DOM document from word id
     *
     * @param wordId is the word id to build query from
     * @param pos    the pos to build query from
     * @return PropBank data as DOM document
     */
    fun queryDoc(connection: SQLiteDatabase, wordId: Long, pos: Char): Document?

    /**
     * Business method that returns PropBank data as XML from word id
     *
     * @param wordId target word id
     * @param pos    the pos to build query from
     * @return PropBank data as XML
     */
    fun queryXML(connection: SQLiteDatabase, wordId: Long, pos: Char): String

    // I T E M S

    /**
     * Business method that returns role set data as DOM document from role set id
     *
     * @param connection connection
     * @param roleSetId  the role set to build query from
     * @param pos        the pos to build query from
     * @return PropBank role set data as DOM document
     */
    fun queryRoleSetDoc(connection: SQLiteDatabase, roleSetId: Long, pos: Char?): Document?

    /**
     * Business method that returns role set data as XML from role set id
     *
     * @param connection connection
     * @param roleSetId  the role set id to build query from
     * @param pos        the pos to build query from
     * @return PropBank role set data as XML
     */
    fun queryRoleSetXML(connection: SQLiteDatabase, roleSetId: Long, pos: Char?): String
}
