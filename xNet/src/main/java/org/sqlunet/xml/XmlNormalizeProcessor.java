package org.sqlunet.xml;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 * Normalizing processor
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("unused")
public class XmlNormalizeProcessor extends XmlProcessor
{
	static private final String TAG = "XmlNormalizeProcessor";
	@Override
	public String process(final String xml) throws Exception
	{
		final Element e = XmlProcessor.docFromString("<root>" + xml + "</root>"); ////
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
	 * @param node element
	 * @return string
	 */
	static private String elementToString(final Node node)
	{
		final Document document = node.getOwnerDocument();
		final DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
		final LSSerializer serializer = domImplLS.createLSSerializer();
		return serializer.writeToString(node);
	}
}
