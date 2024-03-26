/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.dom.DomFactory.makeDocument
import org.sqlunet.dom.DomTransformer.docToString
import org.sqlunet.sql.NodeFactory.makeNode
import org.sqlunet.syntagnet.sql.Collocation.Companion.makeSelectorFromWord
import org.sqlunet.syntagnet.sql.Collocation.WithDefinitionAndPos
import org.sqlunet.syntagnet.sql.Collocation.WithDefinitionAndPos.Companion.make
import org.sqlunet.syntagnet.sql.Collocation.WithDefinitionAndPos.Companion.makeFromWord
import org.sqlunet.syntagnet.sql.Collocation.WithDefinitionAndPos.Companion.makeFromWordId
import org.sqlunet.syntagnet.sql.Collocation.WithDefinitionAndPos.Companion.makeFromWordIdAndSynsetId
import org.sqlunet.syntagnet.sql.Collocation.WithDefinitionAndPos.Companion.makeFromWordIdAndSynsetIds
import org.sqlunet.syntagnet.sql.Collocation.WithDefinitionAndPos.Companion.makeFromWordIds
import org.sqlunet.syntagnet.sql.SnNodeFactory.makeCollocationNode
import org.sqlunet.syntagnet.sql.SnNodeFactory.makeSelectorCollocationNode
import org.sqlunet.syntagnet.sql.SnNodeFactory.makeSnRootNode
import org.w3c.dom.Document
import org.w3c.dom.Node

