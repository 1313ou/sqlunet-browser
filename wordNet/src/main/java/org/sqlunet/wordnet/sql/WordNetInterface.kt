/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

import android.database.sqlite.SQLiteDatabase
import org.w3c.dom.Document

/**
 * Business methods for WordNet interface
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal interface WordNetInterface {

    // S E L E C T O R

    /**
     * Business method that returns WordNet selector data as DOM document
     *
     * @param connection connection
     * @param word       target word
     * @return WordNet selector data as DOM document
     */
    fun querySelectorDoc(connection: SQLiteDatabase, word: String): Document?

    /**
     * Business method that returns WordNet selector data as XML
     *
     * @param connection connection
     * @param word       target word
     * @return WordNet selector data as XML
     */
    fun querySelectorXML(connection: SQLiteDatabase, word: String): String

    // D E T A I L

    /**
     * Business method that returns WordNet data as a Document
     *
     * @param connection    connection
     * @param word          target word
     * @param withRelations determines if queries are to include relations
     * @param recurse       determines if queries are to follow relations recursively
     * @return WordNet data as a DOM Document
     */
    fun queryDoc(connection: SQLiteDatabase, word: String, withRelations: Boolean, recurse: Boolean): Document?

    /**
     * Business method that returns WordNet data as XML
     *
     * @param connection    connection
     * @param word          target word
     * @param withRelations determines if queries are to include relations
     * @param recurse       determines if queries are to follow relations recursively
     * @return WordNet data as XML
     */
    fun queryXML(connection: SQLiteDatabase, word: String, withRelations: Boolean, recurse: Boolean): String

    /**
     * Business method that returns WordNet data as DOM document
     *
     * @param connection    connection
     * @param word          target word
     * @param posName       target part-of-speech
     * @param domainName    target domain
     * @param relationName  target relation name
     * @param withRelations determines if queries are to include relations
     * @param recurse       determines if queries are to follow relations recursively
     * @return WordNet data as DOM document
     */
    fun queryDoc(connection: SQLiteDatabase, word: String, posName: String, domainName: String, relationName: String, withRelations: Boolean, recurse: Boolean): Document?

    /**
     * Business method that returns WordNet data as XML
     *
     * @param connection    connection
     * @param word          target word
     * @param posName       target part-of-speech
     * @param domainName    target domain
     * @param relationName  target relation type name
     * @param withRelations determines if queries are to include relations
     * @param recurse       determines if queries are to follow relations recursively
     * @return WordNet data as XML data
     */
    fun queryXML(connection: SQLiteDatabase, word: String, posName: String, domainName: String, relationName: String, withRelations: Boolean, recurse: Boolean): String

    /**
     * Business method that returns WordNet data as a Document
     *
     * @param connection    connection
     * @param wordId        target word id
     * @param synsetId      target synset id
     * @param withRelations determines if queries are to include relations
     * @param recurse       determines if queries are to follow relations recursively
     * @return WordNet data as a DOM Document
     */
    fun queryDoc(connection: SQLiteDatabase, wordId: Long, synsetId: Long, withRelations: Boolean, recurse: Boolean): Document?

    // I T E M S

    /**
     * Business method that returns WordNet word data as DOM document
     *
     * @param connection connection
     * @param wordId     target word id
     * @return WordNet word data as DOM document
     */
    fun queryWordDoc(connection: SQLiteDatabase, wordId: Long): Document?

    /**
     * Business method that returns WordNet word data as XML
     *
     * @param connection connection
     * @param wordId     target word id
     * @return WordNet word data as XML
     */
    fun queryWordXML(connection: SQLiteDatabase, wordId: Long): String

    /**
     * Business method that returns WordNet sense data as DOM document
     *
     * @param connection connection
     * @param wordId     target word id
     * @param synsetId   target synset id
     * @return WordNet synset data as DOM document
     */
    fun querySenseDoc(connection: SQLiteDatabase, wordId: Long, synsetId: Long): Document?

    /**
     * Business method that returns WordNet sense data as XML
     *
     * @param connection connection
     * @param wordId     target word id
     * @param synsetId   target synset id
     * @return WordNet synset data as XML
     */
    fun querySenseXML(connection: SQLiteDatabase, wordId: Long, synsetId: Long): String

    /**
     * Business method that returns WordNet synset data as DOM document
     *
     * @param connection connection
     * @param synsetId   target synset id
     * @return WordNet synset data as DOM document
     */
    fun querySynsetDoc(connection: SQLiteDatabase, synsetId: Long): Document?

    /**
     * Business method that returns WordNet synset data as XML
     *
     * @param connection connection
     * @param synsetId   target synset id
     * @return WordNet synset data as XML
     */
    fun querySynsetXML(connection: SQLiteDatabase, synsetId: Long): String

    /**
     * Business method that returns WordNet parts-of-speech as array of strings
     *
     * @return array of Strings
     */
    val posNames: Array<String>

    /**
     * Business method that returns WordNet domains as array of strings
     *
     * @return array of Strings
     */
    val domainNames: Array<String>

    /**
     * Business method that returns WordNet relation names as array of strings
     *
     * @return array of Strings
     */
    val relationNames: Array<String>
}