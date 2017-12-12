package org.sqlunet.browser.vn;

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
			case VERBNET:
				xsl = DocumentTransformer.XSL_DIR + (isSelector ? "verbnet2html-select.xsl" : "verbnet2html.xsl");
				break;
			case PROPBANK:
				xsl = DocumentTransformer.XSL_DIR + (isSelector ? "propbank2html-select.xsl" : "propbank2html.xsl");
				break;
			case WORDNET:
				xsl = DocumentTransformer.XSL_DIR + (isSelector ? "wordnet2html-select.xsl" : "wordnet2html.xsl");
				break;
		}
		return DocumentTransformer.class.getResourceAsStream(xsl);
	}
}
