package org.sqlunet.sql;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOM node factory
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class NodeFactory
{
	private static final String NS_URL = "http://org.sqlunet";

	private static final String NS_XSD = "SqlUNet.xsd";

	/**
	 * Make versatile node
	 *
	 * @param document is the DOM Document being built
	 * @param parent   is the parent node to attach this node to
	 * @param name     is the node's tag name
	 * @param text     is the node's text content
	 * @return newly created node
	 */
	static public Element makeNode(final Document document, final Node parent, final String name, final String text)
	{
		final Element element = document.createElement(name);

		// attach
		if (parent != null)
		{
			parent.appendChild(element);
		}

		// text
		NodeFactory.makeText(document, element, text);

		return element;
	}

	/**
	 * Make top node
	 *
	 * @param document is the DOM Document being built
	 * @param parent   is the parent node to attach this node to
	 * @param name     is the node's tag name
	 * @param text     is the node's text content
	 * @return newly created node
	 */
	static public Element makeNode(final Document document, final Node parent, final String name, final String text, final String ns)
	{
		final Element node = makeNode(document, parent, name, text);
		node.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", ns);
		return node;
	}

	/**
	 * Make root node
	 *
	 * @param document is the DOM Document being built
	 * @param parent   is the parent node to attach this node to
	 * @param name     is the node's tag name
	 * @param text     is the node's text content
	 * @return newly created node
	 */
	static public Element makeRootNode(final Document document, final Node parent, final String name, final String text, final String ns)
	{
		final Element node = makeNode(document, parent, name, text, ns);
		node.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xs:schemaLocation", NS_URL + ' ' + NS_XSD);
		return node;
	}

	/**
	 * Build node attribute
	 *
	 * @param element is the element node to set attribute for
	 * @param name    is the attribute's name
	 * @param value   is the attribute's value
	 */
	protected static void makeAttribute(final Element element, final String name, final String value)
	{
		if (value != null && !value.isEmpty())
		{
			element.setAttribute(name, value);
		}
	}

	/**
	 * Add attributes to element
	 *
	 * @param element DOM element to attach attributes to	 * @param args     name-value pairs
	 */
	static public void addAttributes(final Element element, final String... args)
	{
		// attributes
		for (int i = 0; i < args.length; i += 2)
		{
			NodeFactory.makeAttribute(element, args[i], args[i + 1]);
		}
	}

	/**
	 * Make text node
	 *
	 * @param document is the DOM Document being built
	 * @param parent   is the parent node to attach this text to
	 * @param text     text
	 * @return parent
	 */
	protected static Element makeText(final Document document, final Element parent, final String text)
	{
		// text
		if (text != null && !text.isEmpty())
		{
			parent.appendChild(document.createTextNode(text));
		}
		return parent;
	}
}