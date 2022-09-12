/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.browser.sn;

import java.io.InputStream;

import androidx.annotation.Nullable;

/**
 * XSL Transformer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DocumentTransformer extends org.sqlunet.browser.web.DocumentTransformer
{
	static private final String XSL_DIR = "/org/sqlunet/";

	/**
	 * Get XSL file
	 *
	 * @param source     type of source
	 * @param isSelector is selector source
	 * @return XSL inputstream
	 */
	@Nullable
	protected InputStream getXSLStream(final String source, final boolean isSelector)
	{
		String xsl = null;
		Settings.Source from = Settings.Source.valueOf(source);
		switch (from)
		{
			case WORDNET:
				xsl = DocumentTransformer.XSL_DIR + (isSelector ? "wordnet2html-select.xsl" : "wordnet2html.xsl");
				break;
			case SYNTAGNET:
				xsl = DocumentTransformer.XSL_DIR + (isSelector ? "syntagnet2html-select.xsl" : "syntagnet2html.xsl");
				break;
			case BNC:
				xsl = DocumentTransformer.XSL_DIR + (isSelector ? "bnc2html-select.xsl" : "bnc2html.xsl");
				break;
		}
		return org.sqlunet.browser.web.DocumentTransformer.class.getResourceAsStream(xsl);
	}
}
