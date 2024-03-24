/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

import org.sqlunet.sql.NodeFactory.addAttributes
import org.sqlunet.sql.NodeFactory.makeAttribute
import org.sqlunet.sql.NodeFactory.makeNode
import org.sqlunet.wordnet.sql.WnNodeFactory.makeSynsetNode
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.util.regex.Pattern

/**
 * VerbNet DOM node factory
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal object VnNodeFactory {

    /**
     * Make VerbNet root node
     *
     * @param doc     is the DOM Document being built
     * @param classId target class id
     * @return newly created node
     */
    @JvmStatic
    fun makeVnRootClassNode(doc: Document, classId: Long): Node {
        val rootNode = makeNode(doc, doc, "verbnet", null, VerbNetImplementation.VN_NS)
        addAttributes(rootNode, "classid", classId.toString())
        return rootNode
    }

    /**
     * Make VerbNet root node
     *
     * @param doc      is the DOM Document being built
     * @param wordId   target word id
     * @param synsetId target synset id (0 for all)
     * @return newly created node
     */
    @JvmStatic
    fun makeVnRootNode(doc: Document, wordId: Long, synsetId: Long?): Node {
        val rootNode = makeNode(doc, doc, "verbnet", null, VerbNetImplementation.VN_NS)
        if (synsetId == null || synsetId == 0L) {
            addAttributes(rootNode, "wordid", wordId.toString())
        } else {
            addAttributes(rootNode, "wordid", wordId.toString(), "synsetid", synsetId.toString())
        }
        return rootNode
    }

    /**
     * Make VerbNet root node
     *
     * @param doc  is the DOM Document being built
     * @param word target word
     * @return newly created node
     */
    @JvmStatic
    fun makeVnRootNode(doc: Document, word: String?): Node {
        val rootNode = makeNode(doc, doc, "verbnet", null, VerbNetImplementation.VN_NS)
        addAttributes(rootNode, "word", word!!)
        return rootNode
    }

    /**
     * Make class node
     *
     * @param doc     is the DOM Document being built
     * @param parent  is the parent node to attach this node to
     * @param vnClass is the class
     * @return newly created node
     */
    @JvmStatic
    fun makeVnClassNode(doc: Document, parent: Node?, vnClass: VnClass): Node {
        val element = makeNode(doc, parent, "vnclass", null)
        makeAttribute(element, "name", vnClass.className)
        return element
    }

    /**
     * Make the class-with-sense node
     *
     * @param doc     is the DOM Document being built
     * @param parent  is the parent node to attach this node to
     * @param vnClass is the vn class with sense
     */
    @JvmStatic
    fun makeVnClassWithSenseNode(doc: Document, parent: Node?, vnClass: VnClassWithSense): Node {
        val element = makeNode(doc, parent, "vnclass", null)
        addAttributes(
            element,  //
            "name", vnClass.className,  //
            "classid", vnClass.classId.toString(),  //
            "wordid", vnClass.wordId.toString(),  //
            "synsetid", vnClass.synsetId.toString(),  //
            "sensenum", vnClass.senseNum.toString(),  //
            "sensekey", vnClass.senseKey,  //
            "groupings", vnClass.groupings,  //
            "quality", vnClass.quality.toString()
        )
        return element
    }

    /**
     * Make FnRole roles node
     *
     * @param doc    is the DOM Document being built
     * @param parent is the parent node to attach this node to
     * @return newly created node
     */
    @JvmStatic
    fun makeVnRolesNode(doc: Document, parent: Node?): Node {
        return makeNode(doc, parent, "themroles", null)
    }

    /**
     * Make FnRole role node
     *
     * @param doc    is the DOM Document being built
     * @param parent is the parent node to attach this node to
     * @param role   is the role
     * @param i      is the indexOf
     * @return newly created node
     */
    @JvmStatic
    fun makeVnRoleNode(doc: Document, parent: Node?, role: VnRole, i: Int): Node {
        val element = makeNode(doc, parent, "themrole", null)
        makeAttribute(element, "type", role.roleType)
        makeAttribute(element, "id", i.toString())
        val restrsElement = makeNode(doc, element, "restrs", null)
        restrsElement.setAttribute("value", role.selectionRestrictions)
        // restrsElement.setTextContent(role.selectionRestrictions)
        return element
    }

    /**
     * Make FnRole frames node
     *
     * @param doc    is the DOM Document being built
     * @param parent is the parent node to attach this node to
     * @return newly created node
     */
    @JvmStatic
    fun makeVnFramesNode(doc: Document, parent: Node?): Node {
        return makeNode(doc, parent, "frames", null)
    }

    /**
     * Make FnFrameElement frame node
     *
     * @param doc    is the DOM Document being built
     * @param parent is the parent node to attach this node to
     * @param frame  is the frame
     * @param i      is the rank
     * @return newly created node
     */
    @JvmStatic
    fun makeVnFrameNode(doc: Document, parent: Node?, frame: VnFrame, i: Int): Node {
        val element = makeNode(doc, parent, "frame", null)
        makeAttribute(element, "id", i.toString())
        makeAttribute(element, "description", frame.description1 + " - " + frame.description2)
        val descriptionElement = makeNode(doc, element, "description", null)
        makeAttribute(descriptionElement, "descriptionNumber", frame.number)
        makeAttribute(descriptionElement, "xtag", frame.xTag)
        makeAttribute(descriptionElement, "primary", frame.description1)
        makeAttribute(descriptionElement, "secondary", frame.description2)
        // val syntaxElement = makeNode(doc, element, "syntax", null)
        // syntaxElement.appendChild(doc.createTextNode(frame.getSyntax()))
        // val semanticsElement = makeNode(doc, element, "semantics", null)
        // semanticsElement.appendChild(doc.createTextNode(frame.getSemantics()))
        val syntaxElement = makeNode(doc, element, "syntax", null)
        val syntaxConcat = frame.syntax
        val syntaxItems = syntaxConcat.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (syntaxItem in syntaxItems) {
            val syntaxItemElement = makeNode(doc, syntaxElement, "synitem", null)
            makeVnSyntaxNodes(doc, syntaxItemElement, syntaxItem)
            // syntaxItemElement.setTextContent(syntaxItem)
        }
        val semanticsElement = makeNode(doc, element, "semantics", null)
        val semanticsConcat = frame.semantics
        val semanticsItems = semanticsConcat.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (semanticItem in semanticsItems) {
            val semanticItemElement = makeNode(doc, semanticsElement, "semitem", null)
            makeVnSemanticNodes(doc, semanticItemElement, semanticItem)
            // semanticItemElement.setTextContent(semanticItem)
        }
        val examplesElement = makeNode(doc, element, "examples", null)
        for (example in frame.examples) {
            val exampleElement = makeNode(doc, examplesElement, "example", null)
            exampleElement.appendChild(doc.createTextNode(example))
        }
        return element
    }

    /**
     * Parse text into segments
     *
     * @param text    input text
     * @param pattern delimiter pattern
     * @return segments
     */
    private fun parse(text: CharSequence, pattern: Pattern): Array<String?>? {
        // general pattern
        val matcher = pattern.matcher(text)
        if (matcher.find()) {
            val n = matcher.groupCount()
            val fields = arrayOfNulls<String>(n)
            for (i in 1..n) {
                // val group = matcher.group(i)
                // val start = matcher.start(i)
                // val end = matcher.end(i)
                fields[i - 1] = matcher.group(i)
            }
            return fields
        }
        return null
    }

    /**
     * Syntax pattern
     */
    private val syntaxPattern = Pattern.compile("^(\\S+) ?(\\p{Upper}[\\p{Lower}_\\p{Upper}]*)? ?(.+)?")

    /**
     * Make syntax nodes
     *
     * @param doc       doc
     * @param parent    parent node
     * @param statement statement
     * @return node
     */
    private fun makeVnSyntaxNodes(doc: Document, parent: Node, statement: CharSequence): Node {
        val fields = parse(statement, syntaxPattern)
        if (fields != null && fields.size == 3) {
            val categoryElement = makeNode(doc, parent, "cat", null)
            categoryElement.setAttribute("value", fields[0])
            // categoryElement.setTextContent(fields[0]);
            if (fields[1] != null) {
                val valueElement = makeNode(doc, parent, "value", null)
                valueElement.setAttribute("value", fields[1])
                // valueElement.setTextContent(fields[1]);
            }
            if (fields[2] != null) {
                val restrsElement = makeNode(doc, parent, "restrs", null)
                restrsElement.setAttribute("value", fields[2])
                // restrsElement.setTextContent(fields[2]);
            }
        }
        return parent
    }

    /**
     * Semantics pattern
     */
    private val semanticsPattern = Pattern.compile("([^(]+)\\((.*)\\)")

    /**
     * Arguments pattern
     */
    private const val ARGS_PATTERN = "[\\s,]+"

    /**
     * Make semantics nodes
     *
     * @param doc       doc
     * @param parent    parent node
     * @param statement statement
     * @return node
     */
    private fun makeVnSemanticNodes(doc: Document, parent: Node, statement: CharSequence): Node {
        val relArgs = parse(statement, semanticsPattern)
        if (relArgs != null && relArgs.size == 2) {
            val relElement = makeNode(doc, parent, "rel", null)
            relElement.setAttribute("value", relArgs[0])
            val args = relArgs[1]!!.split(ARGS_PATTERN.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var i = 1
            for (arg in args) {
                val argElement = makeNode(doc, parent, "arg", null)
                argElement.setAttribute("value", arg)
                argElement.setAttribute("argtype", i++.toString())
                // argElement.setTextContent(arg);
            }
        }
        return parent
    }

    /**
     * Make synset node with a flag
     *
     * @param doc    is the DOM Document being built
     * @param parent is the parent node to attach this node to
     * @param size   is the synset's size (the number of words in the synset)
     * @param id     is the synset's id in the database
     * @param flag   is the synset's flag
     * @return newly created node
     */
    fun makeSynsetNodeFlagged(doc: Document, parent: Node?, size: Int, id: Long, flag: Boolean): Node {
        val element = makeSynsetNode(doc, parent, id, size)
        if (flag) {
            makeAttribute(element, "flagged", "true")
        }
        return element
    }
}