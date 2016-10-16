package org.sqlunet.browser.web;

import org.sqlunet.settings.Settings;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.io.StringWriter;

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
 * @author Bernard Bou
 */
class XSLTransformer
{
	/**
	 * Transform Document to HTML
	 *
	 * @param thisDoc    doc
	 * @param isSelector is selector source
	 * @return html
	 */
	static public String docToHtml(final Document thisDoc, final Settings.Source source, final boolean isSelector)
	{
		try
		{
			return XSLTransformer.docToString(thisDoc, XSLTransformer.getXSLStream(source, isSelector), "html"); //$NON-NLS-1$
		} catch (final Exception e)
		{
			e.printStackTrace();
			return "error " + e; //$NON-NLS-1$
		}
	}

	/**
	 * Transform Document to XML
	 *
	 * @param thisDoc doc
	 * @return xml
	 */
	static public String docToXml(final Document thisDoc)
	{
		try
		{
			return XSLTransformer.docToString(thisDoc, null, "xml"); //$NON-NLS-1$
		} catch (final Exception e)
		{
			e.printStackTrace();
			return "error " + e; //$NON-NLS-1$
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
				xsl = "/org/sqlunet/wordnet/dom/xsl/" + (isSelector ?
						"select_wordnet2html.xsl" :
						"wordnet2html.xsl"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				break;
			case VERBNET:
				xsl = "/org/sqlunet/verbnet/dom/xsl/" + (isSelector ?
						"select_verbnet2html.xsl" :
						"verbnet2html.xsl"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				break;
			case PROPBANK:
				xsl = "/org/sqlunet/propbank/dom/xsl/" + (isSelector ?
						"select_propbank2html.xsl" :
						"propbank2html.xsl"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				break;
			case FRAMENET:
				xsl = "/org/sqlunet/framenet/dom/xsl/" + (isSelector ?
						"select_framenet2html.xsl" :
						"framenet2html.xsl"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				break;
			case BNC:
				xsl = "/org/sqlunet/bnc/dom/xsl/" + (isSelector ?
						"select_bnc2html.xsl" :
						"bnc2html.xsl"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				break;
			default:
				break;
		}
		return XSLTransformer.class.getResourceAsStream(xsl);
	}

	/**
	 * Transform Document to XML
	 *
	 * @param thisDoc       is the DOM Document to be output as XML
	 * @param thisXSLStream is the XSL stream
	 * @return XML String that represents DOM document
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	static private String docToString(final Document thisDoc, final InputStream thisXSLStream, final String method) throws TransformerException
	{
		final DOMSource thisSource = new DOMSource(thisDoc);

		// output stream
		final StringWriter thisOutStream = new StringWriter();
		final StreamResult thisResultStream = new StreamResult(thisOutStream);

		// style
		StreamSource thisStyleSource = null;
		if (thisXSLStream != null)
		{
			thisXSLStream.mark(10000);
			thisStyleSource = new StreamSource(thisXSLStream);
		}

		// transform
		final TransformerFactory thisTransformerFactory = TransformerFactory.newInstance();
		final Transformer thisTransformer = thisStyleSource == null ?
				thisTransformerFactory.newTransformer() :
				thisTransformerFactory.newTransformer(thisStyleSource);
		thisTransformer.setOutputProperty(javax.xml.transform.OutputKeys.METHOD, method);
		thisTransformer.transform(thisSource, thisResultStream);

		return thisOutStream.toString();
	}
}
