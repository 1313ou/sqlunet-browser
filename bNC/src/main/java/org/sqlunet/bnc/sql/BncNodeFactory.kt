/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.bnc.sql

import org.sqlunet.sql.NodeFactory
import org.w3c.dom.Document
import org.w3c.dom.Node

/**
 * DOM node factory
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal object BncNodeFactory {

    /**
     * Make BNC root node
     *
     * @param doc    is the DOM Document being built
     * @param wordId target word id
     * @param pos    target pos
     * @return newly created node
     */
    @JvmStatic
    fun makeBncRootNode(doc: Document, wordId: Long, pos: Char?): Node {
        val rootNode = NodeFactory.makeNode(doc, doc, "bnc", null, BncImplementation.BNC_NS)
        if (pos == null) {
            NodeFactory.addAttributes(rootNode, "wordid", wordId.toString())
        } else {
            NodeFactory.addAttributes(rootNode, "wordid", wordId.toString(), "pos", pos.toString())
        }
        return rootNode
    }

    /**
     * Make the BNC data node
     *
     * @param doc    is the DOM Document being built
     * @param parent is the parent node to attach this node to
     * @param data   is the BNC data
     * @param i      the ith BNC data
     */
    @JvmStatic
    fun makeBncNode(doc: Document, parent: Node?, data: BncData, i: Int): Node {
        val element = NodeFactory.makeNode(doc, parent, "bncdata", null)
        NodeFactory.makeAttribute(element, "ith", i.toString())
        NodeFactory.makeAttribute(element, "pos", if (data.posName != null) data.posName else data.pos)
        makeDataNode(doc, element, "freq", data.freq)
        makeDataNode(doc, element, "range", data.range)
        makeDataNode(doc, element, "disp", data.disp)
        makeDataNode(doc, element, "convfreq", data.convFreq)
        makeDataNode(doc, element, "convrange", data.convRange)
        makeDataNode(doc, element, "convdisp", data.convDisp)
        makeDataNode(doc, element, "taskfreq", data.taskFreq)
        makeDataNode(doc, element, "taskrange", data.taskRange)
        makeDataNode(doc, element, "taskdisp", data.taskDisp)
        makeDataNode(doc, element, "imagfreq", data.imagFreq)
        makeDataNode(doc, element, "imagrange", data.imagRange)
        makeDataNode(doc, element, "imagdisp", data.imagDisp)
        makeDataNode(doc, element, "inffreq", data.infFreq)
        makeDataNode(doc, element, "infrange", data.infRange)
        makeDataNode(doc, element, "infdisp", data.infDisp)
        makeDataNode(doc, element, "spokenfreq", data.spokenFreq)
        makeDataNode(doc, element, "spokenrange", data.spokenRange)
        makeDataNode(doc, element, "spokendisp", data.spokenDisp)
        makeDataNode(doc, element, "writtenfreq", data.writtenFreq)
        makeDataNode(doc, element, "writtenrange", data.writtenRange)
        makeDataNode(doc, element, "writtendisp", data.writtenDisp)
        return element
    }

    private fun makeDataNode(doc: Document, parent: Node, name: String, `object`: Any?) {
        if (`object` == null) {
            return
        }
        NodeFactory.makeNode(doc, parent, name, `object`.toString())
    }
}