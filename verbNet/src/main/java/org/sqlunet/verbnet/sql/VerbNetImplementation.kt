/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.dom.DomFactory.makeDocument
import org.sqlunet.dom.DomTransformer.docToString
import org.sqlunet.sql.NodeFactory.makeNode
import org.sqlunet.verbnet.sql.VnClass.Companion.make
import org.sqlunet.verbnet.sql.VnClassWithSense.Companion.make
import org.sqlunet.verbnet.sql.VnEntry.Companion.make
import org.sqlunet.verbnet.sql.VnNodeFactory.makeVnClassNode
import org.sqlunet.verbnet.sql.VnNodeFactory.makeVnClassWithSenseNode
import org.sqlunet.verbnet.sql.VnNodeFactory.makeVnFrameNode
import org.sqlunet.verbnet.sql.VnNodeFactory.makeVnFramesNode
import org.sqlunet.verbnet.sql.VnNodeFactory.makeVnRoleNode
import org.sqlunet.verbnet.sql.VnNodeFactory.makeVnRolesNode
import org.sqlunet.verbnet.sql.VnNodeFactory.makeVnRootClassNode
import org.sqlunet.verbnet.sql.VnNodeFactory.makeVnRootNode
import org.sqlunet.wordnet.sql.WnNodeFactory.makeSenseNode
import org.sqlunet.wordnet.sql.WnNodeFactory.makeSynsetNode
import org.sqlunet.wordnet.sql.WnNodeFactory.makeWordNode
import org.w3c.dom.Document
import org.w3c.dom.Node

