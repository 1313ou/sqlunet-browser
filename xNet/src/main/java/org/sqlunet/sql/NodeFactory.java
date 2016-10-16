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
	/**
	 * Make versatile node
	 *
	 * @param document
	 *        is the DOM Document being built
	 * @param parent
	 *        is the parent node to attach this node to
	 * @param name
	 *        is the node's tag name
	 * @param text
	 *        is the node's text content
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
	 * Build node attribute
	 *
	 * @param element
	 *        is the element node to set attribute for
	 * @param name
	 *        is the attribute's name
	 * @param value
	 *        is the attribute's value
	 */
	protected static void makeAttribute(final Element element, final String name, final String value)
	{
		if (value != null && !value.isEmpty())
		{
			element.setAttribute(name, value);
		}
	}

	/**
	 * Make target node
	 *
	 * @param document
	 *        is the DOM Document being built
	 * @param parent
	 *        is the parent node to attach this node to
	 * @param args
	 *        name-value pairs
	 * @return newly created node
	 */
	@SuppressWarnings("UnusedReturnValue")
	static public Element makeTargetNode(final Document document, final Node parent, final String... args)
	{
		final Element element = document.createElement("target"); //$NON-NLS-1$

		// attach
		if (parent != null)
		{
			parent.appendChild(element);
		}

		// attributes
		for (int i = 0; i < args.length; i += 2)
		{
			NodeFactory.makeAttribute(element, args[i], args[i + 1]);
		}

		return element;
	}

	/**
	 * Make text node
	 *
	 * @param document
	 *        is the DOM Document being built
	 * @param parent
	 *        is the parent node to attach this text to
	 * @param text text
	 * @return parent
	 */
	@SuppressWarnings("UnusedReturnValue")
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