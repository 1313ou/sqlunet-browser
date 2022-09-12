/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.dom;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import androidx.annotation.NonNull;

/**
 * Document factory
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DomFactory
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
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			final DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.newDocument();
		}
		catch (@NonNull final ParserConfigurationException e)
		{
			throw new RuntimeException(e);
		}
	}
}
