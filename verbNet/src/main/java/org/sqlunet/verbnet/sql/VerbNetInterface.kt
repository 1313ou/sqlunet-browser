/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

import android.database.sqlite.SQLiteDatabase
import org.w3c.dom.Document

/**
 * Business methods for VerbNet interface
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal interface VerbNetInterface {

    // S E L E C T O R

    /**
     * Business method that returns VerbNet selector data as DOM document
     *
     * @param word target word
     * @return VerbNet selector data as DOM document
     */
    fun querySelectorDoc(connection: SQLiteDatabase, word: String): Document?

    /**
     * Business method that returns VerbNet selector data as XML
     *
     * @param word target word
     * @return VerbNet selector data as XML
     */
    fun querySelectorXML(connection: SQLiteDatabase, word: String): String

    // D E T A I L

    /**
     * Business method that returns VerbNet data as DOM document from word
     *
     * @param word target word
     * @return VerbNet data as DOM document
     */
    fun queryDoc(connection: SQLiteDatabase, word: String): Document?

    /**
     * Business method that returns VerbNet data as XML from word
     *
     * @param word target word
     * @return VerbNet data as XML
     */
    fun queryXML(connection: SQLiteDatabase, word: String): String

    /**
     * Business method that returns VerbNet data as DOM document from sense
     *
     * @param wordId   word id to build query from
     * @param synsetId synset id to build query from (null if any)
     * @param pos      pos to build query from
     * @return VerbNet data as DOM document
     */
    fun queryDoc(connection: SQLiteDatabase, wordId: Long, synsetId: Long, pos: Char): Document?

    /**
     * Business method that returns VerbNet data as XML from sense
     *
     * @param wordId   target word id
     * @param synsetId target synset id (null if any)
     * @param pos      pos to build query from
     * @return VerbNet data as XML
     */
    fun queryXML(connection: SQLiteDatabase, wordId: Long, synsetId: Long, pos: Char): String

    // I T E M S

    /**
     * Business method that returns class data as DOM document from class id
     *
     * @param connection connection
     * @param classId    class to build query from
     * @param pos        pos to build query from
     * @return VerbNet class data as DOM document
     */
    fun queryClassDoc(connection: SQLiteDatabase, classId: Long, pos: Char): Document?

    /**
     * Business method that returns class data as XML
     *
     * @param connection connection
     * @param classId    class to build query from
     * @param pos        pos to build query from
     * @return VerbNet class data as XML
     */
    fun queryClassXML(connection: SQLiteDatabase, classId: Long, pos: Char): String
}
