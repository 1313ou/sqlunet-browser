package org.sqlunet.bnc.sql;

import org.sqlunet.sql.NodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOM node factory
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class BncNodeFactory extends NodeFactory
{
	/**
	 * Make BNC root node
	 *
	 * @param doc    is the DOM Document being built
	 * @param wordId target word id
	 * @param pos    target pos
	 * @return newly created node
	 */
	static public Node makeBncRootNode(final Document doc, final long wordId, final Character pos)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "bnc", null, BncImplementation.BNCNS);
		if (pos == null)
		{
			NodeFactory.addAttributes(rootNode, "wordid", Long.toString(wordId));
		}
		else
		{
			NodeFactory.addAttributes(rootNode, "wordid", Long.toString(wordId), "pos", Character.toString(pos));
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
		final Element element = NodeFactory.makeNode(doc, parent, "bncdata", null);
		NodeFactory.makeAttribute(element, "id", Integer.toString(i));
		NodeFactory.makeAttribute(element, "pos", data.pos);

		BncNodeFactory.makeDataNode(doc, element, "freq", data.freq);
		BncNodeFactory.makeDataNode(doc, element, "range", data.range);
		BncNodeFactory.makeDataNode(doc, element, "disp", data.disp);

		BncNodeFactory.makeDataNode(doc, element, "convfreq", data.convFreq);
		BncNodeFactory.makeDataNode(doc, element, "convrange", data.convRange);
		BncNodeFactory.makeDataNode(doc, element, "convdisp", data.convDisp);

		BncNodeFactory.makeDataNode(doc, element, "taskfreq", data.taskFreq);
		BncNodeFactory.makeDataNode(doc, element, "taskrange", data.taskRange);
		BncNodeFactory.makeDataNode(doc, element, "taskdisp", data.taskDisp);

		BncNodeFactory.makeDataNode(doc, element, "imagfreq", data.imagFreq);
		BncNodeFactory.makeDataNode(doc, element, "imagrange", data.imagRange);
		BncNodeFactory.makeDataNode(doc, element, "imagdisp", data.imagDisp);

		BncNodeFactory.makeDataNode(doc, element, "inffreq", data.infFreq);
		BncNodeFactory.makeDataNode(doc, element, "infrange", data.infRange);
		BncNodeFactory.makeDataNode(doc, element, "infdisp", data.infDisp);

		BncNodeFactory.makeDataNode(doc, element, "spokenfreq", data.spokenFreq);
		BncNodeFactory.makeDataNode(doc, element, "spokenrange", data.spokenRange);
		BncNodeFactory.makeDataNode(doc, element, "spokendisp", data.spokenDisp);

		BncNodeFactory.makeDataNode(doc, element, "writtenfreq", data.writtenFreq);
		BncNodeFactory.makeDataNode(doc, element, "writtenrange", data.writtenRange);
		BncNodeFactory.makeDataNode(doc, element, "writtendisp", data.writtenDisp);
		return element;
	}

	static private void makeDataNode(final Document doc, final Node parent, final String name, final Object object)
	{
		if (object == null)
		{
			return;
		}
		NodeFactory.makeNode(doc, parent, name, object.toString());
	}
}