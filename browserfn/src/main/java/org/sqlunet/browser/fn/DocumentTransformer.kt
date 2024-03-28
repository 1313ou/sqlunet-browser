/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.fn

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
     * @param from       type of source
     * @param isSelector is selector source
     * @return XSL inputstream
     */
    override fun getXSLStream(from: String, isSelector: Boolean): InputStream? {
        val source = FnSettings.Source.valueOf(from)
        if (source == FnSettings.Source.FRAMENET) {
            val xsl = XSL_DIR + if (isSelector) "framenet2html-select.xsl" else "framenet2html.xsl"
            return DocumentTransformer::class.java.getResourceAsStream(xsl)
        }
        return null
    }

    companion object {
        private const val XSL_DIR = "/org/sqlunet/"
    }
}
