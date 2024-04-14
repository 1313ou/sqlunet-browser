/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.sql

import org.sqlunet.sql.NodeFactory.addAttributes
import org.sqlunet.sql.NodeFactory.makeAttribute
import org.sqlunet.sql.NodeFactory.makeNode
import org.sqlunet.sql.NodeFactory.makeText
import org.w3c.dom.Document
import org.w3c.dom.Node

/**
 * DOM node factory
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal object PbNodeFactory {

    /**
     * Make PropBank root node
     *
     * @param doc    is the DOM Document being built
     * @param wordId target word id
     * @return newly created node
     */
    fun makePbRootNode(doc: Document, wordId: Long): Node {
        val rootNode = makeNode(doc, doc, "propbank", null, PropBankImplementation.PB_NS)
        addAttributes(rootNode, "wordid", wordId.toString())
        return rootNode
    }

    /**
     * Make PropBank root node
     *
     * @param doc  is the DOM Document being built
     * @param word target word
     * @return newly created node
     */
    fun makePbRootNode(doc: Document, word: String?): Node {
        val rootNode = makeNode(doc, doc, "propbank", null, PropBankImplementation.PB_NS)
        addAttributes(rootNode, "word", word!!)
        return rootNode
    }

    /**
     * Make PropBank root node
     *
     * @param doc       is the DOM Document being built
     * @param roleSetId target roleSet id
     * @return newly created node
     */
    fun makePbRootRoleSetNode(doc: Document, roleSetId: Long): Node {
        val rootNode = makeNode(doc, doc, "propbank", null, PropBankImplementation.PB_NS)
        addAttributes(rootNode, "rolesetid", roleSetId.toString())
        return rootNode
    }

    /**
     * Make the roleSet node
     *
     * @param doc     is the DOM Document being built
     * @param parent  is the parent node to attach this node to
     * @param roleSet is the roleSet information
     * @param i       the ith roleSet
     */
    fun makePbRoleSetNode(doc: Document, parent: Node?, roleSet: PbRoleSet, i: Int): Node {
        val element = makeNode(doc, parent, "roleset", null)
        makeAttribute(element, "num", i.toString())
        makeAttribute(element, "name", roleSet.roleSetName)
        makeAttribute(element, "rolesetid", roleSet.roleSetId.toString())
        makeAttribute(element, "head", roleSet.roleSetHead)
        if (roleSet.wordId != 0L) {
            makeAttribute(element, "wordid", roleSet.wordId.toString())
        }
        makeText(doc, element, roleSet.roleSetDescr)
        return element
    }

    /**
     * Make the roleSet node
     *
     * @param doc    is the DOM Document being built
     * @param parent is the parent node to attach this node to
     * @param role   is the role information
     */
    fun makePbRoleNode(doc: Document, parent: Node?, role: PbRole): Node {
        val element = makeNode(doc, parent, "role", null)
        makeAttribute(element, "roleid", role.roleId.toString())
        makeAttribute(element, "argtype", role.argType)
        makeAttribute(element, "theta", role.roleTheta)
        makeAttribute(element, "func", role.roleFunc)
        makeText(doc, element, role.roleDescr)
        return element
    }

    fun makePbExampleNode(doc: Document, parent: Node?, example: PbExample): Node {
        val element = makeNode(doc, parent, "example", null)
        makeAttribute(element, "exampleid", example.exampleId.toString())
        makeAttribute(element, "aspect", example.aspect)
        makeAttribute(element, "form", example.form)
        makeAttribute(element, "tense", example.tense)
        makeAttribute(element, "voice", example.voice)
        makeAttribute(element, "person", example.person)
        makeText(doc, element, example.text)
        makeNode(doc, element, "rel", example.rel)
        if (example.args != null) {
            for (arg in example.args) {
                val element3 = makeNode(doc, element, "arg", null)
                makeAttribute(element3, "argtype", arg.argType)
                if (arg.f != null) {
                    makeAttribute(element3, "f", arg.f)
                }
                makeAttribute(element3, "descr", arg.description)
                if (arg.vnTheta != null) {
                    makeAttribute(element3, "theta", arg.vnTheta)
                }
                makeText(doc, element3, arg.subText)
            }
        }
        return element
    }
}