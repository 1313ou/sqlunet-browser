/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import org.sqlunet.sql.DocumentFragmentParser.Companion.mount
import org.sqlunet.sql.NodeFactory.addAttributes
import org.sqlunet.sql.NodeFactory.makeAttribute
import org.sqlunet.sql.NodeFactory.makeNode
import org.sqlunet.sql.NodeFactory.makeText
import org.sqlunet.sql.Utils.join
import org.w3c.dom.Document
import org.w3c.dom.Node

/**
 * DOM node factory
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal object FnNodeFactory {

    /**
     * Make root node
     *
     * @param doc    DOM Document being built
     * @param wordId target word id
     * @param pos    target pos
     * @return newly created node
     */
    fun makeFnRootNode(doc: Document, wordId: Long, pos: Char?): Node {
        val rootNode = makeNode(doc, doc, "framenet", null, FrameNetImplementation.FN_NS)
        if (pos == null) {
            addAttributes(rootNode, "wordid", wordId.toString())
        } else {
            addAttributes(rootNode, "wordid", wordId.toString(), "pos", pos.toString())
        }
        return rootNode
    }

    /**
     * Make root node
     *
     * @param doc  DOM Document being built
     * @param word target word
     * @param pos  target pos
     * @return newly created node
     */
    fun makeFnRootNode(doc: Document, word: String?, pos: Char?): Node {
        val rootNode = makeNode(doc, doc, "framenet", null, FrameNetImplementation.FN_NS)
        if (pos == null) {
            addAttributes(rootNode, "word", word!!)
        } else {
            addAttributes(rootNode, "word", word!!, "pos", pos.toString())
        }
        return rootNode
    }

    /**
     * Make lex unit node
     *
     * @param doc     DOM Document being built
     * @param parent  parent node to attach this node to
     * @param lexUnit frame information
     */
    fun makeFnLexunitNode(doc: Document, parent: Node?, lexUnit: FnLexUnit): Node {
        val element = makeNode(doc, parent, "lexunit", null)
        makeAttribute(element, "name", lexUnit.lexUnit)
        makeAttribute(element, "luid", lexUnit.luId.toString())
        makeText(doc, element, lexUnit.definition)
        return element
    }

    /**
     * Make frame node
     *
     * @param doc      DOM Document being built
     * @param parent   parent node to attach this node to
     * @param frame    frame
     * @param removeEx whether to remove <ex> element
     * @return newly created node
    </ex> */
    fun makeFnFrameNode(doc: Document, parent: Node?, frame: FnFrame, removeEx: Boolean): Node {
        val element = makeNode(doc, parent, "frame", null)
        makeAttribute(element, "name", frame.frameName)
        makeAttribute(element, "frameid", frame.frameId.toString())
        mount(doc, element, frame.frameDefinition, "framedefinition")
        if (removeEx) {
            // retrieve the elements 'ex'
            val examples = element.getElementsByTagName("ex")
            for (i in 0 until examples.length) {
                val example = examples.item(i)
                example.parentNode.removeChild(example)
            }
            element.normalize()
        }
        if (!frame.semTypes.isNullOrEmpty()) {
            for (semType in frame.semTypes) {
                val element2 = makeNode(doc, element, "semtype", null)
                makeAttribute(element2, "semtypeid", semType.semTypeId.toString())
                makeAttribute(element2, "semtype", semType.semTypeName)
                makeText(doc, element, semType.semTypeDefinition)
            }
        }
        if (!frame.relatedFrames.isNullOrEmpty()) {
            for (relatedFrame in frame.relatedFrames) {
                val element2 = makeNode(doc, element, "related", null)
                makeAttribute(element2, "frameid", relatedFrame.frameId.toString())
                makeAttribute(element2, "frame", relatedFrame.frameName)
                makeAttribute(element2, "relation", relatedFrame.relation)
            }
        }
        return element
    }

    /**
     * Make FE node
     *
     * @param doc    DOM Document being built
     * @param parent parent node to attach this node to
     * @param fe     FE
     * @return newly created node
     */
    fun makeFnFENode(doc: Document, parent: Node?, fe: FnFrameElement): Node {
        val element = makeNode(doc, parent, "fe", null)
        makeAttribute(element, "name", fe.feType)
        makeAttribute(element, "feid", fe.feId.toString())
        makeAttribute(element, "coreset", fe.coreSet.toString())
        makeAttribute(element, "type", fe.coreType)
        if (fe.semTypes != null) makeAttribute(element, "semtype", join(*fe.semTypes))
        mount(doc, element, fe.feDefinition, "fedefinition")
        return element
    }

    /**
     * Make governor node
     *
     * @param doc      DOM Document being built
     * @param parent   parent node to attach this node to
     * @param governor governor
     * @return newly created node
     */
    fun makeFnGovernorNode(doc: Document, parent: Node?, governor: FnGovernor): Node {
        val element = makeNode(doc, parent, "governor", null)
        makeAttribute(element, "governorid", governor.governorId.toString())
        makeAttribute(element, "wordid", governor.wordId.toString())
        makeText(doc, element, governor.governor)
        return element
    }

    /**
     * Make sentences node
     *
     * @param doc    DOM Document being built
     * @param parent parent node to attach this node to
     * @return newly created node
     */
    fun makeFnSentencesNode(doc: Document, parent: Node?): Node {
        return makeNode(doc, parent, "sentences", null)
    }

    /**
     * Make sentence node
     *
     * @param doc    DOM Document being built
     * @param parent parent node to attach this node to
     * @param i      the ith
     */
    fun makeFnSentenceNode(doc: Document, parent: Node, sentence: FnSentence, i: Int): Node {
        return makeFnSentenceNode(doc, parent, sentence.text, sentence.sentenceId, i)
    }

    /**
     * Make sentence node
     *
     * @param doc    DOM Document being built
     * @param parent parent node to attach this node to
     * @param i      the ith
     */
    private fun makeFnSentenceNode(doc: Document, parent: Node, text: String, sentenceId: Long, i: Int): Node {
        val element = makeNode(doc, parent, "sentence", null)
        if (i != 0) {
            makeAttribute(element, "num", i.toString())
        }
        makeAttribute(element, "sentenceid", sentenceId.toString())
        makeText(doc, element, text)
        return element
    }

    /**
     * Make annoSet node
     *
     * @param doc       DOM Document being built
     * @param parent    parent node to attach this node to
     * @param annoSetId annoSetId
     * @return annoSet node
     */
    fun makeFnAnnoSetNode(doc: Document, parent: Node?, annoSetId: Long): Node {
        val element = makeNode(doc, parent, "annoset", null)
        makeAttribute(element, "annosetid", annoSetId.toString())
        return element
    }

    /**
     * Make annoSet node
     *
     * @param doc     DOM Document being built
     * @param parent  parent node to attach this node to
     * @param annoSet annoSet
     * @return annoSet node
     */
    fun makeFnAnnoSetNode(doc: Document, parent: Node?, annoSet: FnAnnoSet): Node {
        return makeFnAnnoSetNode(doc, parent, annoSet.annoSetId)
    }

    /**
     * Make layer node
     *
     * @param doc   DOM Document being built
     * @param layer target layer
     * @return layer node
     */
    fun makeFnLayerNode(doc: Document, parent: Node?, layer: FnLayer): Node {
        val element = makeNode(doc, parent, "layer", null)
        makeAttribute(element, "rank", layer.rank.toString())
        makeAttribute(element, "layerid", layer.layerId.toString())
        makeAttribute(element, "type", layer.layerType)
        if (layer.labels != null) {
            for (label in layer.labels) {
                val element2 = makeNode(doc, element, "label", null)
                if ("0" != label.from || "0" != label.to) {
                    makeAttribute(element2, "from", label.from)
                    makeAttribute(element2, "to", label.to)
                }
                makeAttribute(element2, "label", label.label)
                if (!label.iType.isNullOrEmpty()) {
                    makeAttribute(element2, "itype", label.iType)
                }
            }
        }
        return element
    }

    /**
     * Make root frame node
     *
     * @param doc     DOM Document being built
     * @param frameId target frame id
     * @return root frame node
     */
    fun makeFnRootFrameNode(doc: Document, frameId: Long): Node {
        val rootNode = makeNode(doc, doc, "framenet", null, FrameNetImplementation.FN_NS)
        addAttributes(rootNode, "frameid", frameId.toString())
        return rootNode
    }

    /**
     * Make root lexunit node
     *
     * @param doc  DOM Document being built
     * @param luId target luId
     * @return root lexunit node
     */
    fun makeFnRootLexUnitNode(doc: Document, luId: Long): Node {
        val rootNode = makeNode(doc, doc, "framenet", null, FrameNetImplementation.FN_NS)
        addAttributes(rootNode, "luid", luId.toString())
        return rootNode
    }

    /**
     * Make root sentence node
     *
     * @param doc        DOM Document being built
     * @param sentenceId target sentence id
     * @return root sentence node
     */
    fun makeFnRootSentenceNode(doc: Document, sentenceId: Long): Node {
        val rootNode = makeNode(doc, doc, "framenet", null, FrameNetImplementation.FN_NS)
        addAttributes(rootNode, "sentenceid", sentenceId.toString())
        return rootNode
    }

    /**
     * Make root annoSet node
     *
     * @param doc       DOM Document being built
     * @param annoSetId target annoSetId
     * @return root annoSet node
     */
    fun makeFnRootAnnoSetNode(doc: Document, annoSetId: Long): Node {
        val rootNode = makeNode(doc, doc, "framenet", null, FrameNetImplementation.FN_NS)
        addAttributes(rootNode, "annosetid", annoSetId.toString())
        return rootNode
    }
}