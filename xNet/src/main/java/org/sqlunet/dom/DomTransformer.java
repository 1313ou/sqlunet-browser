/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.dom;

import android.util.Log;

import org.w3c.dom.Document;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * XSL transformer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DomTransformer
{
	static private final String TAG = "XSLTransformer";

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
			return DomTransformer.docToString(doc, null, "xml");
		}
		catch (@NonNull final Exception e)
		{
			Log.e(TAG, "While transforming doc to XML", e);
			return "error " + e;
		}
	}


	// O U T P U T

	/**
	 * Transform Document to XML (no transformation)
	 *
	 * @param document org.w3.dom.Document to convert to XML form
	 * @return XML string for Document
	 */
	static public String docToString(final Document document)
	{
		try
		{
			final Source source = new DOMSource(document);

			// output stream
			final StringWriter writer = new StringWriter();
			final Result result = new StreamResult(writer);

			// use a Transformer for output
			final TransformerFactory factory = TransformerFactory.newInstance();
			final Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.transform(source, result);
			return writer.toString();
		}
		catch (@NonNull final TransformerException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Transform Document to XML (with transformation)
	 *
	 * @param doc       is the DOM Document to be output as XML
	 * @param xslStream is the XSL stream
	 * @return XML String that represents DOM document
	 * @throws TransformerException transformer exception
	 */
	static public String docToString(final Document doc, @Nullable final InputStream xslStream, final String method) throws TransformerException
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
