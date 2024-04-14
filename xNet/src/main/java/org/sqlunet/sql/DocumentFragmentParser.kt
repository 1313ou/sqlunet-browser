/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.sql

import org.w3c.dom.Document
import org.w3c.dom.DocumentFragment
import org.w3c.dom.Node
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.IOException
import java.io.Reader
import java.io.StringReader
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

/**
 * <tt>DOMFragmentParser</tt> build a document fragment from an XML fragment source that has not necessary a unique root element, or that may have text content
 * around the root element(s).
 *
 *
 * The XML fragment mustn't contain DTD stuff : it must be a pure mix of XML tags and/or text. Entity resolution is thus irrelevant.
 *
 *
 *
 * The fragment may begin with a [text declaration](http://www.w3.org/TR/2000/REC-xml-20001006#sec-TextDecl).
 *
 *
 * @author Philippe Poulard
 */
class DocumentFragmentParser private constructor() {

    /**
     * Make document builder
     *
     * @return document builder
     */
    private fun makeDocumentBuilder(): DocumentBuilder? {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            factory.isCoalescing = true
            factory.isIgnoringComments = true
            factory.isNamespaceAware = false
            factory.isIgnoringElementContentWhitespace = true
            factory.isValidating = false
            return factory.newDocumentBuilder()
        } catch (e: ParserConfigurationException) {
            // do nothing
        }
        return null
    }

    /**
     * Parse an input source.
     *
     * @param text the input to parse.
     * @return document fragment
     * @throws SAXException sax exception
     * @throws IOException  io exception
     */
    @Throws(SAXException::class, IOException::class)
    private fun parse(text: String): DocumentFragment {
        val input = InputSource(StringReader("<dummy>$text</dummy>"))

        // parser
        val builder = makeDocumentBuilder()!!

        // parse
        val document = builder.parse(input)

        // fragment
        val fragment = document.createDocumentFragment()

        // here, the document element is the <dummy/> element.
        val children = document.documentElement.childNodes

        // move dummy's children over to the document fragment
        for (i in 0 until children.length) {
            fragment.appendChild(children.item(i))
        }
        return fragment
    }

    /**
     * Parse an input source.
     *
     * @param text the input to parse.
     * @return document fragment
     * @throws SAXException sax exception
     * @throws IOException  io exception
     */
    @Throws(SAXException::class, IOException::class)
    fun parseWithEntities(text: String): DocumentFragment {
        val input = InputSource(StringReader(text))

        // wrap the input with a root
        val reader: Reader = StringReader("<!DOCTYPE RooT [<!ENTITY in SYSTEM \"in\">]><RooT>&in;</RooT>")
        val inputSource = InputSource(reader)
        inputSource.publicId = input.publicId
        inputSource.systemId = input.systemId

        // parser
        val builder = makeDocumentBuilder()!!

        // the real input will be delivered by this entity resolver
        builder.setEntityResolver { _: String?, _: String? -> input }

        // parse
        val document = builder.parse(inputSource)

        // fragment
        val fragment = document.createDocumentFragment()

        // get the content of <Root>
        val root = document.documentElement
        while (root.hasChildNodes()) {
            fragment.appendChild(root.firstChild)
        }

        // clean the doc (the DTD, <RooT>)
        while (document.hasChildNodes()) {
            document.removeChild(document.firstChild)
        }
        return fragment
    }

    companion object {

        /**
         * Mount xml
         *
         * @param document is the DOM Document being built
         * @param element  is the DOM element to attach to
         * @param xml      definition
         */
        fun mount(document: Document, element: Node, xml: String, tag: String) {
            val parser = DocumentFragmentParser()
            try {
                val fragment = parser.parse("<$tag>$xml</$tag>")
                element.appendChild(document.importNode(fragment, true))
            } catch (e: SAXException) {
                element.appendChild(document.createTextNode("XML:" + xml + ":" + e.message))
            } catch (e: IOException) {
                // do nothing
            }
        }
    }
}
