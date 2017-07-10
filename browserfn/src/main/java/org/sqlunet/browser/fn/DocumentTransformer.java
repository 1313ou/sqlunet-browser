package org.sqlunet.browser.fn;

import java.io.InputStream;

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
	protected InputStream getXSLStream(final String source, final boolean isSelector)
	{
		String xsl = null;
		Settings.Source from = Settings.Source.valueOf(source);
		switch (from)
		{
			case FRAMENET:
				xsl = DocumentTransformer.XSL_DIR + (isSelector ? "framenet2html-select.xsl" : "framenet2html.xsl");
				break;
		}
		return DocumentTransformer.class.getResourceAsStream(xsl);
	}
}
