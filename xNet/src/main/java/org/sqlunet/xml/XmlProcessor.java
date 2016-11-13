package org.sqlunet.xml;

import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Abstract XML processor
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract class XmlProcessor
{
	/**
	 * Builder factory
	 */
	static private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	/**
	 * Constructor
	 */
	XmlProcessor()
	{
		//
	}

	/**
	 * Process
	 *
	 * @param xml input XML
	 * @return processed XML
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public abstract String process(String xml) throws Exception;

	/**
	 * Convert string to doc
	 *
	 * @param xml XML string
	 * @return root element
	 * @throws Exception
	 */
	static Element docFromString(final String xml) throws Exception
	{
		final DocumentBuilder builder = XmlProcessor.factory.newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(xml.getBytes())).getDocumentElement();
	}
}
