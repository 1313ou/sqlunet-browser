package org.sqlunet.sql;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * <tt>DOMFragmentParser</tt> build a document fragment from an XML fragment source that has not necessary a unique root element, or that may have text content
 * around the root element(s).
 * <p>
 * The XML fragment mustn't contain DTD stuff : it must be a pure mix of XML tags and/or text. Entity resolution is thus irrelevant.
 * </p>
 * <p>
 * The fragment may begin with a <a href="http://www.w3.org/TR/2000/REC-xml-20001006#sec-TextDecl">text declaration</a>.
 * </p>
 *
 * @author Philippe Poulard
 */
public class DocumentFragmentParser
{
	/**
	 * Create a parser that parses XML fragments.
	 */
	private DocumentFragmentParser()
	{
		// do nothing
	}

	/**
	 * Make document builder
	 *
	 * @return document builder
	 */
	private DocumentBuilder makeDocumentBuilder()
	{
		try
		{
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setCoalescing(true);
			factory.setIgnoringComments(true);
			factory.setNamespaceAware(false);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			return factory.newDocumentBuilder();
		}
		catch (final ParserConfigurationException e)
		{
			// do nothing
		}
		return null;
	}

	/**
	 * Parse an input source.
	 *
	 * @param text the input to parse.
	 * @return document fragment
	 * @throws SAXException
	 * @throws IOException
	 */
	private DocumentFragment parse(final String text) throws SAXException, IOException
	{
		final InputSource input = new InputSource(new StringReader("<dummy>" + text + "</dummy>")); //

		// parser
		final DocumentBuilder builder = makeDocumentBuilder();
		assert builder != null;

		// parse
		final Document document = builder.parse(input);
		// try
		// {
		// String xml = docToString(document);
		// Log.d(TAG, xml);
		// }
		// catch (Exception e)
		// {
		// e.printStackTrace();
		// }

		// fragment
		final DocumentFragment fragment = document.createDocumentFragment();

		// here, the document element is the <dummy/> element.
		final NodeList children = document.getDocumentElement().getChildNodes();

		// move dummy's children over to the document fragment
		for (int i = 0; i < children.getLength(); i++)
		{
			fragment.appendChild(children.item(i));
		}
		return fragment;
	}

	/**
	 * Parse an input source.
	 *
	 * @param text the input to parse.
	 * @return document fragment
	 * @throws SAXException
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	public DocumentFragment parse_with_entities(final String text) throws SAXException, IOException
	{
		final InputSource input = new InputSource(new StringReader(text));

		// wrap the input with a root
		final Reader reader = new StringReader("<!DOCTYPE RooT [<!ENTITY in SYSTEM \"in\">]><RooT>&in;</RooT>"); //
		final InputSource inputSource = new InputSource(reader);
		inputSource.setPublicId(input.getPublicId());
		inputSource.setSystemId(input.getSystemId());

		// parser
		final DocumentBuilder builder = makeDocumentBuilder();
		assert builder != null;

		// the real input will be delivered by this entity resolver
		builder.setEntityResolver(new EntityResolver()
		{
			@Override
			public InputSource resolveEntity(final String publicId, final String systemId)
			{
				return input;
			}
		});

		// parse
		final Document document = builder.parse(inputSource);
		// try
		// {
		// String xml = docToString(document);
		// Log.d(TAG, xml);
		// }
		// catch (Exception e)
		// {
		// e.printStackTrace();
		// }

		// fragment
		final DocumentFragment fragment = document.createDocumentFragment();

		// get the content of <Root>
		final Element root = document.getDocumentElement();
		while (root.hasChildNodes())
		{
			fragment.appendChild(root.getFirstChild());
		}

		// clean the doc (the DTD, <RooT>)
		while (document.hasChildNodes())
		{
			document.removeChild(document.getFirstChild());
		}
		return fragment;
	}

	/**
	 * Mount xml
	 *
	 * @param document is the DOM Document being built
	 * @param element  is the DOM element to attach to
	 * @param xml      definition
	 */
	static public void mount(final Document document, final Node element, final String xml, final String tag)
	{
		final DocumentFragmentParser parser = new DocumentFragmentParser();
		try
		{
			final DocumentFragment fragment = parser.parse('<' + tag + '>' + xml + "</" + tag + '>'); //
			element.appendChild(document.importNode(fragment, true));
		}
		catch (final SAXException e)
		{
			element.appendChild(document.createTextNode("XML:" + xml + ":" + e.getMessage())); //
		}
		catch (final IOException e)
		{
			// do nothing
		}
	}
}
