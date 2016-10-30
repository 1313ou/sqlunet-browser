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
	 * @param doc    DOM Document being built
	 * @param wordId target word id
	 * @param pos    target pos
	 * @return newly created node
	 */
	static public Node makeFnRootNode(final Document doc, final long wordId, final Character pos)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "framenet", null); //
		if (pos == null)
		{
			NodeFactory.makeTargetNode(doc, rootNode, "wordid", Long.toString(wordId)); //
		}
		else
		{
			NodeFactory.makeTargetNode(doc, rootNode, "wordid", Long.toString(wordId), "pos", Character.toString(pos)); //
		}
		return rootNode;
	}

	/**
	 * Make root node
	 *
	 * @param doc  DOM Document being built
	 * @param word target word
	 * @param pos  target pos
	 * @return newly created node
	 */
	static public Node makeFnRootNode(final Document doc, final String word, final Character pos)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "framenet", null); //
		if (pos == null)
		{
			NodeFactory.makeTargetNode(doc, rootNode, "word", word); //
		}
		else
		{
			NodeFactory.makeTargetNode(doc, rootNode, "word", word, "pos", Character.toString(pos)); //
		}
		return rootNode;
	}

	/**
	 * Make lex unit node
	 *
	 * @param doc     DOM Document being built
	 * @param parent  parent node to attach this node to
	 * @param lexUnit frame information
	 */
	public static Node makeFnLexunitNode(final Document doc, final Node parent, final FnLexUnit lexUnit)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "lexunit", null); //
		NodeFactory.makeAttribute(element, "name", lexUnit.lexUnit); //
		NodeFactory.makeAttribute(element, "luid", Long.toString(lexUnit.luId)); //
		NodeFactory.makeText(doc, element, lexUnit.definition);
		return element;
	}

	/**
	 * Make frame node
	 *
	 * @param doc      DOM Document being built
	 * @param parent   parent node to attach this node to
	 * @param frame    frame
	 * @param removeEx whether to remove <ex> element
	 * @return newly created node
	 */
	public static Node makeFnFrameNode(final Document doc, final Node parent, final FnFrame frame, final boolean removeEx)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "frame", null); //
		NodeFactory.makeAttribute(element, "name", frame.frameName); //
		NodeFactory.makeAttribute(element, "frameid", Long.toString(frame.frameId)); //

		DocumentFragmentParser.mount(doc, element, frame.frameDefinition, "framedefinition"); //
		if (removeEx)
		{
			// retrieve the elements 'ex'
			final NodeList examples = element.getElementsByTagName("ex"); //
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
				final Element element2 = NodeFactory.makeNode(doc, element, "semtype", null); //
				NodeFactory.makeAttribute(element2, "semtypeid", Long.toString(semType.semTypeId)); //
				NodeFactory.makeAttribute(element2, "semtype", semType.semTypeName); //
				NodeFactory.makeText(doc, element, semType.semTypeDefinition);
			}
		}
		if (frame.relatedFrames != null && !frame.relatedFrames.isEmpty())
		{
			for (final FnRelatedFrame relatedFrame : frame.relatedFrames)
			{
				final Element element2 = NodeFactory.makeNode(doc, element, "related", null); //
				NodeFactory.makeAttribute(element2, "frameid", Long.toString(relatedFrame.frameId)); //
				NodeFactory.makeAttribute(element2, "frame", relatedFrame.frameName); //
				NodeFactory.makeAttribute(element2, "relation", relatedFrame.relation); //
			}
		}

		return element;
	}

	/**
	 * Make FE node
	 *
	 * @param doc    DOM Document being built
	 * @param parent parent node to attach this node to
	 * @param fe     FE
	 * @return newly created node
	 */
	public static Node makeFnFENode(final Document doc, final Node parent, final FnFrameElement fe)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "fe", null); //
		NodeFactory.makeAttribute(element, "name", fe.feType); //
		NodeFactory.makeAttribute(element, "feid", Long.toString(fe.feId)); //
		NodeFactory.makeAttribute(element, "coreset", Integer.toString(fe.coreSet)); //
		NodeFactory.makeAttribute(element, "type", fe.coreType); //
		NodeFactory.makeAttribute(element, "semtype", Utils.join(fe.semTypes)); //
		DocumentFragmentParser.mount(doc, element, fe.feDefinition, "fedefinition"); //
		return element;
	}

	/**
	 * Make governor node
	 *
	 * @param doc      DOM Document being built
	 * @param parent   parent node to attach this node to
	 * @param governor governor
	 * @return newly created node
	 */
	public static Node makeFnGovernorNode(final Document doc, final Node parent, final FnGovernor governor)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "governor", null); //
		NodeFactory.makeAttribute(element, "governorid", Long.toString(governor.governorId)); //
		NodeFactory.makeAttribute(element, "wordid", Long.toString(governor.wordId)); //
		NodeFactory.makeText(doc, element, governor.governor);
		return element;
	}

	/**
	 * Make sentences node
	 *
	 * @param doc    DOM Document being built
	 * @param parent parent node to attach this node to
	 * @return newly created node
	 */
	public static Node makeFnSentencesNode(final Document doc, final Node parent)
	{
		return NodeFactory.makeNode(doc, parent, "sentences", null);
	}

	/**
	 * Make sentence node
	 *
	 * @param doc    DOM Document being built
	 * @param parent parent node to attach this node to
	 * @param i      the ith
	 */
	public static Node makeFnSentenceNode(final Document doc, final Node parent, final FnSentence sentence, final int i)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "sentence", null); //
		if (i != -1)
		{
			NodeFactory.makeAttribute(element, "num", Integer.toString(i)); //
		}
		NodeFactory.makeAttribute(element, "sentenceid", Long.toString(sentence.sentenceId)); //
		NodeFactory.makeText(doc, element, sentence.text);
		return element;
	}

	/**
	 * Make annoSet node
	 *
	 * @param doc     DOM Document being built
	 * @param parent  parent node to attach this node to
	 * @param annoSet annoSet
	 * @return annoSet node
	 */
	public static Node makeFnAnnoSetNode(final Document doc, final Node parent, final FnAnnoSet annoSet)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "annoset", null); //
		NodeFactory.makeAttribute(element, "annosetid", Long.toString(annoSet.annoSetId)); //
		FnNodeFactory.makeFnSentenceNode(doc, element, annoSet.sentence, -1);
		return element;
	}

	/**
	 * Make layer node
	 *
	 * @param doc   DOM Document being built
	 * @param layer target layer
	 * @return layer node
	 */
	public static Node makeFnLayerNode(final Document doc, final Node parent, final FnLayer layer)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "layer", null); //
		NodeFactory.makeAttribute(element, "rank", Long.toString(layer.rank)); //
		NodeFactory.makeAttribute(element, "layerid", Long.toString(layer.layerId)); //
		NodeFactory.makeAttribute(element, "type", layer.layerType); //
		if (layer.labels != null)
		{
			for (final FnLabel label : layer.labels)
			{
				final Element element2 = NodeFactory.makeNode(doc, element, "label", null); //
				if (!"0".equals(label.from) || !"0".equals(label.to)) //
				{
					NodeFactory.makeAttribute(element2, "from", label.from); //
					NodeFactory.makeAttribute(element2, "to", label.to); //
				}
				NodeFactory.makeAttribute(element2, "label", label.label); //
				if (label.iType != null && !label.iType.isEmpty())
				{
					NodeFactory.makeAttribute(element2, "itype", label.iType); //
				}
			}
		}
		return element;
	}

	/**
	 * Make root frame node
	 *
	 * @param doc     DOM Document being built
	 * @param frameId target frame id
	 * @return root frame node
	 */
	public static Node makeFnRootFrameNode(final Document doc, final long frameId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "framenet", null); //
		NodeFactory.makeTargetNode(doc, rootNode, "frameid", Long.toString(frameId)); //
		return rootNode;
	}

	/**
	 * Make root lexunit node
	 *
	 * @param doc  DOM Document being built
	 * @param luId target luId
	 * @return root lexunit node
	 */
	public static Node makeFnRootLexUnitNode(final Document doc, final long luId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "framenet", null); //
		NodeFactory.makeTargetNode(doc, rootNode, "luid", Long.toString(luId)); //
		return rootNode;
	}

	/**
	 * Make root sentence node
	 *
	 * @param doc        DOM Document being built
	 * @param sentenceId target sentence id
	 * @return root sentence node
	 */
	public static Node makeFnRootSentenceNode(final Document doc, final long sentenceId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "framenet", null); //
		NodeFactory.makeTargetNode(doc, rootNode, "sentenceid", Long.toString(sentenceId)); //
		return rootNode;
	}

	/**
	 * Make root annoSet node
	 *
	 * @param doc       DOM Document being built
	 * @param annoSetId target annoSetId
	 * @return root annoSet node
	 */
	public static Node makeFnRootAnnoSetNode(final Document doc, final long annoSetId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "framenet", null); //
		NodeFactory.makeTargetNode(doc, rootNode, "annosetid", Long.toString(annoSetId)); //
		return rootNode;
	}
}