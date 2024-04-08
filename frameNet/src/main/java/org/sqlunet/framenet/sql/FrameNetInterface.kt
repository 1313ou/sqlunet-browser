/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase
import org.w3c.dom.Document

/**
 * Business methods for FrameNet interface
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal interface FrameNetInterface {

    // S E L E C T O R

    /**
     * Business method that returns FrameNet selector data as DOM document
     *
     * @param word target word
     * @param pos  target pos to build query from
     * @return FrameNet selector data as DOM document
     */
    fun querySelectorDoc(connection: SQLiteDatabase, word: String, pos: Char?): Document?

    /**
     * Business method that returns FrameNet selector data as XML
     *
     * @param word target word
     * @param pos  target pos to build query from
     * @return FrameNet selector data as XML
     */
    fun querySelectorXML(connection: SQLiteDatabase, word: String, pos: Char?): String

    // D E T A I L

    /**
     * Business method that returns FrameNet data as DOM document
     *
     * @param wordId target word id to build query from
     * @param pos    target pos to build query from
     * @return FrameNet data as DOM document
     */
    fun queryDoc(connection: SQLiteDatabase, wordId: Long, pos: Char?): Document?

    /**
     * Business method that returns FrameNet data as XML
     *
     * @param wordId target word id
     * @param pos    target pos to build query from
     * @return FrameNet data as XML
     */
    fun queryXML(connection: SQLiteDatabase, wordId: Long, pos: Char?): String

    /**
     * Business method that returns FrameNet data as DOM document
     *
     * @param word target word
     * @param pos  target pos to build query from
     * @return FrameNet data as DOM document
     */
    fun queryDoc(connection: SQLiteDatabase, word: String, pos: Char?): Document?

    /**
     * Business method that returns FrameNet data as XML
     *
     * @param word target word
     * @param pos  target pos to build query from
     * @return FrameNet data as XML
     */
    fun queryXML(connection: SQLiteDatabase, word: String, pos: Char?): String

    // I T E M S

    /**
     * Business method that returns FrameNet frame data as DOM document
     *
     * @param connection connection
     * @param frameId    target frame to build query from
     * @param pos        target pos to build query from
     * @return FrameNet frame id data as DOM document
     */
    fun queryFrameDoc(connection: SQLiteDatabase, frameId: Long, pos: Char?): Document?

    /**
     * Business method that returns FrameNet frame data as XML
     *
     * @param connection connection
     * @param frameId    target frame id to build query from
     * @param pos        target pos to build query from
     * @return FrameNet frame data as XML
     */
    fun queryFrameXML(connection: SQLiteDatabase, frameId: Long, pos: Char?): String

    /**
     * Business method that returns FrameNet lex unit data as DOM document
     *
     * @param connection connection
     * @param luId       target lex unit id to build query from
     * @return FrameNet lex unit data as DOM document
     */
    fun queryLexUnitDoc(connection: SQLiteDatabase, luId: Long): Document?

    /**
     * Business method that returns FrameNet lex unit data as XML
     *
     * @param connection connection
     * @param luId       target lex unit id to build query from
     * @return FrameNet lex unit data as XML
     */
    fun queryLexUnitXML(connection: SQLiteDatabase, luId: Long): String
}
