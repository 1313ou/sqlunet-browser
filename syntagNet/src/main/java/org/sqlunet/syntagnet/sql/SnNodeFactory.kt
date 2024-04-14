/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.sql

import org.sqlunet.sql.NodeFactory.addAttributes
import org.sqlunet.sql.NodeFactory.makeAttribute
import org.sqlunet.sql.NodeFactory.makeNode
import org.sqlunet.syntagnet.sql.Collocation.WithDefinitionAndPos
import org.w3c.dom.Document
import org.w3c.dom.Node

/**
 * DOM node factory
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal object SnNodeFactory {

    /**
     * Make SyntagNet root node
     *
     * @param doc    is the DOM Document being built
     * @param wordId target word id
     * @return newly created node
     */
    fun makeSnRootNode(doc: Document, wordId: Long): Node {
        val rootNode = makeNode(doc, doc, "syntagnet", null, SyntagNetImplementation.SN_NS)
        addAttributes(rootNode, "wordid", wordId.toString())
        return rootNode
    }

    /**
     * Make SyntagNet root node
     *
     * @param doc  is the DOM Document being built
     * @param word target word
     * @return newly created node
     */
    fun makeSnRootNode(doc: Document, word: String?): Node {
        val rootNode = makeNode(doc, doc, "syntagnet", null, SyntagNetImplementation.SN_NS)
        addAttributes(rootNode, "word", word!!)
        return rootNode
    }

    /**
     * Make SyntagNet root node
     *
     * @param doc           is the DOM Document being built
     * @param collocationId target collocation id
     * @return newly created node
     */
    fun makeCollocationNode(doc: Document, collocationId: Long): Node {
        val rootNode = makeNode(doc, doc, "syntagnet", null, SyntagNetImplementation.SN_NS)
        addAttributes(rootNode, "syntagnetid", collocationId.toString())
        return rootNode
    }

    /**
     * Make the collocation node
     *
     * @param doc         is the DOM Document being built
     * @param parent      is the parent node to attach this node to
     * @param collocation is the collocation information
     * @param i           the ith collocation
     */
    fun makeCollocationNode(doc: Document, parent: Node?, collocation: WithDefinitionAndPos, i: Int): Node {
        val collocationElement = makeNode(doc, parent, "collocation", null)
        makeAttribute(collocationElement, "ith", i.toString())
        makeAttribute(collocationElement, "collocationid", collocation.collocationId.toString())
        makeAttribute(collocationElement, "word1id", collocation.word1Id.toString())
        makeAttribute(collocationElement, "word2id", collocation.word2Id.toString())
        makeAttribute(collocationElement, "synset1id", collocation.synset1Id.toString())
        makeAttribute(collocationElement, "synset2id", collocation.synset2Id.toString())
        val word1Element = makeNode(doc, collocationElement, "word", collocation.word1)
        makeAttribute(word1Element, "wordid", collocation.word1Id.toString())
        makeAttribute(word1Element, "which", "1")
        val word2Element = makeNode(doc, collocationElement, "word", collocation.word2)
        makeAttribute(word2Element, "wordid", collocation.word2Id.toString())
        makeAttribute(word2Element, "which", "2")
        val synset1Element = makeNode(doc, collocationElement, "synset", collocation.definition1)
        makeAttribute(synset1Element, "synsetid", collocation.synset1Id.toString())
        makeAttribute(synset1Element, "pos", collocation.pos1.toString())
        makeAttribute(synset1Element, "which", "1")
        val synset2Element = makeNode(doc, collocationElement, "synset", collocation.definition2)
        makeAttribute(synset2Element, "synsetid", collocation.synset2Id.toString())
        makeAttribute(synset1Element, "pos", collocation.pos2.toString())
        makeAttribute(synset2Element, "which", "2")
        return collocationElement
    }

    /**
     * Make the collocation node
     *
     * @param doc         is the DOM Document being built
     * @param parent      is the parent node to attach this node to
     * @param collocation is the collocation information
     * @param i           the ith collocation
     */
    fun makeSelectorCollocationNode(doc: Document, parent: Node?, collocation: Collocation, i: Int): Node {
        val collocationElement = makeNode(doc, parent, "collocation", null)
        makeAttribute(collocationElement, "ith", i.toString())
        makeAttribute(collocationElement, "collocationid", collocation.collocationId.toString())
        makeAttribute(collocationElement, "word1id", collocation.word1Id.toString())
        makeAttribute(collocationElement, "word2id", collocation.word2Id.toString())
        makeAttribute(collocationElement, "synset1id", collocation.synset1Id.toString())
        makeAttribute(collocationElement, "synset2id", collocation.synset2Id.toString())
        val word1Element = makeNode(doc, collocationElement, "word", collocation.word1)
        makeAttribute(word1Element, "wordid", collocation.word1Id.toString())
        makeAttribute(word1Element, "which", "1")
        val word2Element = makeNode(doc, collocationElement, "word", collocation.word2)
        makeAttribute(word2Element, "wordid", collocation.word2Id.toString())
        makeAttribute(word2Element, "which", "2")
        return collocationElement
    }
}