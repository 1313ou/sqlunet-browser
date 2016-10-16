package org.sqlunet.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

@SuppressWarnings("unused")
public class XmlNormalizeProcessor extends XmlProcessor
{
	@Override
	public String process(final String xml) throws Exception
	{
		final Element e = XmlProcessor.docFromString("<root>" + xml + "</root>"); //$NON-NLS-1$//$NON-NLS-2$
		try
		{
			return XmlNormalizeProcessor.elementToString(e);
		}
		catch (final Exception ex)
		{
			System.err.println(xml);
			ex.printStackTrace();
		}
		return null;
	}

	private static String elementToString(final Element e)
	{
		final Document document = e.getOwnerDocument();
		final DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
		final LSSerializer serializer = domImplLS.createLSSerializer();
		return serializer.writeToString(e);
	}
}
