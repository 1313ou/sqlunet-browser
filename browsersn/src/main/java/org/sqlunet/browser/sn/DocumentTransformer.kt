/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.sn

import org.sqlunet.browser.web.DocumentTransformer
import java.io.InputStream

/**
 * XSL Transformer
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class DocumentTransformer : DocumentTransformer() {

    /**
     * Get XSL file
     *
     * @param from       type of source
     * @param isSelector is selector source
     * @return XSL inputstream
     */
    override fun getXSLStream(from: String, isSelector: Boolean): InputStream? {
        val source = SnSettings.Source.valueOf(from)
        val xsl: String = when (source) {
            SnSettings.Source.WORDNET -> XSL_DIR + (if (isSelector) "wordnet2html-select.xsl" else "wordnet2html.xsl")
            SnSettings.Source.SYNTAGNET -> XSL_DIR + (if (isSelector) "syntagnet2html-select.xsl" else "syntagnet2html.xsl")
            SnSettings.Source.BNC -> XSL_DIR + (if (isSelector) "bnc2html-select.xsl" else "bnc2html.xsl")
        }
        return DocumentTransformer::class.java.getResourceAsStream(xsl)
    }

    companion object {

        private const val XSL_DIR = "/org/sqlunet/"
    }
}
