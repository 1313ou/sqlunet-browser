/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

import org.sqlunet.sql.NodeFactory
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

/**
 * DOM node factory
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object WnNodeFactory {

    /**
     * Make part-of-speech node
     *
     * @param doc    is the DOM Document being built
     * @param parent is the parent node to attach this node to
     * @param value  is the part-of-speech
     * @return newly created node
     */
    fun makePosNode(doc: Document, parent: Node?, value: String?): Node {
        val element = NodeFactory.makeNode(doc, parent, "pos", null)
        NodeFactory.makeAttribute(element, "name", value)
        return element
    }

    /**
     * Make domain node
     *
     * @param doc    is the DOM Document being built
     * @param parent is the parent node to attach this node to
     * @param value  domain
     * @return newly created node
     */
    fun makeDomainNode(doc: Document, parent: Node?, value: String?): Node {
        val element = NodeFactory.makeNode(doc, parent, "domain", null)
        NodeFactory.makeAttribute(element, "name", value)
        return element
    }

    /**
     * Make sense node
     *
     * @param doc      is the DOM Document being built
     * @param parent   is the parent node to attach this node to
     * @param wordId   is the word id
     * @param synsetId is the synset id
     * @return newly created node
     */
    fun makeSenseNode(doc: Document, parent: Node?, wordId: Long, synsetId: Long, senseIdx: Int): Element {
        val element = NodeFactory.makeNode(doc, parent, "sense", null)
        if (wordId != 0L) {
            NodeFactory.makeAttribute(element, "wordid", wordId.toString())
        }
        if (synsetId != 0L) {
            NodeFactory.makeAttribute(element, "synsetid", synsetId.toString())
        }
        if (senseIdx != 0) {
            NodeFactory.makeAttribute(element, "number", senseIdx.toString())
        }
        return element
    }

    /**
     * Make synset node
     *
     * @param doc      is the DOM Document being built
     * @param parent   is the parent node to attach this node to
     * @param synsetId is the synset's id in the database
     * @param size     is the synset's size (the number of words in the synset)
     * @return newly created element
     */
    fun makeSynsetNode(doc: Document, parent: Node?, synsetId: Long, size: Int): Element {
        val element = NodeFactory.makeNode(doc, parent, "synset", null)
        NodeFactory.makeAttribute(element, "synsetid", synsetId.toString())
        if (size != 0) {
            NodeFactory.makeAttribute(element, "size", size.toString())
        }
        return element
    }

    /**
     * Make word (synset item) node
     *
     * @param doc    is the DOM Document being built
     * @param parent is the parent node to attach this node to
     * @param word   target word
     * @param id     is the word id
     * @return newly created node
     */
    fun makeWordNode(doc: Document, parent: Node?, word: String?, id: Long): Node {
        val element = NodeFactory.makeNode(doc, parent, "word", word)
        NodeFactory.makeAttribute(element, "wordid", id.toString())
        return element
    }

    /**
     * Make link node
     *
     * @param doc          is the DOM Document being built
     * @param parent       is the parent node to attach this node to
     * @param relationType is the relation type
     * @param level        is the recursion level
     * @return newly created node
     */
    fun makeRelationNode(doc: Document, parent: Node?, relationType: String?, level: Int): Node {
        val element = NodeFactory.makeNode(doc, parent, relationType, null)
        if (level > 0) {
            NodeFactory.makeAttribute(element, "level", level.toString())
        }
        return element
    }

    /**
     * Make 'more' relation node (when recursiveness is broken and result is truncated)
     *
     * @param doc          is the DOM Document being built
     * @param parent       is the parent node to attach this node to
     * @param relationType is the relation type
     * @param level        is the recursion level
     * @return newly created node
     */
    fun makeMoreRelationNode(doc: Document, parent: Node?, relationType: String?, level: Int): Node {
        val element = NodeFactory.makeNode(doc, parent, relationType, null)
        NodeFactory.makeAttribute(element, "level", level.toString())
        NodeFactory.makeAttribute(element, "more", "true")
        return element
    }
}