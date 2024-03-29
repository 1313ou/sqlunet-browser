/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.bnc.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.bnc.sql.BncData.Companion.makeData
import org.sqlunet.bnc.sql.BncNodeFactory.makeBncNode
import org.sqlunet.bnc.sql.BncNodeFactory.makeBncRootNode
import org.sqlunet.dom.DomFactory
import org.sqlunet.dom.DomTransformer
import org.sqlunet.sql.NodeFactory
import org.w3c.dom.Document
import org.w3c.dom.Node

/**
 * Encapsulates BNC query implementation
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class BncImplementation : BncInterface {

    /**
     * Business method that returns BNC data as DOM document from word
     *
     * @param connection connection
     * @param word       the target word
     * @return BNC data as DOM document
     */
    override fun queryDoc(connection: SQLiteDatabase, word: String): Document {
        val doc = DomFactory.makeDocument()
        val rootNode: Node = NodeFactory.makeNode(doc, doc, "bnc", word, BNC_NS)
        walk(connection, doc, rootNode, word)
        return doc
    }

    /**
     * Business method that returns BNC data as XML from word
     *
     * @param connection connection
     * @param word       the target word
     * @return BNC data as XML
     */
    override fun queryXML(connection: SQLiteDatabase, word: String): String {
        val doc = queryDoc(connection, word)
        return DomTransformer.docToString(doc)
    }

    /**
     * Business method that returns BNC data as DOM document from word id and pos
     *
     * @param connection connection
     * @param wordId     the word id to build query from
     * @param pos        the pos to build query from (null if any)
     * @return BNC data as DOM document
     */
    override fun queryDoc(connection: SQLiteDatabase, wordId: Long, pos: Char?): Document {
        val doc = DomFactory.makeDocument()
        val rootNode = makeBncRootNode(doc, wordId, pos)
        walk(connection, doc, rootNode, wordId, pos)
        return doc
    }

    /**
     * Business method that returns BNC data as XML from word id and pos
     *
     * @param connection connection
     * @param wordId     the target word id
     * @param pos        the target pos (null if any)
     * @return BNC data as XML
     */
    override fun queryXML(connection: SQLiteDatabase, wordId: Long, pos: Char?): String {
        val doc = queryDoc(connection, wordId, pos)
        return DomTransformer.docToString(doc)
    }

    companion object {

        const val BNC_NS = "http://org.sqlunet/bc"

        /**
         * Perform queries for BNC data
         *
         * @param connection data source
         * @param doc        the org.w3c.dom.Document being built
         * @param parent     the org.w3c.dom.Node the walk will attach results to
         * @param targetWord the target word
         */
        private fun walk(connection: SQLiteDatabase, doc: Document, parent: Node, targetWord: String) {
            val datas = makeData(connection, targetWord)
            if (datas != null) {
                var i = 1
                for (data in datas) {
                    makeBncNode(doc, parent, data!!, i++)
                }
            }
        }

        /**
         * Perform queries for BNC data
         *
         * @param connection   data source
         * @param doc          the org.w3c.dom.Document being built
         * @param parent       the org.w3c.dom.Node the walk will attach results to
         * @param targetWordId the target word id
         * @param targetPos    the target pos (null for any)
         */
        private fun walk(connection: SQLiteDatabase, doc: Document, parent: Node, targetWordId: Long, targetPos: Char?) {
            val datas = makeData(connection, targetWordId, targetPos)
            if (datas != null) {
                var i = 1
                for (data in datas) {
                    makeBncNode(doc, parent, data!!, i++)
                }
            }
        }
    }
}
