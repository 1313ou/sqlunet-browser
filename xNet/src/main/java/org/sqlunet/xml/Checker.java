package org.sqlunet.xml;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Check XML well-formedness
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("unused")
class Checker
{
	/**
	 * Check non element
	 *
	 * @param node     node
	 * @param messages messages
	 */
	public static void checkNonElement(final Node node, final String... messages)
	{
		if (!node.getNodeValue().matches("\\s*")) //
		{
			throw new RuntimeException("non element node " + node + "| " + (messages.length == 0 ? "" : messages[0])); //
		}
	}

	/**
	 * Check element name
	 *
	 * @param name     name
	 * @param pattern  pattern to match
	 * @param messages messages
	 */
	public static void checkElementName(final String name, final String pattern, final String... messages)
	{
		if (!name.matches(pattern))
		{
			throw new RuntimeException("element name |" + name + "| " + (messages.length == 0 ? "" : messages[0])); //
		}
	}

	/**
	 * Check attribute name
	 *
	 * @param node     node
	 * @param pattern  pattern to match
	 * @param messages messages
	 */
	public static void checkElementAttributeName(final Node node, final String pattern, final String... messages)
	{
		final NamedNodeMap attrs = node.getAttributes();
		if (pattern != null)
		{
			if (attrs != null)
			{
				for (int i = 0; i < attrs.getLength(); i++)
				{
					final Node attr = attrs.item(i);
					final String name = attr.getNodeName();
					if (!name.matches(pattern))
					{
						throw new RuntimeException("attribute name |" + name + "| " + (messages.length == 0 ? "" : messages[0])); //
					}
				}
			}
		}
		else
		{
			if (attrs.getLength() != 0)
			{
				final StringBuilder sb = new StringBuilder();
				for (int i = 0; i < attrs.getLength(); i++)
				{
					final Node attr = attrs.item(i);
					sb.append(attr.getNodeName()).append(' ');
				}
				throw new RuntimeException("attribute name |" + sb + "| " + (messages.length == 0 ? "" : messages[0])); //
			}
		}
	}

	/**
	 * Check element attribute value
	 *
	 * @param value    attribute value
	 * @param pattern  pattern to match
	 * @param messages messages
	 */
	public static void checkElementAttributeValue(final String value, final String pattern, final String... messages)
	{
		final String[] items = value.split("\\s+"); //
		for (final String item : items)
		{
			if (!item.matches(pattern))
			{
				throw new RuntimeException("attr value |" + item + "| " + (messages.length == 0 ? "" : messages[0])); //
			}
		}
	}

	/**
	 * Check text value
	 *
	 * @param node     node
	 * @param messages messages
	 */
	public static void checkTextValue(final Node node, final String... messages)
	{
		node.normalize();

		final NodeList nodes = node.getChildNodes();
		if (nodes.getLength() > 1)
		{
			throw new RuntimeException("multiple nodes |" + node.getNodeName() + "| " + (messages.length == 0 ? "" : messages[0])); //
		}

		for (int j = 0; j < nodes.getLength(); j++)
		{
			final Node childNode = nodes.item(j);
			if (childNode instanceof Element)
			{
				throw new RuntimeException("includes elements |" + childNode.getNodeName() + "| " + (messages.length == 0 ? "" : messages[0])); //
			}
		}
	}
}
