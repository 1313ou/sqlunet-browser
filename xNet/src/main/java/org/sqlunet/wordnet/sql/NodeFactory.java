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
	 * @param thisDoc    is the DOM Document being built
	 * @param thisParent is the parent node to attach this node to
	 * @param thisValue  is the part-of-speech
	 * @return newly created node
	 */
	static public Node makePosNode(final Document thisDoc, final Node thisParent, final String thisValue)
	{
		final Element thisElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "pos", null); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "name", thisValue); //$NON-NLS-1$
		return thisElement;
	}

	/**
	 * Make lexdomain node
	 *
	 * @param thisDoc    is the DOM Document being built
	 * @param thisParent is the parent node to attach this node to
	 * @param thisValue  lexdomain
	 * @return newly created node
	 */
	static public Node makeLexDomainNode(final Document thisDoc, final Node thisParent, final String thisValue)
	{
		final Element thisElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "lexdomain", null); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "name", thisValue); //$NON-NLS-1$
		return thisElement;
	}

	/**
	 * Make sense node
	 *
	 * @param thisDoc      is the DOM Document being built
	 * @param thisParent   is the parent node to attach this node to
	 * @param thisSenseIdx is the sense index
	 * @return newly created node
	 */
	static public Node makeSenseNode(final Document thisDoc, final Node thisParent, final int thisSenseIdx)
	{
		final Element thisElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "sense", null); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "number", Integer.toString(thisSenseIdx)); //$NON-NLS-1$
		return thisElement;
	}

	/**
	 * Make sense node
	 *
	 * @param thisDoc      is the DOM Document being built
	 * @param thisParent   is the parent node to attach this node to
	 * @param thisWordId   is the wordid
	 * @param thisSynsetId is the synsetid
	 * @return newly created node
	 */
	static public Node makeSenseNode(final Document thisDoc, final Node thisParent, final long thisWordId, final long thisSynsetId)
	{
		final Element thisElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "sense", null); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "word-id", Long.toString(thisWordId)); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "synset-id", Long.toString(thisSynsetId)); //$NON-NLS-1$
		return thisElement;
	}

	/**
	 * Make synset node
	 *
	 * @param thisDoc    is the DOM Document being built
	 * @param thisParent is the parent node to attach this node to
	 * @param thisSize   is the synset's size (the number of words in the synset)
	 * @param thisId     is the synset's id in the database
	 * @return newly created element
	 */
	static public Element makeSynsetNode(final Document thisDoc, final Node thisParent, final int thisSize, final long thisId)
	{
		final Element thisElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "synset", null); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "size", Integer.toString(thisSize)); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "synset-id", Long.toString(thisId)); //$NON-NLS-1$
		return thisElement;
	}

	/**
	 * Make word (synset item) node
	 *
	 * @param thisDoc    is the DOM Document being built
	 * @param thisParent is the parent node to attach this node to
	 * @param thisWord   is the target word
	 * @param thisId     is the word id
	 * @return newly created node
	 */
	@SuppressWarnings("UnusedReturnValue")
	static public Node makeWordNode(final Document thisDoc, final Node thisParent, final String thisWord, final long thisId)
	{
		final Element thisElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "word", thisWord); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "word-id", Long.toString(thisId)); //$NON-NLS-1$
		return thisElement;
	}

	/**
	 * Make link node
	 *
	 * @param thisDoc      is the DOM Document being built
	 * @param thisParent   is the parent node to attach this node to
	 * @param thisLinkType is the link type
	 * @param thisLevel    is the recursion level
	 * @return newly created node
	 */
	static public Node makeLinkNode(final Document thisDoc, final Node thisParent, final String thisLinkType, final int thisLevel)
	{
		final Element thisElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, thisLinkType, null);
		if (thisLevel > 0)
		{
			org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "level", Integer.toString(thisLevel)); //$NON-NLS-1$
		}
		return thisElement;
	}

	/**
	 * Make 'more' link node (when recursiveness is broken and result is truncated)
	 *
	 * @param thisDoc      is the DOM Document being built
	 * @param thisParent   is the parent node to attach this node to
	 * @param thisLinkType is the link type
	 * @param thisLevel    is the recursion level
	 * @return newly created node
	 */
	@SuppressWarnings("UnusedReturnValue")
	static public Node makeMoreLinkNode(final Document thisDoc, final Node thisParent, final String thisLinkType, final int thisLevel)
	{
		final Element thisElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, thisLinkType, null);
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "level", Integer.toString(thisLevel)); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "more", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		return thisElement;
	}
}