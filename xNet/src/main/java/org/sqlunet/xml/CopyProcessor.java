package org.sqlunet.xml;

/**
 * Copy processor that just copies XML input to output
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("unused")
public class CopyProcessor extends XmlProcessor
{
	@Override
	public String process(final String xml) throws Exception
	{
		return xml;
	}
}
