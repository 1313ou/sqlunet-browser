/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.dom.DomFactory.makeDocument
import org.sqlunet.dom.DomTransformer.docToString
import org.sqlunet.framenet.sql.FnLayer.Companion.makeFromAnnoSet
import org.sqlunet.framenet.sql.FnLayer.Companion.makeFromSentence
import org.sqlunet.framenet.sql.FnLexUnit.Companion.makeFromFnWord
import org.sqlunet.framenet.sql.FnLexUnit.Companion.makeFromFnWordId
import org.sqlunet.framenet.sql.FnLexUnit.Companion.makeFromFrame
import org.sqlunet.framenet.sql.FnLexUnit.Companion.makeFromId
import org.sqlunet.framenet.sql.FnLexUnit.Companion.makeFromWord
import org.sqlunet.framenet.sql.FnLexUnit.Companion.makeFromWordId
import org.sqlunet.framenet.sql.FnNodeFactory.makeFnAnnoSetNode
import org.sqlunet.framenet.sql.FnNodeFactory.makeFnFENode
import org.sqlunet.framenet.sql.FnNodeFactory.makeFnFrameNode
import org.sqlunet.framenet.sql.FnNodeFactory.makeFnGovernorNode
import org.sqlunet.framenet.sql.FnNodeFactory.makeFnLayerNode
import org.sqlunet.framenet.sql.FnNodeFactory.makeFnLexunitNode
import org.sqlunet.framenet.sql.FnNodeFactory.makeFnRootAnnoSetNode
import org.sqlunet.framenet.sql.FnNodeFactory.makeFnRootFrameNode
import org.sqlunet.framenet.sql.FnNodeFactory.makeFnRootLexUnitNode
import org.sqlunet.framenet.sql.FnNodeFactory.makeFnRootNode
import org.sqlunet.framenet.sql.FnNodeFactory.makeFnRootSentenceNode
import org.sqlunet.framenet.sql.FnNodeFactory.makeFnSentenceNode
import org.sqlunet.framenet.sql.FnNodeFactory.makeFnSentencesNode
import org.sqlunet.framenet.sql.FnSentence.Companion.make
import org.sqlunet.framenet.sql.FnSentence.Companion.makeFromLexicalUnit
import org.sqlunet.wordnet.sql.WnNodeFactory.makeWordNode
import org.w3c.dom.Document
import org.w3c.dom.Node

