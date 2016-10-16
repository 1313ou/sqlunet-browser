package org.sqlunet.bnc.sql;

import org.sqlunet.sql.NodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOM node factory
 *
 * @author Bernard Bou
 */
class BncNodeFactory extends NodeFactory
{
	/**
	 * Make BNC root node
	 *
	 * @param thisDoc
	 *            is the DOM Document being built
	 * @param thisWordId
	 *            is the target word id
	 * @param thisPos
	 *            is the target pos
	 * @return newly created node
	 */
	@SuppressWarnings("boxing")
	static public Node makeBncRootNode(final Document thisDoc, final long thisWordId, final Character thisPos)
	{
		final Element thisRootNode = NodeFactory.makeNode(thisDoc, thisDoc, "bnc", null); //$NON-NLS-1$
		if (thisPos == null)
		{
			org.sqlunet.sql.NodeFactory.makeTargetNode(thisDoc, thisRootNode, "word-id", Long.toString(thisWordId)); //$NON-NLS-1$
		}
		else
		{
			org.sqlunet.sql.NodeFactory.makeTargetNode(thisDoc, thisRootNode, "word-id", Long.toString(thisWordId), "pos", Character.toString(thisPos)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return thisRootNode;
	}

	/**
	 * Make the BNC data node
	 *
	 * @param thisDoc
	 *            is the DOM Document being built
	 * @param thisParent
	 *            is the parent node to attach this node to
	 * @param thisData
	 *            is the BNC data
	 * @param i
	 *            the ith BNC data
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static Node makeBncNode(final Document thisDoc, final Node thisParent, final BncData thisData, final int i)
	{
		final Element thisElement = NodeFactory.makeNode(thisDoc, thisParent, "bncdata", null); //$NON-NLS-1$
		NodeFactory.makeAttribute(thisElement, "id", Integer.toString(i)); //$NON-NLS-1$
		NodeFactory.makeAttribute(thisElement, "pos", thisData.thePos); //$NON-NLS-1$

		BncNodeFactory.makeDataNode(thisDoc, thisElement, "freq", thisData.theFreq); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(thisDoc, thisElement, "range", thisData.theRange); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(thisDoc, thisElement, "disp", thisData.theDisp); //$NON-NLS-1$

		BncNodeFactory.makeDataNode(thisDoc, thisElement, "convfreq", thisData.theConvFreq); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(thisDoc, thisElement, "convrange", thisData.theConvRange); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(thisDoc, thisElement, "convdisp", thisData.theConvDisp); //$NON-NLS-1$

		BncNodeFactory.makeDataNode(thisDoc, thisElement, "taskfreq", thisData.theTaskFreq); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(thisDoc, thisElement, "taskrange", thisData.theTaskRange); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(thisDoc, thisElement, "taskdisp", thisData.theTaskDisp); //$NON-NLS-1$

		BncNodeFactory.makeDataNode(thisDoc, thisElement, "imagfreq", thisData.theImagFreq); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(thisDoc, thisElement, "imagrange", thisData.theImagRange); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(thisDoc, thisElement, "imagdisp", thisData.theImagDisp); //$NON-NLS-1$

		BncNodeFactory.makeDataNode(thisDoc, thisElement, "inffreq", thisData.theInfFreq); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(thisDoc, thisElement, "infrange", thisData.theInfRange); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(thisDoc, thisElement, "infdisp", thisData.theInfDisp); //$NON-NLS-1$

		BncNodeFactory.makeDataNode(thisDoc, thisElement, "spokenfreq", thisData.theSpokenFreq); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(thisDoc, thisElement, "spokenrange", thisData.theSpokenRange); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(thisDoc, thisElement, "spokendisp", thisData.theSpokenDisp); //$NON-NLS-1$

		BncNodeFactory.makeDataNode(thisDoc, thisElement, "writtenfreq", thisData.theWrittenFreq); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(thisDoc, thisElement, "writtenrange", thisData.theWrittenRange); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(thisDoc, thisElement, "writtendisp", thisData.theWrittenDisp); //$NON-NLS-1$
		return thisElement;
	}

	static private void makeDataNode(final Document thisDoc, final Element thisParent, final String thisName, final Object thisObject)
	{
		if (thisObject == null)
			return;
		NodeFactory.makeNode(thisDoc, thisParent, thisName, thisObject.toString());
	}
}