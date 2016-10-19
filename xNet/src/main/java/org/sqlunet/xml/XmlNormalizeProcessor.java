package org.sqlunet.xml;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 * Normalizing processor
 */
@SuppressWarnings("unused")
public class XmlNormalizeProcessor extends XmlProcessor
{
	private static final String TAG = "XmlNormalizeProcessor"; //$NON-NLS-1$

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
			Log.e(TAG, "While normalizing xml " + xml, ex);
		}
		return null;
	}

	/**
	 * Element to string
	 *
	 * @param e element
	 * @return string
	 */
	private static String elementToString(final Node e)
	{
		final Document document = e.getOwnerDocument();
		final DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
		final LSSerializer serializer = domImplLS.createLSSerializer();
		return serializer.writeToString(e);
	}
}
