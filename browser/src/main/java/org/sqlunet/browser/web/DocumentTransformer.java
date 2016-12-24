package org.sqlunet.browser.web;

import android.util.Log;

import org.sqlunet.settings.Settings;
import org.sqlunet.dom.DomTransformer;
import org.w3c.dom.Document;

import java.io.InputStream;

/**
 * XSL Transformer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class DocumentTransformer
{
	static private final String TAG = "DocumentTransformer";

	static private final String XSL_DIR = "/org/sqlunet/";

	/**
	 * Transform Document to HTML
	 *
	 * @param doc        doc
	 * @param isSelector is selector source
	 * @return html
	 */
	static public String docToHtml(final Document doc, final Settings.Source source, final boolean isSelector)
	{
		// Log.d(TAG, "to be transformed:" + DomTransformer.docToXml(doc));
		try
		{
			return DomTransformer.docToString(doc, DocumentTransformer.getXSLStream(source, isSelector), "html");
		}
		catch (final Exception e)
		{
			Log.e(TAG, "While transforming doc to HTML", e);
			return "error " + e;
		}
	}

	/**
	 * Get XSL file
	 *
	 * @param from       type of source
	 * @param isSelector is selector source
	 * @return XSL inputstream
	 */
	static private InputStream getXSLStream(final Settings.Source from, final boolean isSelector)
	{
		String xsl = null;
		switch (from)
		{
			case WORDNET:
				xsl = DocumentTransformer.XSL_DIR + (isSelector ? "wordnet2html-select.xsl" : "wordnet2html.xsl");
				break;
			case VERBNET:
				xsl = DocumentTransformer.XSL_DIR + (isSelector ? "verbnet2html-select.xsl" : "verbnet2html.xsl");
				break;
			case PROPBANK:
				xsl = DocumentTransformer.XSL_DIR + (isSelector ? "propbank2html-select.xsl" : "propbank2html.xsl");
				break;
			case FRAMENET:
				xsl = DocumentTransformer.XSL_DIR + (isSelector ? "framenet2html-select.xsl" : "framenet2html.xsl");
				break;
			case BNC:
				xsl = DocumentTransformer.XSL_DIR + (isSelector ? "bnc2html-select.xsl" : "bnc2html.xsl");
				break;
		}
		return DocumentTransformer.class.getResourceAsStream(xsl);
	}
}
