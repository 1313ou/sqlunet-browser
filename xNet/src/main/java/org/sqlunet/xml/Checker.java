package org.sqlunet.xml;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("unused")
class Checker
{
	public static void check_nonElement(final Node node, final String... messg)
	{
		if (!node.getNodeValue().matches(" *")) //$NON-NLS-1$
			throw new RuntimeException("non element node " + node + "| " + (messg.length == 0 ?
					"" :
					messg[0])); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public static void check_ElementName(final String name, final String m, final String... messg)
	{
		if (!name.matches(m))
			throw new RuntimeException("element name |" + name + "| " + (messg.length == 0 ?
					"" :
					messg[0])); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public static void check_ElementAttributeName(final Element e, final String m, final String... messg)
	{
		final NamedNodeMap attrs = e.getAttributes();
		if (m != null)
		{
			if (attrs != null)
			{
				for (int i = 0; i < attrs.getLength(); i++)
				{
					final Node attr = attrs.item(i);
					final String name = attr.getNodeName();
					if (!name.matches(m))
						throw new RuntimeException("attribute name |" + name + "| " + (messg.length == 0 ?
								"" :
								messg[0])); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
		} else
		{
			if (attrs.getLength() != 0)
			{
				final StringBuilder sb = new StringBuilder();
				for (int i = 0; i < attrs.getLength(); i++)
				{
					final Node attr = attrs.item(i);
					sb.append(attr.getNodeName()).append(' ');
				}
				throw new RuntimeException("attribute name |" + sb + "| " + (messg.length == 0 ?
						"" :
						messg[0])); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
	}

	public static void check_ElementAttributeValue(final String value, final String m, final String... messg)
	{
		final String[] items = value.split("\\s+"); //$NON-NLS-1$
		for (final String item : items)
		{
			if (!item.matches(m))
				throw new RuntimeException("attr value |" + item + "| " + (messg.length == 0 ?
						"" :
						messg[0])); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	public static void check_TextValue(final Element e, final String... messg)
	{
		e.normalize();

		final NodeList nodes = e.getChildNodes();
		if (nodes.getLength() > 1)
			throw new RuntimeException("multiple nodes |" + e.getNodeName() + "| " + (messg.length == 0 ?
					"" :
					messg[0])); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		for (int j = 0; j < nodes.getLength(); j++)
		{
			final Node node = nodes.item(j);
			if (node instanceof Element)
				throw new RuntimeException("includes elements |" + e.getNodeName() + "| " + (messg.length == 0 ?
						"" :
						messg[0])); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
}
