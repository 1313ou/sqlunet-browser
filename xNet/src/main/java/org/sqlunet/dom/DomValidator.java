package org.sqlunet.dom;

import android.util.Log;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import mf.javax.xml.transform.Source;
import mf.javax.xml.transform.TransformerException;
import mf.javax.xml.transform.stream.StreamSource;
import mf.javax.xml.validation.Schema;
import mf.javax.xml.validation.SchemaFactory;
import mf.javax.xml.validation.Validator;

public class DomValidator
{
	static final String TAG = "XMLValidator";

	/**
	 * DomValidator
	 *
	 * @param validator validator
	 * @param source    source to validate
	 * @throws SAXException         exception
	 * @throws IOException          exception
	 * @throws TransformerException exception
	 */
	private static void validate(final Validator validator, final Source source)
	{
		try
		{
			validator.validate(source);
			Log.i(TAG, "ok");
		}
		catch (final SAXException e)
		{
			Log.e(TAG, "sax", e);
		}
		catch (final IOException e)
		{
			Log.e(TAG, "io", e);
		}
	}

	/**
	 * DomValidator strings
	 *
	 * @param xsdUrl  xsd url
	 * @param strings files
	 * @throws SAXException exception
	 */
	public static void validateStrings(final URL xsdUrl, final String... strings)
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
		catch (final SAXException e)
		{
			Log.e(TAG, "xsd", e);
		}
	}

	/**
	 * DomValidator docs
	 *
	 * @param xsdUrl    xsd url
	 * @param documents documents
	 * @throws SAXException exception
	 */
	public static void validateDocs(final URL xsdUrl, final org.w3c.dom.Document... documents)
	{
		try
		{
			final Validator validator = DomValidator.makeValidator(xsdUrl);
			for (final org.w3c.dom.Document document : documents)
			{
				if(document!=null)
				{
					// cannot make org.w3c.dom.Documnet and mf.org.w3c.dom.Document compatible
					// DomValidator.validate(validator, new DOMSource(document));
					final String string = DomTransformer.docToXml(document);
					DomValidator.validate(validator, new StreamSource(new StringReader(string)));
				}
			}
		}
		catch (final SAXException e)
		{
			Log.e(TAG, "xsd", e);
		}
	}

	/**
	 * DomValidator
	 *
	 * @param xsdUrl    xsd url
	 * @param filePaths files
	 * @throws SAXException exception
	 */
	public static void validateFiles(final URL xsdUrl, final String... filePaths)
	{
		try
		{
			final Validator validator = DomValidator.makeValidator(xsdUrl);
			for (final String filePath : filePaths)
			{
				DomValidator.validate(validator, new StreamSource(filePath));
			}
		}
		catch (final SAXException e)
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
	private static Validator makeValidator(final URL xsdUrl) throws SAXException
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
	public static URL path2Url(final String xsdPath) throws SAXException
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
		catch (final Exception e)
		{
			try
			{
				xsdUrl = new URL(xsdPath);
			}
			catch (final Exception e1)
			{
				try
				{
					xsdUrl = new File(xsdPath).toURI().toURL();
				}
				catch (final Exception e2)
				{
					throw new RuntimeException("No validator XSD file");
				}
			}
		}
		return xsdUrl;
	}
}