/**
 * Encapsulates VerbNet query implementation
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class VerbNetImplementation : VerbNetInterface {

    // S E L E C T O R

    /**
     * Business method that returns VerbNet selector data as DOM document
     *
     * @param connection connection
     * @param word       target word
     * @return VerbNet selector data as DOM document
     */
    override fun querySelectorDoc(connection: SQLiteDatabase, word: String): Document {
        val doc = makeDocument()
        val rootNode = makeVnRootNode(doc, word)
        walkSelector(connection, doc, rootNode, word)
        return doc
    }

    /**
     * Business method that returns VerbNet selector data as XML
     *
     * @param connection connection
     * @param word       target word
     * @return VerbNet selector data as XML
     */
    override fun querySelectorXML(connection: SQLiteDatabase, word: String): String {
        val doc = querySelectorDoc(connection, word)
        return docToString(doc)
    }

    // D E T A I L

    /**
     * Business method that returns VerbNet data as DOM document from word
     *
     * @param connection connection
     * @param word       target word
     * @return VerbNet data as DOM document
     */
    override fun queryDoc(connection: SQLiteDatabase, word: String): Document {
        val doc = makeDocument()
        val rootNode = makeVnRootNode(doc, word)
        walk(connection, doc, rootNode, word)
        return doc
    }

    /**
     * Business method that returns VerbNet data as XML
     *
     * @param connection connection
     * @param word       target word
     * @return VerbNet data as XML
     */
    override fun queryXML(connection: SQLiteDatabase, word: String): String {
        val doc = queryDoc(connection, word)
        return docToString(doc)
    }

    /**
     * Business method that returns VerbNet data as DOM document from sense
     *
     * @param connection connection
     * @param wordId     word id to build query from
     * @param synsetId   synset id to build query from (null if any)
     * @param pos        pos to build query from
     * @return VerbNet data as DOM document
     */
    override fun queryDoc(connection: SQLiteDatabase, wordId: Long, synsetId: Long, pos: Char): Document {
        val doc = makeDocument()
        val rootNode = makeVnRootNode(doc, wordId, synsetId)
        walk(connection, doc, rootNode, wordId, synsetId, roles = true, frames = true)
        return doc
    }

    /**
     * Business method that returns VerbNet data as XML from sense
     *
     * @param connection connection
     * @param wordId     target word id
     * @param synsetId   target synset id (null if any)
     * @param pos        pos to build query from
     * @return VerbNet data as XML
     */
    override fun queryXML(connection: SQLiteDatabase, wordId: Long, synsetId: Long, pos: Char): String {
        val doc = queryDoc(connection, wordId, synsetId, pos)
        return docToString(doc)
    }

    // class

    override fun queryClassDoc(connection: SQLiteDatabase, classId: Long, pos: Char?): Document {
        val doc = makeDocument()
        val rootNode = makeVnRootClassNode(doc, classId)
        walkClass(connection, doc, rootNode, classId)
        return doc
    }

    /**
     * Business method that returns class data as XML from class id
     *
     * @param connection connection
     * @param classId    class to build query from
     * @param pos        pos to build query from
     * @return VerbNet class data as XML
     */
    override fun queryClassXML(connection: SQLiteDatabase, classId: Long, pos: Char?): String {
        val doc = queryClassDoc(connection, classId, pos)
        return docToString(doc)
    }

    companion object {

        const val VN_NS = "http://org.sqlunet/vn"

        // W A L K

        /**
         * Perform queries for VerbNet selector data
         *
         * @param connection connection
         * @param doc        org.w3c.dom.Document being built
         * @param parent     org.w3c.dom.Node walk will attach results to
         * @param targetWord target word
         */
        private fun walkSelector(connection: SQLiteDatabase, doc: Document, parent: Node, targetWord: String) {
            // entry
            val entry = make(connection, targetWord) ?: return

            // word
            // NodeFactory.makeWordNode(doc, parent, entry.word.word, entry.word.id)

            // iterate synsets
            val synsets = entry.synsets ?: return
            var currentId = -1L
            var currentFlag = false
            for (synset in synsets) {
                // select
                val isFlagged = synset.flag
                val isSame = currentId == synset.synsetId
                val wasFlagged = currentFlag
                currentId = synset.synsetId
                currentFlag = isFlagged
                if (isSame && !isFlagged && wasFlagged) {
                    continue
                }

                // verbnet nodes
                walk(connection, doc, parent, entry.word.id, synset.synsetId, roles = false, frames = false)
            }
        }

        /**
         * Perform queries for VerbNet data from word
         *
         * @param connection connection
         * @param doc        org.w3c.dom.Document being built
         * @param parent     org.w3c.dom.Node walk will attach results to
         * @param targetWord target word
         */
        private fun walk(connection: SQLiteDatabase, doc: Document, parent: Node, targetWord: String) {
            // entry
            val entry = make(connection, targetWord) ?: return

            // word
            makeWordNode(doc, parent, entry.word.word, entry.word.id)

            // iterate synsets
            val synsets = entry.synsets ?: return
            var i = 1
            var currentId = -1L
            var currentFlag = false
            for (synset in synsets) {
                // select
                val isFlagged = synset.flag
                val isSame = currentId == synset.synsetId
                val wasFlagged = currentFlag
                currentId = synset.synsetId
                currentFlag = isFlagged
                if (isSame && !isFlagged && wasFlagged) {
                    continue
                }

                // sense node
                val senseNode: Node = makeSenseNode(doc, parent, entry.word.id, synset.synsetId, i++)

                // verbnet nodes
                walk(connection, doc, senseNode, entry.word.id, synset.synsetId, roles = true, frames = true)
            }
        }

        /**
         * Perform queries for VerbNet data from sense
         *
         * @param connection     data source
         * @param doc            org.w3c.dom.Document being built
         * @param parent         org.w3c.dom.Node walk will attach results to
         * @param targetWordId   target word id
         * @param targetSynsetId target synset id (null for any)
         * @param roles          whether to include roles
         * @param frames         whether to include frames
         */
        private fun walk(connection: SQLiteDatabase, doc: Document, parent: Node, targetWordId: Long, targetSynsetId: Long?, roles: Boolean, frames: Boolean) {
            // classes
            val vnClasses = make(connection, targetWordId, targetSynsetId)
            if (vnClasses != null) {
                for (vnClass in vnClasses) {
                    // class
                    val classNode = makeVnClassWithSenseNode(doc, parent, vnClass!!)
                    if (vnClass.synsetId != 0L) {
                        // sense node
                        val senseNode: Node = makeSenseNode(doc, classNode, vnClass.wordId, vnClass.synsetId!!, vnClass.senseNum)

                        // synset nodes
                        val synsetNode: Node = makeSynsetNode(doc, senseNode, vnClass.synsetId, 0)

                        // gloss
                        makeNode(doc, synsetNode, "definition", vnClass.definition)
                    }

                    // roles
                    if (roles) {
                        val rolesNode = makeVnRolesNode(doc, classNode)
                        val roleSet = VnRoleSet.make(connection, vnClass.classId, targetWordId, targetSynsetId)
                        if (roleSet != null) {
                            var j = 1
                            for (role in roleSet.roles) {
                                makeVnRoleNode(doc, rolesNode, role, j)
                                j++
                            }
                        }
                    }

                    // frames
                    if (frames) {
                        val framesNode = makeVnFramesNode(doc, classNode)
                        val frameSet = VnFrameSet.make(connection, vnClass.classId, targetWordId, targetSynsetId)
                        if (frameSet != null) {
                            var j = 1
                            for (frame in frameSet.frames) {
                                makeVnFrameNode(doc, framesNode, frame, j)
                                j++
                            }
                        }
                    }
                }
            }
        }

        // I T E M S

        /**
         * Perform queries for VerbNet data from class id
         *
         * @param connection data source
         * @param doc        org.w3c.dom.Document being built
         * @param parent     org.w3c.dom.Node walk will attach results to
         * @param classId    target class id
         */
        private fun walkClass(connection: SQLiteDatabase, doc: Document, parent: Node, classId: Long) {
            // class
            val clazz = make(connection, classId)!!
            val classNode = makeVnClassNode(doc, parent, clazz)

            // roles
            val rolesNode = makeVnRolesNode(doc, classNode)
            val roleSet = VnRoleSet.make(connection, clazz.classId)
            if (roleSet != null) {
                var j = 1
                for (role in roleSet.roles) {
                    makeVnRoleNode(doc, rolesNode, role, j)
                    j++
                }
            }

            // frames
            val framesNode = makeVnFramesNode(doc, classNode)
            val frameSet = VnFrameSet.make(connection, clazz.classId)
            if (frameSet != null) {
                var j = 1
                for (frame in frameSet.frames) {
                    makeVnFrameNode(doc, framesNode, frame, j)
                    j++
                }
            }
        }
    }
}
