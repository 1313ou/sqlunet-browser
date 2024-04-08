/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.dom.DomFactory.makeDocument
import org.sqlunet.dom.DomTransformer.docToString
import org.sqlunet.propbank.sql.PbNodeFactory.makePbExampleNode
import org.sqlunet.propbank.sql.PbNodeFactory.makePbRoleNode
import org.sqlunet.propbank.sql.PbNodeFactory.makePbRoleSetNode
import org.sqlunet.propbank.sql.PbNodeFactory.makePbRootNode
import org.sqlunet.propbank.sql.PbNodeFactory.makePbRootRoleSetNode
import org.sqlunet.propbank.sql.PbRoleSet.Companion.makeFromWord
import org.sqlunet.propbank.sql.PbRoleSet.Companion.makeFromWordId
import org.w3c.dom.Document
import org.w3c.dom.Node

/**
 * Encapsulates PropBank query implementation
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class PropBankImplementation : PropBankInterface {

    /**
     * Business method that returns PropBank selector data as DOM document
     *
     * @param connection connection
     * @param word       target word
     * @return PropBank selector data as DOM document
     */
    override fun querySelectorDoc(connection: SQLiteDatabase, word: String): Document {
        val doc = makeDocument()
        val rootNode = makePbRootNode(doc, word)
        walkSelector(connection, doc, rootNode, word)
        return doc
    }

    /**
     * Business method that returns PropBank selector data as XML
     *
     * @param connection connection
     * @param word       target word
     * @return PropBank selector data as XML
     */
    override fun querySelectorXML(connection: SQLiteDatabase, word: String): String {
        val doc = querySelectorDoc(connection, word)
        return docToString(doc)
    }

    /**
     * Business method that returns PropBank data as DOM document from word
     *
     * @param connection connection
     * @param word       target word
     * @return PropBank data as DOM document
     */
    override fun queryDoc(connection: SQLiteDatabase, word: String): Document {
        val doc = makeDocument()
        val rootNode = makePbRootNode(doc, word)
        walk(connection, doc, rootNode, word)
        return doc
    }

    /**
     * Business method that returns PropBank data as XML from word
     *
     * @param connection connection
     * @param word       target word
     * @return PropBank data as XML
     */
    override fun queryXML(connection: SQLiteDatabase, word: String): String {
        val doc = queryDoc(connection, word)
        return docToString(doc)
    }

    /**
     * Business method that returns PropBank data as DOM document from word id
     *
     * @param connection connection
     * @param wordId     word id to build query from
     * @param pos        pos to build query from
     * @return PropBank data as DOM document
     */
    override fun queryDoc(connection: SQLiteDatabase, wordId: Long, pos: Char?): Document {
        val doc = makeDocument()
        val wordNode = makePbRootNode(doc, wordId)
        walk(connection, doc, wordNode, wordId)
        return doc
    }

    /**
     * Business method that returns PropBank data as XML from word id
     *
     * @param connection connection
     * @param wordId     target word id
     * @param pos        pos to build query from
     * @return PropBank data as XML
     */
    override fun queryXML(connection: SQLiteDatabase, wordId: Long, pos: Char?): String {
        val doc = queryDoc(connection, wordId, pos)
        return docToString(doc)
    }

    /**
     * Business method that returns role set data as DOM document from role set id
     *
     * @param connection connection
     * @param roleSetId  role set to build query from
     * @param pos        pos to build query from
     * @return PropBank role set data as DOM document
     */
    override fun queryRoleSetDoc(connection: SQLiteDatabase, roleSetId: Long, pos: Char?): Document {
        val doc = makeDocument()
        val rootNode = makePbRootRoleSetNode(doc, roleSetId)
        walkRoleSet(connection, doc, rootNode, roleSetId)
        return doc
    }

    /**
     * Business method that returns role set data as XML from role set id
     *
     * @param connection connection
     * @param roleSetId  role set id to build query from
     * @param pos        pos to build query from
     * @return PropBank role set data as XML
     */
    override fun queryRoleSetXML(connection: SQLiteDatabase, roleSetId: Long, pos: Char?): String {
        val doc = queryRoleSetDoc(connection, roleSetId, pos)
        return docToString(doc)
    }

    companion object {

        const val PB_NS = "http://org.sqlunet/pb"

        /**
         * Perform queries for PropBank selector data from word
         *
         * @param connection connection
         * @param doc        org.w3c.dom.Document being built
         * @param parent     org.w3c.dom.Node the walk will attach results to
         * @param targetWord target word
         */
        private fun walkSelector(connection: SQLiteDatabase, doc: Document, parent: Node, targetWord: String) {
            val roleSets = makeFromWord(connection, targetWord)
            makeSelector(doc, parent, roleSets)
        }

        /**
         * Perform queries for PropBank data from word
         *
         * @param connection connection
         * @param doc        org.w3c.dom.Document being built
         * @param parent     org.w3c.dom.Node the walk will attach results to
         * @param targetWord target word
         */
        private fun walk(connection: SQLiteDatabase, doc: Document, parent: Node, targetWord: String) {
            val roleSets = makeFromWord(connection, targetWord)
            makeSelector(doc, parent, roleSets)
        }

        /**
         * Perform queries for PropBank data from word id
         *
         * @param connection   data source
         * @param doc          org.w3c.dom.Document being built
         * @param parent       org.w3c.dom.Node the walk will attach results to
         * @param targetWordId target word id
         */
        private fun walk(connection: SQLiteDatabase, doc: Document, parent: Node, targetWordId: Long) {
            val roleSets = makeFromWordId(connection, targetWordId)
            if (roleSets != null) {
                walk(connection, doc, parent, roleSets)
            }
        }

        /**
         * Perform queries for PropBank data from role set id
         *
         * @param connection data source
         * @param doc        org.w3c.dom.Document being built
         * @param parent     org.w3c.dom.Node the walk will attach results to
         * @param roleSetId  role set id
         */
        private fun walkRoleSet(connection: SQLiteDatabase, doc: Document, parent: Node, roleSetId: Long) {
            // role sets
            val roleSets = PbRoleSet.make(connection, roleSetId)
            if (roleSets != null) {
                walk(connection, doc, parent, roleSets)
            }
        }

        /**
         * Query PropBank data from role sets
         *
         * @param connection data source
         * @param doc        org.w3c.dom.Document being built
         * @param parent     org.w3c.dom.Node the walk will attach results to
         * @param roleSets   role sets
         */
        private fun walk(connection: SQLiteDatabase, doc: Document, parent: Node, roleSets: Iterable<PbRoleSet?>) {
            // role sets
            var i = 1
            for (roleSet in roleSets) {
                // role set
                val roleSetNode = makePbRoleSetNode(doc, parent, roleSet!!, i++)

                // roles
                val roles = PbRole.make(connection, roleSet.roleSetId)
                if (roles != null) {
                    for (role in roles) {
                        makePbRoleNode(doc, roleSetNode, role!!)
                    }
                }
                // examples
                val examples = PbExample.make(connection, roleSet.roleSetId)
                if (examples != null) {
                    for (example in examples) {
                        makePbExampleNode(doc, roleSetNode, example!!)
                    }
                }
            }
        }

        /**
         * Display query results for PropBank data from query result
         *
         * @param doc      org.w3c.dom.Document being built
         * @param parent   org.w3c.dom.Node the walk will attach results to
         * @param roleSets role sets
         */
        private fun makeSelector(doc: Document, parent: Node, roleSets: Iterable<PbRoleSet?>?) {
            // role sets
            if (roleSets != null) {
                var i = 1
                for (roleSet in roleSets) {
                    // role set
                    makePbRoleSetNode(doc, parent, roleSet!!, i++)
                }
            }
        }
    }
}