/**
 * Encapsulates SyntagNet query implementation
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SyntagNetImplementation : SyntagNetInterface {

    /**
     * Business method that returns SyntagNet selector data as DOM document
     *
     * @param connection connection
     * @param word       target word
     * @return SyntagNet selector data as DOM document
     */
    override fun querySelectorDoc(connection: SQLiteDatabase, word: String): Document {
        val doc = makeDocument()
        val rootNode = makeSnRootNode(doc, word)
        walkSelector(connection, doc, rootNode, word)
        return doc
    }

    /**
     * Business method that returns SyntagNet selector data as XML
     *
     * @param connection connection
     * @param word       target word
     * @return SyntagNet selector data as XML
     */
    override fun querySelectorXML(connection: SQLiteDatabase, word: String): String {
        val doc = querySelectorDoc(connection, word)
        return docToString(doc)
    }

    /**
     * Business method that returns SyntagNet data as DOM document from word
     *
     * @param connection connection
     * @param word       target word
     * @return SyntagNet data as DOM document
     */
    override fun queryDoc(connection: SQLiteDatabase, word: String): Document {
        val doc = makeDocument()
        val rootNode = makeSnRootNode(doc, word)
        walk(connection, doc, rootNode, word)
        return doc
    }

    /**
     * Business method that returns SyntagNet data as XML from word
     *
     * @param connection connection
     * @param word       target word
     * @return SyntagNet data as XML
     */
    override fun queryXML(connection: SQLiteDatabase, word: String): String {
        val doc = queryDoc(connection, word)
        return docToString(doc)
    }

    /**
     * Business method that returns SyntagNet data as DOM document from word id
     *
     * @param connection connection
     * @param wordId     word id to build query from
     * @param synsetId   is the synset id to build query from (nullable)
     * @param pos        pos to build query from (nullable)
     * @return SyntagNet data as DOM document
     */
    override fun queryDoc(connection: SQLiteDatabase, wordId: Long, synsetId: Long?, pos: Char?): Document {
        val doc = makeDocument()
        val wordNode = makeSnRootNode(doc, wordId)
        if (synsetId == null) {
            walk(connection, doc, wordNode, wordId)
        } else {
            walk(connection, doc, wordNode, wordId, synsetId)
        }
        return doc
    }

    /**
     * Business method that returns SyntagNet data as XML from word id
     *
     * @param connection connection
     * @param wordId     target word id
     * @param synsetId   is the synset id to build query from (nullable)
     * @param pos        pos to build query from (nullable)
     * @return SyntagNet data as XML
     */
    override fun queryXML(connection: SQLiteDatabase, wordId: Long, synsetId: Long?, pos: Char?): String {
        val doc = queryDoc(connection, wordId, synsetId, pos)
        return docToString(doc)
    }

    override fun queryDoc(connection: SQLiteDatabase, wordId: Long, synsetId: Long?, word2Id: Long, synset2Id: Long?): Document {
        val doc = makeDocument()
        val wordNode = makeSnRootNode(doc, wordId)
        if (synsetId == null || synset2Id == null) {
            walk2(connection, doc, wordNode, wordId, word2Id)
        } else {
            walk2(connection, doc, wordNode, wordId, synsetId, word2Id, synset2Id)
        }
        return doc
    }

    override fun queryXML(connection: SQLiteDatabase, wordId: Long, synsetId: Long?, word2Id: Long, synset2Id: Long?): String {
        val doc = queryDoc(connection, wordId, synsetId, word2Id, synset2Id)
        return docToString(doc)
    }

    /**
     * Business method that returns collocation data as DOM document from collocation id
     *
     * @param connection    connection
     * @param collocationId collocation to build query from
     * @return SyntagNet collocation data as DOM document
     */
    override fun queryCollocationDoc(connection: SQLiteDatabase, collocationId: Long): Document {
        val doc = makeDocument()
        val rootNode = makeSnRootNode(doc, collocationId)
        walkCollocation(connection, doc, rootNode, collocationId)
        return doc
    }

    /**
     * Business method that returns collocation data as XML from collocation id
     *
     * @param connection    connection
     * @param collocationId collocation id to build query from
     * @return SyntagNet collocation data as XML
     */
    override fun queryCollocationXML(connection: SQLiteDatabase, collocationId: Long): String {
        val doc = queryCollocationDoc(connection, collocationId)
        return docToString(doc)
    }

    companion object {
        const val SN_NS = "http://org.sqlunet/sn"
        // S E L E C T O R
        /**
         * Perform queries for SyntagNet selector data from word
         *
         * @param connection connection
         * @param doc        org.w3c.dom.Document being built
         * @param parent     org.w3c.dom.Node the walk will attach results to
         * @param targetWord target word
         */
        private fun walkSelector(connection: SQLiteDatabase, doc: Document, parent: Node, targetWord: String) {
            val collocations = makeSelectorFromWord(connection, targetWord)

            // word
            makeNode(doc, parent, "word", targetWord)

            // syntagnet nodes
            makeSelector(doc, parent, collocations)
        }

        /**
         * Perform queries for SyntagNet data from word
         *
         * @param connection connection
         * @param doc        org.w3c.dom.Document being built
         * @param parent     org.w3c.dom.Node the walk will attach results to
         * @param targetWord target word
         */
        private fun walk(connection: SQLiteDatabase, doc: Document, parent: Node, targetWord: String) {
            val collocations = makeFromWord(connection, targetWord)

            // word
            makeNode(doc, parent, "word", targetWord)

            // collocations
            var i = 1
            for (collocation in collocations) {
                makeCollocationNode(doc, parent, collocation, i++)
            }
        }
        // D E T A I L
        /**
         * Perform queries for SyntagNet data from word id
         *
         * @param connection   data source
         * @param doc          org.w3c.dom.Document being built
         * @param parent       org.w3c.dom.Node the walk will attach results to
         * @param targetWordId target word id
         */
        private fun walk(connection: SQLiteDatabase, doc: Document, parent: Node, targetWordId: Long) {
            // collocations
            val collocations = makeFromWordId(connection, targetWordId)
            walk(doc, parent, collocations)
        }

        /**
         * Perform queries for SyntagNet data from word id
         *
         * @param connection     data source
         * @param doc            org.w3c.dom.Document being built
         * @param parent         org.w3c.dom.Node the walk will attach results to
         * @param targetWordId   target word id
         * @param targetSynsetId target synset id
         */
        private fun walk(connection: SQLiteDatabase, doc: Document, parent: Node, targetWordId: Long, targetSynsetId: Long) {
            // collocations
            val collocations = makeFromWordIdAndSynsetId(connection, targetWordId, targetSynsetId)
            walk(doc, parent, collocations)
        }

        /**
         * Perform queries for SyntagNet data from word id
         *
         * @param connection   data source
         * @param doc          org.w3c.dom.Document being built
         * @param parent       org.w3c.dom.Node the walk will attach results to
         * @param targetWordId target word id
         */
        private fun walk2(connection: SQLiteDatabase, doc: Document, parent: Node, targetWordId: Long, targetWord2Id: Long) {
            // collocations
            val collocations = makeFromWordIds(connection, targetWordId, targetWord2Id)
            walk(doc, parent, collocations)
        }

        /**
         * Perform queries for SyntagNet data from word id
         *
         * @param connection     data source
         * @param doc            org.w3c.dom.Document being built
         * @param parent         org.w3c.dom.Node the walk will attach results to
         * @param targetWordId   target word id
         * @param targetSynsetId target synset id
         */
        private fun walk2(connection: SQLiteDatabase, doc: Document, parent: Node, targetWordId: Long, targetSynsetId: Long, targetWord2Id: Long, targetSynset2Id: Long) {
            // collocations
            val collocations = makeFromWordIdAndSynsetIds(connection, targetWordId, targetSynsetId, targetWord2Id, targetSynset2Id)
            walk(doc, parent, collocations)
        }

        /**
         * Perform queries for SyntagNet data from collocation id
         *
         * @param connection    data source
         * @param doc           org.w3c.dom.Document being built
         * @param parent        org.w3c.dom.Node the walk will attach results to
         * @param collocationId collocation id
         */
        private fun walkCollocation(connection: SQLiteDatabase, doc: Document, parent: Node, collocationId: Long) {
            // collocations
            val collocations = make(connection, collocationId)
            walk(doc, parent, collocations)
        }

        /**
         * Query SyntagNet data from collocations
         *
         * @param doc          org.w3c.dom.Document being built
         * @param parent       org.w3c.dom.Node the walk will attach results to
         * @param collocations collocations
         */
        private fun walk(doc: Document, parent: Node, collocations: Iterable<WithDefinitionAndPos>) {
            var i = 1
            for (collocation in collocations) {
                // collocation
                makeCollocationNode(doc, parent, collocation, i++)
            }
        }

        /**
         * Display query results for SyntagNet data from query result
         *
         * @param doc          org.w3c.dom.Document being built
         * @param parent       org.w3c.dom.Node the walk will attach results to
         * @param collocations collocations
         */
        private fun makeSelector(doc: Document, parent: Node, collocations: Iterable<Collocation>) {
            var i = 1
            for (collocation in collocations) {
                // collocation
                makeSelectorCollocationNode(doc, parent, collocation, i++)
            }
        }
    }
}
