/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.sql;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import androidx.annotation.NonNull;

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
	@NonNull
	static public Node makePosNode(@NonNull final Document doc, final Node parent, final String value)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "pos", null);
		NodeFactory.makeAttribute(element, "name", value);
		return element;
	}

	/**
	 * Make domain node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @param value  domain
	 * @return newly created node
	 */
	@NonNull
	static public Node makeDomainNode(@NonNull final Document doc, final Node parent, final String value)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "domain", null);
		NodeFactory.makeAttribute(element, "name", value);
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
	static public Element makeSenseNode(@NonNull final Document doc, final Node parent, final long wordId, final long synsetId, final int senseIdx)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "sense", null);
		if (wordId != 0)
		{
			NodeFactory.makeAttribute(element, "wordid", Long.toString(wordId));
		}
		if (synsetId != 0)
		{
			NodeFactory.makeAttribute(element, "synsetid", Long.toString(synsetId));
		}
		if (senseIdx != 0)
		{
			NodeFactory.makeAttribute(element, "number", Integer.toString(senseIdx));
		}
		return element;
	}

	/**
	 * Make synset node
	 *
	 * @param doc      is the DOM Document being built
	 * @param parent   is the parent node to attach this node to
	 * @param synsetId is the synset's id in the database
	 * @param size     is the synset's size (the number of words in the synset)
	 * @return newly created element
	 */
	@NonNull
	static public Element makeSynsetNode(@NonNull final Document doc, final Node parent, final long synsetId, final int size)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "synset", null);
		NodeFactory.makeAttribute(element, "synsetid", Long.toString(synsetId));
		if (size != 0)
		{
			NodeFactory.makeAttribute(element, "size", Integer.toString(size));
		}
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
	@NonNull
	static public Node makeWordNode(@NonNull final Document doc, final Node parent, final String word, final long id)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "word", word);
		NodeFactory.makeAttribute(element, "wordid", Long.toString(id));
		return element;
	}

	/**
	 * Make link node
	 *
	 * @param doc          is the DOM Document being built
	 * @param parent       is the parent node to attach this node to
	 * @param relationType is the relation type
	 * @param level        is the recursion level
	 * @return newly created node
	 */
	static public Node makeRelationNode(@NonNull final Document doc, final Node parent, final String relationType, final int level)
	{
		final Element element = NodeFactory.makeNode(doc, parent, relationType, null);
		if (level > 0)
		{
			NodeFactory.makeAttribute(element, "level", Integer.toString(level));
		}
		return element;
	}

	/**
	 * Make 'more' relation node (when recursiveness is broken and result is truncated)
	 *
	 * @param doc          is the DOM Document being built
	 * @param parent       is the parent node to attach this node to
	 * @param relationType is the relation type
	 * @param level        is the recursion level
	 * @return newly created node
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	static public Node makeMoreRelationNode(@NonNull final Document doc, final Node parent, final String relationType, final int level)
	{
		final Element element = NodeFactory.makeNode(doc, parent, relationType, null);
		NodeFactory.makeAttribute(element, "level", Integer.toString(level));
		NodeFactory.makeAttribute(element, "more", "true");
		return element;
	}
}