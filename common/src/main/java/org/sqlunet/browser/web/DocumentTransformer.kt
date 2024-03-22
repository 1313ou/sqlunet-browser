/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.web

import android.util.Log
import org.sqlunet.dom.DomTransformer.docToString
import org.w3c.dom.Document
import java.io.InputStream

/**
 * XSL Transformer
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class DocumentTransformer {

    /**
     * Transform Document to HTML
     *
     * @param doc        doc
     * @param isSelector is selector source
     * @return html
     */
    fun docToHtml(doc: Document, source: String, isSelector: Boolean): String {
        // Log.d(TAG, "to be transformed:" + DomTransformer.docToXml(doc));
        return try {
            docToString(doc, getXSLStream(source, isSelector), "html")
        } catch (e: Exception) {
            Log.e(TAG, "While transforming doc to HTML", e)
            "error $e"
        }
    }

    /**
     * Get XSL file
     *
     * @param from       type of source
     * @param isSelector is selector source
     * @return XSL inputstream
     */
    protected abstract fun getXSLStream(from: String, isSelector: Boolean): InputStream?

    companion object {
        private const val TAG = "DocumentTransformer"
    }
}
