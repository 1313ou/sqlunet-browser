/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.sql

import android.database.sqlite.SQLiteDatabase
import org.w3c.dom.Document

/**
 * Business methods for SyntagNet interface
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal interface SyntagNetInterface {

    // S E L E C T O R

    /**
     * Business method that returns SyntagNet selector data as DOM document
     *
     * @param word target word
     * @return SyntagNet selector data as DOM document
     */
    fun querySelectorDoc(connection: SQLiteDatabase, word: String): Document?

    /**
     * Business method that returns SyntagNet selector data as XML
     *
     * @param word target word
     * @return SyntagNet selector data as XML
     */
    fun querySelectorXML(connection: SQLiteDatabase, word: String): String

    // D E T A I L

    /**
     * Business method that returns SyntagNet data as DOM document from word
     *
     * @param word target word
     * @return SyntagNet data as DOM document
     */
    fun queryDoc(connection: SQLiteDatabase, word: String): Document?

    /**
     * Business method that returns SyntagNet selector data as XML from word
     *
     * @param word target word
     * @return SyntagNet selector data as XML
     */
    fun queryXML(connection: SQLiteDatabase, word: String): String

    /**
     * Business method that returns SyntagNet data as DOM document from word id
     *
     * @param wordId   is the word id to build query from
     * @param synsetId is the synset id to build query from (nullable)
     * @param pos      the pos to build query from (nullable)
     * @return SyntagNet data as DOM document
     */
    fun queryDoc(connection: SQLiteDatabase, wordId: Long, synsetId: Long?, pos: Char?): Document?

    /**
     * Business method that returns SyntagNet data as XML from word id
     *
     * @param wordId   target word id
     * @param synsetId is the synset id to build query from (nullable)
     * @param pos      the pos to build query from (nullable)
     * @return SyntagNet data as XML
     */
    fun queryXML(connection: SQLiteDatabase, wordId: Long, synsetId: Long?, pos: Char?): String

    /**
     * Business method that returns SyntagNet data as DOM document from word id
     *
     * @param wordId   is the word id to build query from
     * @param synsetId is the synset id to build query from (nullable)
     * @param word2Id   is the word 2 id to build query from
     * @param synset2Id is the synset 2 id to build query from (nullable)
     * @return SyntagNet data as DOM document
     */
    fun queryDoc(connection: SQLiteDatabase, wordId: Long, synsetId: Long?, word2Id: Long, synset2Id: Long?): Document?

    /**
     * Business method that returns SyntagNet data as XML from word id
     *
     * @param wordId   target word id
     * @param synsetId is the synset id to build query from (nullable)
     * @param word2Id   is the word 2 id to build query from
     * @param synset2Id is the synset 2 id to build query from (nullable)
     * @return SyntagNet data as XML
     */
    fun queryXML(connection: SQLiteDatabase, wordId: Long, synsetId: Long?, word2Id: Long, synset2Id: Long?): String

    // I T E M S

    /**
     * Business method that returns collocation data as DOM document from collocation id
     *
     * @param connection    connection
     * @param collocationId the collocation to build query from
     * @return SyntagNet collocation data as DOM document
     */
    fun queryCollocationDoc(connection: SQLiteDatabase, collocationId: Long): Document?

    /**
     * Business method that returns collocation data as XML from collocation id
     *
     * @param connection    connection
     * @param collocationId the collocation id to build query from
     * @return SyntagNet collocation data as XML
     */
    fun queryCollocationXML(connection: SQLiteDatabase, collocationId: Long): String
}
