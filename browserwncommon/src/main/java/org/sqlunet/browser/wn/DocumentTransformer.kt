/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.wn

import java.io.InputStream

/**
 * XSL Transformer
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class DocumentTransformer : org.sqlunet.browser.web.DocumentTransformer() {

    /**
     * Get XSL file
     *
     * @param from     type of source
     * @param isSelector is selector source
     * @return XSL inputstream
     */
    override fun getXSLStream(from: String, isSelector: Boolean): InputStream? {
        val source = WnSettings.Source.valueOf(from)
        val xsl: String = when (source) {
            WnSettings.Source.WORDNET -> XSL_DIR + (if (isSelector) "wordnet2html-select.xsl" else "wordnet2html.xsl")
            WnSettings.Source.BNC -> XSL_DIR + (if (isSelector) "bnc2html-select.xsl" else "bnc2html.xsl")
        }
        return DocumentTransformer::class.java.getResourceAsStream(xsl)
    }

    companion object {
        private const val XSL_DIR = "/org/sqlunet/"
    }
}
