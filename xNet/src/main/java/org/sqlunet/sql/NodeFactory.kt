/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.sql

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

/**
 * DOM node factory
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object NodeFactory {

    private const val NS_URL = "http://org.sqlunet"
    private const val NS_XSD = "SqlUNet.xsd"

    /**
     * Make versatile node
     *
     * @param document is the DOM Document being built
     * @param parent   is the parent node to attach this node to
     * @param name     is the node's tag name
     * @param text     is the node's text content
     * @return newly created node
     */
    fun makeNode(document: Document, parent: Node?, name: String?, text: String?): Element {
        val element = document.createElement(name)

        // attach
        parent?.appendChild(element)

        // text
        makeText(document, element, text)
        return element
    }

    /**
     * Make top node
     *
     * @param document is the DOM Document being built
     * @param parent   is the parent node to attach this node to
     * @param name     is the node's tag name
     * @param text     is the node's text content
     * @return newly created node
     */
    fun makeNode(document: Document, parent: Node?, name: String?, text: String?, ns: String?): Element {
        val node = makeNode(document, parent, name, text)
        node.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", ns)
        return node
    }

    /**
     * Make root node
     *
     * @param document is the DOM Document being built
     * @param parent   is the parent node to attach this node to
     * @param name     is the node's tag name
     * @param text     is the node's text content
     * @return newly created node
     */
    fun makeRootNode(document: Document, parent: Node?, name: String?, text: String?, ns: String?): Element {
        val node = makeNode(document, parent, name, text, ns)
        node.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xs:schemaLocation", "$NS_URL $NS_XSD")
        return node
    }

    /**
     * Build node attribute
     *
     * @param element is the element node to set attribute for
     * @param name    is the attribute's name
     * @param value   is the attribute's value
     */
    fun makeAttribute(element: Element, name: String?, value: String?) {
        if (!value.isNullOrEmpty()) {
            element.setAttribute(name, value)
        }
    }

    /**
     * Add attributes to element
     *
     * @param element DOM element to attach attributes to	 * @param args     name-value pairs
     */
    fun addAttributes(element: Element, vararg args: String) {
        // attributes
        var i = 0
        while (i < args.size) {
            makeAttribute(element, args[i], args[i + 1])
            i += 2
        }
    }

    /**
     * Make text node
     *
     * @param document is the DOM Document being built
     * @param parent   is the parent node to attach this text to
     * @param text     text
     * @return parent
     */
    fun makeText(document: Document, parent: Element, text: String?): Element {
        // text
        if (!text.isNullOrEmpty()) {
            parent.appendChild(document.createTextNode(text))
        }
        return parent
    }
}