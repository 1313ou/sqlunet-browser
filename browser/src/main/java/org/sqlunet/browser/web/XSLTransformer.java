package org.sqlunet.browser.web;

import android.util.Log;

import org.sqlunet.settings.Settings;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * XSL Transformer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class XSLTransformer
{
	private static final String TAG = "XSLTransformer";
	/**
	 * Transform Document to HTML
	 *
	 * @param doc        doc
	 * @param isSelector is selector source
	 * @return html
	 */
	static public String docToHtml(final Document doc, final Settings.Source source, final boolean isSelector)
	{
		try
		{
			return XSLTransformer.docToString(doc, XSLTransformer.getXSLStream(source, isSelector), "html");
		}
		catch (final Exception e)
		{
			Log.e(TAG, "While transforming doc to HTML", e);
			return "error " + e;
		}
	}

	/**
	 * Transform Document to XML
	 *
	 * @param doc doc
	 * @return xml
	 */
	static public String docToXml(final Document doc)
	{
		try
		{
			return XSLTransformer.docToString(doc, null, "xml");
		}
		catch (final Exception e)
		{
			Log.e(TAG, "While transforming doc to XML", e);
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
				xsl = "/org/sqlunet/wordnet/dom/xsl/" + (isSelector ? "select_wordnet2html.xsl" : "wordnet2html.xsl");
				break;
			case VERBNET:
				xsl = "/org/sqlunet/verbnet/dom/xsl/" + (isSelector ? "select_verbnet2html.xsl" : "verbnet2html.xsl");
				break;
			case PROPBANK:
				xsl = "/org/sqlunet/propbank/dom/xsl/" + (isSelector ? "select_propbank2html.xsl" : "propbank2html.xsl");
				break;
			case FRAMENET:
				xsl = "/org/sqlunet/framenet/dom/xsl/" + (isSelector ? "select_framenet2html.xsl" : "framenet2html.xsl");
				break;
			case BNC:
				xsl = "/org/sqlunet/bnc/dom/xsl/" + (isSelector ? "select_bnc2html.xsl" : "bnc2html.xsl");
				break;
		}
		return XSLTransformer.class.getResourceAsStream(xsl);
	}

	/**
	 * Transform Document to XML
	 *
	 * @param doc       is the DOM Document to be output as XML
	 * @param xslStream is the XSL stream
	 * @return XML String that represents DOM document
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	static private String docToString(final Document doc, final InputStream xslStream, final String method) throws TransformerException
	{
		final Source source = new DOMSource(doc);

		// output stream
		final StringWriter outStream = new StringWriter();
		final Result resultStream = new StreamResult(outStream);

		// style
		StreamSource styleSource = null;
		if (xslStream != null)
		{
			xslStream.mark(10000);
			styleSource = new StreamSource(xslStream);
		}

		// transform
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer = styleSource == null ? transformerFactory.newTransformer() : transformerFactory.newTransformer(styleSource);
		transformer.setOutputProperty(javax.xml.transform.OutputKeys.METHOD, method);
		transformer.transform(source, resultStream);

		return outStream.toString();
	}
}
