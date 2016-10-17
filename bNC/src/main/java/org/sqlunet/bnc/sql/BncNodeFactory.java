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
	 * @param doc    is the DOM Document being built
	 * @param wordId is the target word id
	 * @param pos    is the target pos
	 * @return newly created node
	 */
	static public Node makeBncRootNode(final Document doc, final long wordId, final Character pos)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "bnc", null); //$NON-NLS-1$
		if (pos == null)
		{
			org.sqlunet.sql.NodeFactory.makeTargetNode(doc, rootNode, "word-id", Long.toString(wordId)); //$NON-NLS-1$
		}
		else
		{
			org.sqlunet.sql.NodeFactory.makeTargetNode(doc, rootNode, "word-id", Long.toString(wordId), "pos", Character.toString(pos)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return rootNode;
	}

	/**
	 * Make the BNC data node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @param data   is the BNC data
	 * @param i      the ith BNC data
	 */
	public static Node makeBncNode(final Document doc, final Node parent, final BncData data, final int i)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "bncdata", null); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "id", Integer.toString(i)); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "pos", data.pos); //$NON-NLS-1$

		BncNodeFactory.makeDataNode(doc, element, "freq", data.freq); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(doc, element, "range", data.range); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(doc, element, "disp", data.disp); //$NON-NLS-1$

		BncNodeFactory.makeDataNode(doc, element, "convfreq", data.convFreq); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(doc, element, "convrange", data.convRange); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(doc, element, "convdisp", data.convDisp); //$NON-NLS-1$

		BncNodeFactory.makeDataNode(doc, element, "taskfreq", data.taskFreq); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(doc, element, "taskrange", data.taskRange); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(doc, element, "taskdisp", data.taskDisp); //$NON-NLS-1$

		BncNodeFactory.makeDataNode(doc, element, "imagfreq", data.imagFreq); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(doc, element, "imagrange", data.imagRange); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(doc, element, "imagdisp", data.imagDisp); //$NON-NLS-1$

		BncNodeFactory.makeDataNode(doc, element, "inffreq", data.infFreq); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(doc, element, "infrange", data.infRange); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(doc, element, "infdisp", data.infDisp); //$NON-NLS-1$

		BncNodeFactory.makeDataNode(doc, element, "spokenfreq", data.spokenFreq); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(doc, element, "spokenrange", data.spokenRange); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(doc, element, "spokendisp", data.spokenDisp); //$NON-NLS-1$

		BncNodeFactory.makeDataNode(doc, element, "writtenfreq", data.writtenFreq); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(doc, element, "writtenrange", data.writtenRange); //$NON-NLS-1$
		BncNodeFactory.makeDataNode(doc, element, "writtendisp", data.writtenDisp); //$NON-NLS-1$
		return element;
	}

	static private void makeDataNode(final Document doc, final Element parent, final String name, final Object object)
	{
		if (object == null)
		{
			return;
		}
		NodeFactory.makeNode(doc, parent, name, object.toString());
	}
}