/**
 * Encapsulates FrameNet query implementation
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FrameNetImplementation(private val queryFromFnWord: Boolean) : FrameNetInterface {

    // S E L E C T O R

   /**
     * Business method that returns FrameNet selector data as DOM document
     *
     * @param connection connection
     * @param word       the target (fn) word
     * @param pos        the pos to build query from
     * @return FrameNet selector data as DOM document
     */
    override fun querySelectorDoc(connection: SQLiteDatabase, word: String, pos: Char?): Document {
        val doc = makeDocument()
        val rootNode = makeFnRootNode(doc, word, pos)
        walkSelector(connection, doc, rootNode, word)
        return doc
    }

    /**
     * Business method that returns FrameNet selector data as XML
     *
     * @param connection connection
     * @param word       the target (fn) word
     * @param pos        the pos to build query from
     * @return FrameNet selector data as XML
     */
    override fun querySelectorXML(connection: SQLiteDatabase, word: String, pos: Char?): String {
        val doc = querySelectorDoc(connection, word, pos)
        return docToString(doc)
    }

    // D E T A I L

    /**
     * Business method that returns FrameNet data as DOM document
     *
     * @param connection connection
     * @param word       the target (fn) word
     * @param pos        the pos to build query from
     * @return FrameNet data as DOM document
     */
    override fun queryDoc(connection: SQLiteDatabase, word: String, pos: Char): Document {
        val doc = makeDocument()
        val rootNode = makeFnRootNode(doc, word, pos)
        walk(connection, doc, rootNode, word)
        return doc
    }

    /**
     * Business method that returns FrameNet data as XML
     *
     * @param connection connection
     * @param word       the target (fn) word
     * @param pos        the pos to build query from
     * @return FrameNet data as XML
     */
    override fun queryXML(connection: SQLiteDatabase, word: String, pos: Char): String {
        val doc = queryDoc(connection, word, pos)
        return docToString(doc)
    }

    // I T E M S

    // word

    /**
     * Business method that returns FrameNet data as DOM document
     *
     * @param connection connection
     * @param wordId     the target (fn) word id to build query from
     * @param pos        the pos to build query from
     * @return FrameNet data as DOM document
     */
    override fun queryDoc(connection: SQLiteDatabase, wordId: Long, pos: Char): Document {
        val doc = makeDocument()
        val rootNode = makeFnRootNode(doc, wordId, pos)
        walkWord(connection, doc, rootNode, wordId, pos)
        return doc
    }

    /**
     * Business method that returns FrameNet data as XML
     *
     * @param connection connection
     * @param wordId     the target (fn) word id
     * @param pos        the pos to build query from
     * @return FrameNet data as XML
     */
    override fun queryXML(connection: SQLiteDatabase, wordId: Long, pos: Char): String {
        val doc = queryDoc(connection, wordId, pos)
        return docToString(doc)
    }

    // frame

    /**
     * Business method that returns frame data as DOM document
     *
     * @param connection connection
     * @param frameId    the frame to build query from
     * @param pos        the pos to build query from
     * @return FrameNet frame data as DOM document
     */
    override fun queryFrameDoc(connection: SQLiteDatabase, frameId: Long, pos: Char?): Document {
        val doc = makeDocument()
        val rootNode = makeFnRootFrameNode(doc, frameId)
        walkFrame(connection, doc, rootNode, frameId)
        return doc
    }

    /**
     * Business method that returns frame data as XML
     *
     * @param connection connection
     * @param frameId    the frame to build query from
     * @param pos        the pos to build query from
     * @return FrameNet frame data as XML
     */
    override fun queryFrameXML(connection: SQLiteDatabase, frameId: Long, pos: Char?): String {
        val doc = queryFrameDoc(connection, frameId, pos)
        return docToString(doc)
    }

    // lexunit

    /**
     * Business method that returns lexunit data as DOM document
     *
     * @param connection connection
     * @param luId       the luId to build query from
     * @return FrameNet lexunit data as DOM document
     */
    override fun queryLexUnitDoc(connection: SQLiteDatabase, luId: Long): Document {
        val doc = makeDocument()
        val rootNode = makeFnRootLexUnitNode(doc, luId)
        walkLexUnit(connection, doc, rootNode, luId)
        return doc
    }

    /**
     * Business method that returns lexunit data as XML
     *
     * @param connection connection
     * @param luId       the luId to build query from
     * @return FrameNet lexunit data as XML
     */
    override fun queryLexUnitXML(connection: SQLiteDatabase, luId: Long): String {
        val doc = queryLexUnitDoc(connection, luId)
        return docToString(doc)
    }

    // sentence

    /**
     * Business method that returns sentence data as DOM document
     *
     * @param connection connection
     * @param sentenceId the sentence id to build query from
     * @return FrameNet sentence data as DOM document
     */
    fun querySentenceDoc(connection: SQLiteDatabase, sentenceId: Long): Document {
        val doc = makeDocument()
        val rootNode = makeFnRootSentenceNode(doc, sentenceId)
        walkSentence(connection, doc, rootNode, sentenceId)
        return doc
    }

    /**
     * Business method that returns sentence data as XML
     *
     * @param connection connection
     * @param sentenceId the sentence id to build query from
     * @return FrameNet sentence data as XML
     */
    fun querySentenceXML(connection: SQLiteDatabase, sentenceId: Long): String {
        val doc = querySentenceDoc(connection, sentenceId)
        return docToString(doc)
    }

    // annoSet

    /**
     * Business method that returns annoSet data as DOM document
     *
     * @param connection connection
     * @param annoSetId  the annoSetId to build query from
     * @return FrameNet annoSet data as DOM document
     */
    fun queryAnnoSetDoc(connection: SQLiteDatabase, annoSetId: Long): Document {
        val doc = makeDocument()
        val rootNode = makeFnRootAnnoSetNode(doc, annoSetId)
        walkAnnoSet(connection, doc, rootNode, annoSetId)
        return doc
    }

    /**
     * Business method that returns annoSet data as XML
     *
     * @param connection connection
     * @param annoSetId  the annoSetId to build query from
     * @return FrameNet annoSet data as XML
     */
    fun queryAnnoSetXML(connection: SQLiteDatabase, annoSetId: Long): String {
        val doc = queryAnnoSetDoc(connection, annoSetId)
        return docToString(doc)
    }

    // W A L K

    /**
     * Perform queries for FrameNet selection data
     *
     * @param connection connection
     * @param doc        the org.w3c.dom.Document being built
     * @param parent     the org.w3c.dom.Node the walk will attach results to
     * @param targetWord the target (fn) word
     */
    private fun walkSelector(connection: SQLiteDatabase, doc: Document, parent: Node, targetWord: String) {
        val result = if (queryFromFnWord) makeFromFnWord(connection, targetWord) else makeFromWord(connection, targetWord)
        val lexUnits = result.second ?: return

        // framenet nodes
        makeSelector(doc, parent, lexUnits, true)
    }

    /**
     * Perform queries for FrameNet data from word
     *
     * @param connection connection
     * @param doc        the org.w3c.dom.Document being built
     * @param parent     the org.w3c.dom.Node the walk will attach results to
     * @param targetWord the target (fn) word
     */
    private fun walk(connection: SQLiteDatabase, doc: Document, parent: Node, targetWord: String) {
        val result = if (queryFromFnWord) makeFromFnWord(connection, targetWord) else makeFromWord(connection, targetWord)
        val wordId = result.first
        val lexUnits = result.second ?: return

        // word
        makeWordNode(doc, parent, targetWord, wordId)

        // lexunits
        for (lexUnit in lexUnits) {
            // lexunit
            val lexUnitNode = makeFnLexunitNode(doc, parent, lexUnit!!)

            // frame
            val frame = lexUnit.frame!!
            makeFnFrameNode(doc, lexUnitNode, frame, false)
        }
    }

    /**
     * Perform queries for FrameNet data from (fn) word id
     *
     * @param connection   data source
     * @param doc          the org.w3c.dom.Document being built
     * @param parent       the org.w3c.dom.Node the walk will attach results to
     * @param targetWordId the target (fn) word id
     * @param pos          the target pos
     */
    private fun walkWord(connection: SQLiteDatabase, doc: Document, parent: Node, targetWordId: Long, pos: Char?) {
        // lexunits
        val lexUnits = if (queryFromFnWord) makeFromFnWordId(connection, targetWordId, pos) else makeFromWordId(connection, targetWordId, pos)
        if (lexUnits != null) {
            for (lexUnit in lexUnits) {
                // frame
                val lexUnitNode = makeFnLexunitNode(doc, parent, lexUnit!!)
                val frameNode = makeFnFrameNode(doc, lexUnitNode, lexUnit.frame!!, false)

                // frame FEs
                val fes = FnFrameElement.make(connection, lexUnit.frame.frameId)
                if (fes != null) {
                    for (fe in fes) {
                        makeFnFENode(doc, frameNode, fe!!)
                    }
                }

                // governors
                val governors = FnGovernor.make(connection, lexUnit.luId)
                if (governors != null) {
                    for (governor in governors) {
                        makeFnGovernorNode(doc, lexUnitNode, governor!!)
                    }
                }

                // sentences
                val sentencesNode = makeFnSentencesNode(doc, lexUnitNode)
                val sentences = makeFromLexicalUnit(connection, lexUnit.luId)
                if (sentences != null) {
                    var j = 1
                    for (sentence in sentences) {
                        makeFnSentenceNode(doc, sentencesNode, sentence!!, j)
                        j++
                    }
                }
            }
        }
    }

    companion object {

        const val FN_NS = "http://org.sqlunet/fn"

        /**
         * Perform queries for FrameNet data from frame id
         *
         * @param connection    data source
         * @param doc           the org.w3c.dom.Document being built
         * @param parent        the org.w3c.dom.Node the walk will attach results to
         * @param targetFrameId the target frame id
         */
        private fun walkFrame(connection: SQLiteDatabase, doc: Document, parent: Node, targetFrameId: Long) {
            // frame
            val frame = FnFrame.make(connection, targetFrameId) ?: return
            val frameNode = makeFnFrameNode(doc, parent, frame, false)

            // lexunits
            val lexUnits = makeFromFrame(connection, targetFrameId)
            if (lexUnits != null) {
                for (lexUnit in lexUnits) {
                    // includes frame info
                    makeFnLexunitNode(doc, frameNode, lexUnit!!)
                }
            }

            // frame FEs
            val fes = FnFrameElement.make(connection, frame.frameId)
            if (fes != null) {
                for (fe in fes) {
                    makeFnFENode(doc, frameNode, fe!!)
                }
            }
        }

        /**
         * Perform queries for FrameNet data from lexunit id
         *
         * @param connection data source
         * @param doc        the org.w3c.dom.Document being built
         * @param parent     the org.w3c.dom.Node the walk will attach results to
         * @param luId       the target lexunit id
         */
        private fun walkLexUnit(connection: SQLiteDatabase, doc: Document, parent: Node, luId: Long) {
            // lexunit
            val lexUnit = makeFromId(connection, luId)!!
            val lexUnitNode = makeFnLexunitNode(doc, parent, lexUnit)
            walkFrame(connection, doc, lexUnitNode, lexUnit.frame!!.frameId)

            // sentences
            val sentencesNode = makeFnSentencesNode(doc, lexUnitNode)
            val sentences = makeFromLexicalUnit(connection, lexUnit.luId)
            if (sentences != null) {
                var j = 1
                for (sentence in sentences) {
                    makeFnSentenceNode(doc, sentencesNode, sentence!!, j)
                    j++
                }
            }
        }

        /**
         * Perform queries for FrameNet data from sentence id
         *
         * @param connection data source
         * @param doc        the org.w3c.dom.Document being built
         * @param parent     the org.w3c.dom.Node the walk will attach results to
         * @param sentenceId the target sentence id
         */
        private fun walkSentence(connection: SQLiteDatabase, doc: Document, parent: Node, sentenceId: Long) {
            // sentence
            val sentence = make(connection, sentenceId)!!
            val sentenceNode = makeFnSentenceNode(doc, parent, sentence, 0)

            // layers
            walkLayersFromSentence(connection, doc, sentenceNode, sentenceId)
        }

        /**
         * Perform queries for FrameNet data from annoSet id
         *
         * @param connection data source
         * @param doc        the org.w3c.dom.Document being built
         * @param parent     the org.w3c.dom.Node the walk will attach results to
         * @param annoSetId  the target annoSet id
         */
        private fun walkAnnoSet(connection: SQLiteDatabase, doc: Document, parent: Node, annoSetId: Long) {
            // annoSet
            val annoSet = FnAnnoSet.make(connection, annoSetId)!!
            val sentenceNode = makeFnSentenceNode(doc, parent, annoSet.sentence, 0)

            // annoSet node
            val annoSetNode = makeFnAnnoSetNode(doc, sentenceNode, annoSet)

            // layers
            walkLayersFromAnnoSet(connection, doc, annoSetNode, annoSetId)
        }

        /**
         * Perform queries for FrameNet layers data from annoSet id
         *
         * @param connection data source
         * @param doc        the org.w3c.dom.Document being built
         * @param parent     the org.w3c.dom.Node the walk will attach results to
         * @param annoSetId  the target annoSet id
         */
        private fun walkLayersFromAnnoSet(connection: SQLiteDatabase, doc: Document, parent: Node, annoSetId: Long) {
            // layers
            val layers = makeFromAnnoSet(connection, annoSetId)
            if (layers != null) {
                for (layer in layers) {
                    // layer
                    makeFnLayerNode(doc, parent, layer!!)
                }
            }
        }

        /**
         * Perform queries for FrameNet layers data from sentence id
         *
         * @param connection data source
         * @param doc        the org.w3c.dom.Document being built
         * @param parent     the org.w3c.dom.Node the walk will attach results to
         * @param sentenceId the target sentence id
         */
        private fun walkLayersFromSentence(connection: SQLiteDatabase, doc: Document, parent: Node, sentenceId: Long) {
            // layers
            val layers = makeFromSentence(connection, sentenceId)
            if (layers != null) {
                var annoSetId: Long = -1
                var annoSetNode: Node? = null
                for (layer in layers) {
                    if (annoSetId != layer.annoSetId) {
                        annoSetNode = makeFnAnnoSetNode(doc, parent, layer.annoSetId)
                        annoSetId = layer.annoSetId
                    }

                    // layer
                    makeFnLayerNode(doc, annoSetNode, layer)
                }
            }
        }

        // H E L P E R S

        /**
         * Make selector
         *
         * @param doc      the org.w3c.dom.Document being built
         * @param parent   the org.w3c.dom.Document being built
         * @param lexUnits lexunits
         * @param doFrame  whether to include frame data
         */
        private fun makeSelector(doc: Document, parent: Node, lexUnits: Iterable<FnLexUnit?>, doFrame: Boolean) {
            // lexunits
            for (lexUnit in lexUnits) {
                // lexunit
                val lexUnitNode = makeFnLexunitNode(doc, parent, lexUnit!!)

                // frame
                if (doFrame) {
                    val frame = lexUnit.frame!!
                    makeFnFrameNode(doc, lexUnitNode, frame, true)
                }
            }
        }
    }
}
