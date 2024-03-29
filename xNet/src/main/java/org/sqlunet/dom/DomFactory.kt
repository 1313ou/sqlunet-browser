/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.dom

import org.w3c.dom.Document
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

/**
 * Document factory
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object DomFactory {

    /**
     * Build blank document
     *
     * @return empty org.w3c.dom.Document
     */
    @JvmStatic
    fun makeDocument(): Document {
        return try {
            val factory = DocumentBuilderFactory.newInstance()
            factory.isNamespaceAware = true
            val builder = factory.newDocumentBuilder()
            builder.newDocument()
        } catch (e: ParserConfigurationException) {
            throw RuntimeException(e)
        }
    }
}
