/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.sql;

import org.sqlunet.sql.NodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import androidx.annotation.NonNull;

/**
 * DOM node factory
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class SnNodeFactory extends NodeFactory
{
	/**
	 * Make SyntagNet root node
	 *
	 * @param doc    is the DOM Document being built
	 * @param wordId target word id
	 * @return newly created node
	 */
	static public Node makeSnRootNode(@NonNull final Document doc, final long wordId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "syntagnet", null, SyntagNetImplementation.SN_NS);
		NodeFactory.addAttributes(rootNode, "wordid", Long.toString(wordId));
		return rootNode;
	}

	/**
	 * Make SyntagNet root node
	 *
	 * @param doc  is the DOM Document being built
	 * @param word target word
	 * @return newly created node
	 */
	static public Node makeSnRootNode(@NonNull final Document doc, final String word)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "syntagnet", null, SyntagNetImplementation.SN_NS);
		NodeFactory.addAttributes(rootNode, "word", word);
		return rootNode;
	}

	/**
	 * Make SyntagNet root node
	 *
	 * @param doc       is the DOM Document being built
	 * @param roleSetId target roleSet id
	 * @return newly created node
	 */
	static public Node makeCollocationNode(@NonNull final Document doc, long roleSetId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "syntagnet", null, SyntagNetImplementation.SN_NS);
		NodeFactory.addAttributes(rootNode, "rolesetid", Long.toString(roleSetId));
		return rootNode;
	}

	/**
	 * Make the collocation node
	 *
	 * @param doc         is the DOM Document being built
	 * @param parent      is the parent node to attach this node to
	 * @param collocation is the roleSet information
	 * @param i           the ith roleSet
	 */
	static public Node makeCollocationNode(@NonNull final Document doc, final Node parent, @NonNull final Collocation collocation, final int i)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "roleset", null);
		NodeFactory.makeAttribute(element, "id", Integer.toString(i));
		NodeFactory.makeAttribute(element, "word1", collocation.word1);
		NodeFactory.makeAttribute(element, "word2", collocation.word2);
		NodeFactory.makeAttribute(element, "word1id", Long.toString(collocation.word1Id));
		NodeFactory.makeAttribute(element, "word2id", Long.toString(collocation.word2Id));
		NodeFactory.makeAttribute(element, "synset1id", Long.toString(collocation.synset1Id));
		NodeFactory.makeAttribute(element, "synset2id", Long.toString(collocation.synset2Id));
		return element;
	}
}