/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.dom

import android.util.Log
import org.w3c.dom.Document
import java.io.InputStream
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.Result
import javax.xml.transform.Source
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * XSL transformer
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object DomTransformer {

    private const val TAG = "XSLTransformer"

    /**
     * Transform Document to XML
     *
     * @param doc doc
     * @return xml
     */
    @JvmStatic
    fun docToXml(doc: Document?): String {
        return try {
            docToString(doc, null, "xml")
        } catch (e: Exception) {
            Log.e(TAG, "While transforming doc to XML", e)
            "error $e"
        }
    }

    // O U T P U T

    /**
     * Transform Document to XML (no transformation)
     *
     * @param document org.w3.dom.Document to convert to XML form
     * @return XML string for Document
     */
    @JvmStatic
    fun docToString(document: Document?): String {
        return try {
            val source: Source = DOMSource(document)

            // output stream
            val writer = StringWriter()
            val result: Result = StreamResult(writer)

            // use a Transformer for output
            val factory = TransformerFactory.newInstance()
            val transformer = factory.newTransformer()
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
            transformer.transform(source, result)
            writer.toString()
        } catch (e: TransformerException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Transform Document to XML (with transformation)
     *
     * @param doc       is the DOM Document to be output as XML
     * @param xslStream is the XSL stream
     * @return XML String that represents DOM document
     * @throws TransformerException transformer exception
     */
    @JvmStatic
    @Throws(TransformerException::class)
    fun docToString(doc: Document?, xslStream: InputStream?, method: String?): String {
        val source: Source = DOMSource(doc)

        // output stream
        val outStream = StringWriter()
        val resultStream: Result = StreamResult(outStream)

        // style
        var styleSource: StreamSource? = null
        if (xslStream != null) {
            xslStream.mark(10000)
            styleSource = StreamSource(xslStream)
        }

        // transform
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = if (styleSource == null) transformerFactory.newTransformer() else transformerFactory.newTransformer(styleSource)
        transformer.setOutputProperty(OutputKeys.METHOD, method)
        transformer.transform(source, resultStream)
        return outStream.toString()
    }
}
