/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.browser.fn;

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
		Settings.Source from = Settings.Source.valueOf(source);
		if (from == Settings.Source.FRAMENET)
		{
			final String xsl = DocumentTransformer.XSL_DIR + (isSelector ? "framenet2html-select.xsl" : "framenet2html.xsl");
			return DocumentTransformer.class.getResourceAsStream(xsl);
		}
		return null;
	}
}
