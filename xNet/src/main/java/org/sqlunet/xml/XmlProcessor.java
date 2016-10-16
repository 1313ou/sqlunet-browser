package org.sqlunet.xml;

import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

abstract class XmlProcessor
{
	XmlProcessor()
	{
		//
	}

	@SuppressWarnings("unused")
	public abstract String process(String xml) throws Exception;

	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	static Element docFromString(final String xml) throws Exception
	{
		final DocumentBuilder builder = XmlProcessor.factory.newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(xml.getBytes())).getDocumentElement();
	}
}
