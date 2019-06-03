/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.dom;

import android.util.Log;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import mf.javax.xml.transform.Source;
import mf.javax.xml.transform.stream.StreamSource;
import mf.javax.xml.validation.Schema;
import mf.javax.xml.validation.SchemaFactory;
import mf.javax.xml.validation.Validator;

public class DomValidator
{
	static private final String TAG = "XMLValidator";

	/**
	 * DomValidator
	 *
	 * @param validator validator
	 * @param source    source to validate
	 */
	static private void validate(@NonNull final Validator validator, final Source source)
	{
		try
		{
			validator.validate(source);
			Log.i(TAG, "ok");
		}
		catch (@NonNull final SAXException e)
		{
			Log.e(TAG, "sax", e);
		}
		catch (@NonNull final IOException e)
		{
			Log.e(TAG, "io", e);
		}
	}

	/**
	 * DomValidator strings
	 *
	 * @param xsdUrl  xsd url
	 * @param strings files
	 */
	static public void validateStrings(@NonNull final URL xsdUrl, @NonNull final String... strings)
	{
		try
		{
			final Validator validator = DomValidator.makeValidator(xsdUrl);
			for (final String string : strings)
			{
				if (string != null)
				{
					DomValidator.validate(validator, new StreamSource(new StringReader(string)));
				}
			}
		}
		catch (@NonNull final SAXException e)
		{
			Log.e(TAG, "xsd", e);
		}
	}

	/**
	 * DomValidator docs
	 *
	 * @param xsdUrl    xsd url
	 * @param documents documents
	 */
	static public void validateDocs(@NonNull final URL xsdUrl, @NonNull final org.w3c.dom.Document... documents)
	{
		try
		{
			final Validator validator = DomValidator.makeValidator(xsdUrl);
			for (final org.w3c.dom.Document document : documents)
			{
				if (document != null)
				{
					// cannot make org.w3c.dom.Document and mf.org.w3c.dom.Document compatible
					// DomValidator.validate(validator, new DOMSource(document));
					final String string = DomTransformer.docToXml(document);
					DomValidator.validate(validator, new StreamSource(new StringReader(string)));
				}
			}
		}
		catch (@NonNull final SAXException e)
		{
			Log.e(TAG, "xsd", e);
		}
	}

	/**
	 * DomValidator
	 *
	 * @param xsdUrl    xsd url
	 * @param filePaths files
	 */
	@SuppressWarnings("unused")
	static public void validateFiles(@NonNull final URL xsdUrl, @NonNull final String... filePaths)
	{
		try
		{
			final Validator validator = DomValidator.makeValidator(xsdUrl);
			for (final String filePath : filePaths)
			{
				DomValidator.validate(validator, new StreamSource(filePath));
			}
		}
		catch (@NonNull final SAXException e)
		{
			Log.e(TAG, "xsd", e);
		}
	}

	/**
	 * Make validator
	 *
	 * @param xsdUrl xsd url
	 * @return validator
	 * @throws SAXException exception
	 */
	static private Validator makeValidator(@NonNull final URL xsdUrl) throws SAXException
	{
		//final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final SchemaFactory schemaFactory = new mf.org.apache.xerces.jaxp.validation.XMLSchemaFactory();
		final Schema schema = schemaFactory.newSchema(xsdUrl);
		final Validator validator = schema.newValidator();
		Log.i(TAG, "validator " + xsdUrl.toString());
		return validator;
	}

	/**
	 * Make validator
	 *
	 * @param xsdPath xsd file
	 * @return xsdUrl
	 */
	@Nullable
	@SuppressWarnings("unused")
	static public URL path2Url(@NonNull final String xsdPath)
	{
		URL xsdUrl;
		try
		{
			xsdUrl = DomValidator.class.getResource(xsdPath);
			if (xsdUrl == null)
			{
				throw new RuntimeException("Null XSD resource file");
			}
		}
		catch (@NonNull final Exception e)
		{
			try
			{
				xsdUrl = new URL(xsdPath);
			}
			catch (@NonNull final Exception e1)
			{
				try
				{
					xsdUrl = new File(xsdPath).toURI().toURL();
				}
				catch (@NonNull final Exception e2)
				{
					throw new RuntimeException("No validator XSD file");
				}
			}
		}
		return xsdUrl;
	}
}
