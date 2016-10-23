package org.sqlunet.wordnet.sql;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOM node factory
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class NodeFactory extends org.sqlunet.sql.NodeFactory
{
	/**
	 * Make part-of-speech node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @param value  is the part-of-speech
	 * @return newly created node
	 */
	static public Node makePosNode(final Document doc, final Node parent, final String value)
	{
		final Element element = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "pos", null); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "name", value); //
		return element;
	}

	/**
	 * Make lexdomain node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @param value  lexdomain
	 * @return newly created node
	 */
	static public Node makeLexDomainNode(final Document doc, final Node parent, final String value)
	{
		final Element element = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "lexdomain", null); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "name", value); //
		return element;
	}

	/**
	 * Make sense node
	 *
	 * @param doc      is the DOM Document being built
	 * @param parent   is the parent node to attach this node to
	 * @param senseIdx is the sense index
	 * @return newly created node
	 */
	static public Node makeSenseNode(final Document doc, final Node parent, final int senseIdx)
	{
		final Element element = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "sense", null); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "number", Integer.toString(senseIdx)); //
		return element;
	}

	/**
	 * Make sense node
	 *
	 * @param doc      is the DOM Document being built
	 * @param parent   is the parent node to attach this node to
	 * @param wordId   is the word id
	 * @param synsetId is the synset id
	 * @return newly created node
	 */
	static public Node makeSenseNode(final Document doc, final Node parent, final long wordId, final long synsetId)
	{
		final Element element = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "sense", null); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "word-id", Long.toString(wordId)); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "synset-id", Long.toString(synsetId)); //
		return element;
	}

	/**
	 * Make synset node
	 *
	 * @param doc      is the DOM Document being built
	 * @param parent   is the parent node to attach this node to
	 * @param size     is the synset's size (the number of words in the synset)
	 * @param synsetId is the synset's id in the database
	 * @return newly created element
	 */
	static public Element makeSynsetNode(final Document doc, final Node parent, final int size, final long synsetId)
	{
		final Element element = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "synset", null); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "size", Integer.toString(size)); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "synset-id", Long.toString(synsetId)); //
		return element;
	}

	/**
	 * Make word (synset item) node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @param word   target word
	 * @param id     is the word id
	 * @return newly created node
	 */
	@SuppressWarnings("UnusedReturnValue")
	static public Node makeWordNode(final Document doc, final Node parent, final String word, final long id)
	{
		final Element element = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "word", word); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "word-id", Long.toString(id)); //
		return element;
	}

	/**
	 * Make link node
	 *
	 * @param doc      is the DOM Document being built
	 * @param parent   is the parent node to attach this node to
	 * @param linkType is the link type
	 * @param level    is the recursion level
	 * @return newly created node
	 */
	static public Node makeLinkNode(final Document doc, final Node parent, final String linkType, final int level)
	{
		final Element element = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, linkType, null);
		if (level > 0)
		{
			org.sqlunet.sql.NodeFactory.makeAttribute(element, "level", Integer.toString(level)); //
		}
		return element;
	}

	/**
	 * Make 'more' link node (when recursiveness is broken and result is truncated)
	 *
	 * @param doc      is the DOM Document being built
	 * @param parent   is the parent node to attach this node to
	 * @param linkType is the link type
	 * @param level    is the recursion level
	 * @return newly created node
	 */
	@SuppressWarnings("UnusedReturnValue")
	static public Node makeMoreLinkNode(final Document doc, final Node parent, final String linkType, final int level)
	{
		final Element element = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, linkType, null);
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "level", Integer.toString(level)); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "more", "true"); //
		return element;
	}
}