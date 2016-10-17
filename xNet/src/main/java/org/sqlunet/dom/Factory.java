package org.sqlunet.dom;

import org.w3c.dom.Document;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Document factory
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Factory
{
	// D O C U M E N T

	/**
	 * Build blank document
	 *
	 * @return empty org.w3c.dom.Document
	 */
	static public Document makeDocument()
	{
		try
		{
			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			final DocumentBuilder db = dbf.newDocumentBuilder();
			return db.newDocument();
		}
		catch (final ParserConfigurationException e)
		{
			throw new RuntimeException(e);
		}
	}

	// O U T P U T

	/**
	 * Transform Document to XML form
	 *
	 * @param document the org.w3.dom.Document to convert to XML form
	 * @param dtd      the name of the DTD
	 * @return XML string for Document
	 */
	static public String docToString(final Document document, final String dtd)
	{
		try
		{
			final Source source = new DOMSource(document);

			// output stream
			final StringWriter os = new StringWriter();
			final Result result = new StreamResult(os);

			// use a Transformer for output
			final TransformerFactory tf = TransformerFactory.newInstance();
			final Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.DOCTYPE_SYSTEM, dtd);
			transformer.transform(source, result);
			return os.toString();
		}
		catch (final TransformerException e)
		{
			throw new RuntimeException(e);
		}
	}
}
