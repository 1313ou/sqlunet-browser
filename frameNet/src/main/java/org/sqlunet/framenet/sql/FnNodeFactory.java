/*
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 * Created on 31 dec. 2004
 * Filename : FnNodeFactory.java
 * Class encapsulates creation of DOM nodes
 */
package org.sqlunet.framenet.sql;

import org.sqlunet.sql.DocumentFragmentParser;
import org.sqlunet.sql.NodeFactory;
import org.sqlunet.sql.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOM node factory
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnNodeFactory extends NodeFactory
{
	/**
	 * Make root node
	 *
	 * @param doc    is the DOM Document being built
	 * @param wordId is the target word id
	 * @param pos    is the target pos
	 * @return newly created node
	 */
	static public Node makeFnRootNode(final Document doc, final long wordId, final Character pos)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "framenet", null); //$NON-NLS-1$
		if (pos == null)
		{
			NodeFactory.makeTargetNode(doc, rootNode, "wordid", Long.toString(wordId)); //$NON-NLS-1$
		}
		else
		{
			NodeFactory.makeTargetNode(doc, rootNode, "wordid", Long.toString(wordId), "pos", Character.toString(pos)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return rootNode;
	}

	/**
	 * Make root node
	 *
	 * @param doc  is the DOM Document being built
	 * @param word is the target word
	 * @param pos  is the target pos
	 * @return newly created node
	 */
	static public Node makeFnRootNode(final Document doc, final String word, final Character pos)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "framenet", null); //$NON-NLS-1$
		if (pos == null)
		{
			NodeFactory.makeTargetNode(doc, rootNode, "word", word); //$NON-NLS-1$
		}
		else
		{
			NodeFactory.makeTargetNode(doc, rootNode, "word", word, "pos", Character.toString(pos)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return rootNode;
	}

	/**
	 * Make frame node
	 *
	 * @param doc     is the DOM Document being built
	 * @param parent  is the parent node to attach this node to
	 * @param lexUnit is the frame information
	 */
	public static Node makeFnLexunitNode(final Document doc, final Node parent, final FnLexUnit lexUnit)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "lexunit", null); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "name", lexUnit.lexUnit); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "luid", Long.toString(lexUnit.luId)); //$NON-NLS-1$
		NodeFactory.makeText(doc, element, lexUnit.definition);
		return element;
	}

	/**
	 * Make frame node
	 *
	 * @param doc      is the DOM Document being built
	 * @param parent   is the parent node to attach this node to
	 * @param frame    is the frame
	 * @param removeEx whether to remove <ex> element
	 * @return newly created node
	 */
	public static Node makeFnFrameNode(final Document doc, final Node parent, final FnFrame frame, final boolean removeEx)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "frame", null); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "name", frame.frameName); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "frameid", Long.toString(frame.frameId)); //$NON-NLS-1$

		DocumentFragmentParser.mount(doc, element, frame.frameDefinition, "framedefinition"); //$NON-NLS-1$
		if (removeEx)
		{
			// retrieve the elements 'ex'
			final NodeList examples = element.getElementsByTagName("ex"); //$NON-NLS-1$
			for (int i = 0; i < examples.getLength(); i++)
			{
				final Node example = examples.item(i);
				example.getParentNode().removeChild(example);
			}
			element.normalize();
		}

		if (frame.semTypes != null && !frame.semTypes.isEmpty())
		{
			for (final FnSemType semType : frame.semTypes)
			{
				final Element element2 = NodeFactory.makeNode(doc, element, "semtype", null); //$NON-NLS-1$
				NodeFactory.makeAttribute(element2, "semtypeid", Long.toString(semType.semTypeId)); //$NON-NLS-1$
				NodeFactory.makeAttribute(element2, "semtype", semType.semTypeName); //$NON-NLS-1$
				NodeFactory.makeText(doc, element, semType.semTypeDefinition);
			}
		}
		if (frame.relatedFrames != null && !frame.relatedFrames.isEmpty())
		{
			for (final FnRelatedFrame relatedFrame : frame.relatedFrames)
			{
				final Element element2 = NodeFactory.makeNode(doc, element, "related", null); //$NON-NLS-1$
				NodeFactory.makeAttribute(element2, "frameid", Long.toString(relatedFrame.frameId)); //$NON-NLS-1$
				NodeFactory.makeAttribute(element2, "frame", relatedFrame.frameName); //$NON-NLS-1$
				NodeFactory.makeAttribute(element2, "relation", relatedFrame.relation); //$NON-NLS-1$
			}
		}

		return element;
	}

	/**
	 * Make FE node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @param fe     is the FE
	 * @return newly created node
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static Node makeFnFENode(final Document doc, final Node parent, final FnFrameElement fe)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "fe", null); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "name", fe.feType); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "feid", Long.toString(fe.feId)); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "coreset", Integer.toString(fe.coreSet)); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "type", fe.coreType); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "semtype", Utils.join(fe.semTypes)); //$NON-NLS-1$
		DocumentFragmentParser.mount(doc, element, fe.feDefinition, "fedefinition"); //$NON-NLS-1$
		return element;
	}

	/**
	 * Make governor node
	 *
	 * @param doc      is the DOM Document being built
	 * @param parent   is the parent node to attach this node to
	 * @param governor is the governor
	 * @return newly created node
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static Node makeFnGovernorNode(final Document doc, final Node parent, final FnGovernor governor)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "governor", null); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "governorid", Long.toString(governor.governorId)); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "wordid", Long.toString(governor.wordId)); //$NON-NLS-1$
		NodeFactory.makeText(doc, element, governor.governor);
		return element;
	}

	/**
	 * Make sentences node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @return newly created node
	 */
	public static Node makeFnSentencesNode(final Document doc, final Node parent)
	{
		return NodeFactory.makeNode(doc, parent, "sentences", null);
	}

	/**
	 * Make sentence node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @param i      the ith
	 */
	public static Node makeFnSentenceNode(final Document doc, final Node parent, final FnSentence sentence, final int i)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "sentence", null); //$NON-NLS-1$
		if (i != -1)
		{
			NodeFactory.makeAttribute(element, "num", Integer.toString(i)); //$NON-NLS-1$
		}
		NodeFactory.makeAttribute(element, "sentenceid", Long.toString(sentence.sentenceId)); //$NON-NLS-1$
		NodeFactory.makeText(doc, element, sentence.text);
		return element;
	}

	/**
	 * Make annoset node
	 *
	 * @param doc     is the DOM Document being built
	 * @param parent  is the parent node to attach this node to
	 * @param annoSet annoset
	 * @return annoset node
	 */
	public static Node makeFnAnnoSetNode(final Document doc, final Node parent, final FnAnnoSet annoSet)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "annoset", null); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "annosetid", Long.toString(annoSet.annoSetId)); //$NON-NLS-1$
		FnNodeFactory.makeFnSentenceNode(doc, element, annoSet.sentence, -1);
		return element;
	}

	/**
	 * Make layer node
	 *
	 * @param doc   is the DOM Document being built
	 * @param layer is the target layer
	 * @return layer node
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static Node makeFnLayerNode(final Document doc, final Node parent, final FnLayer layer)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "layer", null); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "rank", Long.toString(layer.rank)); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "layerid", Long.toString(layer.layerId)); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "type", layer.layerType); //$NON-NLS-1$
		if (layer.labels != null)
		{
			for (final FnLabel label : layer.labels)
			{
				final Element element2 = NodeFactory.makeNode(doc, element, "label", null); //$NON-NLS-1$
				if (!"0".equals(label.from) || !"0".equals(label.to)) //$NON-NLS-1$ //$NON-NLS-2$
				{
					NodeFactory.makeAttribute(element2, "from", label.from); //$NON-NLS-1$
					NodeFactory.makeAttribute(element2, "to", label.to); //$NON-NLS-1$
				}
				NodeFactory.makeAttribute(element2, "label", label.label); //$NON-NLS-1$
				if (label.itype != null && !label.itype.isEmpty())
				{
					NodeFactory.makeAttribute(element2, "itype", label.itype); //$NON-NLS-1$
				}
			}
		}
		return element;
	}

	/**
	 * Make root frame node
	 *
	 * @param doc     is the DOM Document being built
	 * @param frameId is the target frame id
	 * @return root frame node
	 */
	public static Node makeFnRootFrameNode(final Document doc, final long frameId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "framenet", null); //$NON-NLS-1$
		NodeFactory.makeTargetNode(doc, rootNode, "frameid", Long.toString(frameId)); //$NON-NLS-1$
		return rootNode;
	}

	/**
	 * Make root lexunit node
	 *
	 * @param doc  is the DOM Document being built
	 * @param luId is the target luid
	 * @return root lexunit node
	 */
	public static Node makeFnRootLexUnitNode(final Document doc, final long luId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "framenet", null); //$NON-NLS-1$
		NodeFactory.makeTargetNode(doc, rootNode, "luid", Long.toString(luId)); //$NON-NLS-1$
		return rootNode;
	}

	/**
	 * Make root sentence node
	 *
	 * @param doc        is the DOM Document being built
	 * @param sentenceId is the target sentenceid
	 * @return root sentence node
	 */
	public static Node makeFnRootSentenceNode(final Document doc, final long sentenceId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "framenet", null); //$NON-NLS-1$
		NodeFactory.makeTargetNode(doc, rootNode, "sentenceid", Long.toString(sentenceId)); //$NON-NLS-1$
		return rootNode;
	}

	/**
	 * Make root annoset node
	 *
	 * @param doc       is the DOM Document being built
	 * @param annoSetId is the target annosetid
	 * @return root annoset node
	 */
	public static Node makeFnRootAnnosetNode(final Document doc, final long annoSetId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "framenet", null); //$NON-NLS-1$
		NodeFactory.makeTargetNode(doc, rootNode, "annosetid", Long.toString(annoSetId)); //$NON-NLS-1$
		return rootNode;
	}
}