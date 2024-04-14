/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.dom

import android.util.Log
import mf.javax.xml.transform.Source
import mf.javax.xml.transform.stream.StreamSource
import mf.javax.xml.validation.SchemaFactory
import mf.javax.xml.validation.Validator
import mf.org.apache.xerces.jaxp.validation.XMLSchemaFactory
import org.w3c.dom.Document
import org.xml.sax.SAXException
import java.io.File
import java.io.IOException
import java.io.StringReader
import java.net.URL

object DomValidator {

    private const val TAG = "XMLValidator"

    /**
     * DomValidator
     *
     * @param validator validator
     * @param source    source to validate
     */
    private fun validate(validator: Validator, source: Source) {
        try {
            validator.validate(source)
            Log.i(TAG, "ok")
        } catch (e: SAXException) {
            Log.e(TAG, "sax", e)
        } catch (e: IOException) {
            Log.e(TAG, "io", e)
        }
    }

    /**
     * DomValidator strings
     *
     * @param xsdUrl  xsd url
     * @param strings files
     */
    fun validateStrings(xsdUrl: URL, vararg strings: String) {
        try {
            val validator = makeValidator(xsdUrl)
            for (string in strings) {
                validate(validator, StreamSource(StringReader(string)))
            }
        } catch (e: SAXException) {
            Log.e(TAG, "xsd", e)
        }
    }

    /**
     * DomValidator docs
     *
     * @param xsdUrl    xsd url
     * @param documents documents
     */
    fun validateDocs(xsdUrl: URL, vararg documents: Document?) {
        try {
            val validator = makeValidator(xsdUrl)
            for (document in documents) {
                if (document == null)
                    continue
                // cannot make org.w3c.dom.Document and mf.org.w3c.dom.Document compatible
                // DomValidator.validate(validator, new DOMSource(document))
                val string = DomTransformer.docToXml(document)
                validate(validator, StreamSource(StringReader(string)))
            }
        } catch (e: SAXException) {
            Log.e(TAG, "xsd", e)
        }
    }

    /**
     * DomValidator
     *
     * @param xsdUrl    xsd url
     * @param filePaths files
     */
    fun validateFiles(xsdUrl: URL, vararg filePaths: String) {
        try {
            val validator = makeValidator(xsdUrl)
            for (filePath in filePaths) {
                validate(validator, StreamSource(filePath))
            }
        } catch (e: SAXException) {
            Log.e(TAG, "xsd", e)
        }
    }

    /**
     * Make validator
     *
     * @param xsdUrl xsd url
     * @return validator
     * @throws SAXException exception
     */
    @Throws(SAXException::class)
    private fun makeValidator(xsdUrl: URL): Validator {
        //var schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        val schemaFactory: SchemaFactory = XMLSchemaFactory()
        val schema = schemaFactory.newSchema(xsdUrl)
        val validator = schema.newValidator()
        Log.i(TAG, "validator $xsdUrl")
        return validator
    }

    /**
     * Make validator
     *
     * @param xsdPath xsd file
     * @return xsdUrl
     */
    fun path2Url(xsdPath: String): URL {
        val xsdUrl: URL = try {
            DomValidator::class.java.getResource(xsdPath)!!
        } catch (e: Exception) {
            try {
                URL(xsdPath)
            } catch (e1: Exception) {
                try {
                    File(xsdPath).toURI().toURL()
                } catch (e2: Exception) {
                    throw RuntimeException("No validator XSD file")
                }
            }
        }
        return xsdUrl
    }
}
