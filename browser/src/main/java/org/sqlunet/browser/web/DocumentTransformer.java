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

	static private final String XSLDIR = "/org/sqlunet/";

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
				xsl = DocumentTransformer.XSLDIR + (isSelector ? "select_wordnet2html.xsl" : "wordnet2html.xsl");
				break;
			case VERBNET:
				xsl = DocumentTransformer.XSLDIR + (isSelector ? "select_verbnet2html.xsl" : "verbnet2html.xsl");
				break;
			case PROPBANK:
				xsl = DocumentTransformer.XSLDIR + (isSelector ? "select_propbank2html.xsl" : "propbank2html.xsl");
				break;
			case FRAMENET:
				xsl = DocumentTransformer.XSLDIR + (isSelector ? "select_framenet2html.xsl" : "framenet2html.xsl");
				break;
			case BNC:
				xsl = DocumentTransformer.XSLDIR + (isSelector ? "select_bnc2html.xsl" : "bnc2html.xsl");
				break;
		}
		return DocumentTransformer.class.getResourceAsStream(xsl);
	}
}
