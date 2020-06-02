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
	 * @param doc           is the DOM Document being built
	 * @param collocationId target collocation id
	 * @return newly created node
	 */
	static public Node makeCollocationNode(@NonNull final Document doc, long collocationId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "syntagnet", null, SyntagNetImplementation.SN_NS);
		NodeFactory.addAttributes(rootNode, "syntagnetid", Long.toString(collocationId));
		return rootNode;
	}

	/**
	 * Make the collocation node
	 *
	 * @param doc         is the DOM Document being built
	 * @param parent      is the parent node to attach this node to
	 * @param collocation is the collocation information
	 * @param i           the ith collocation
	 */
	static public Node makeCollocationNode(@NonNull final Document doc, final Node parent, @NonNull final Collocation.WithDefinitionAndPos collocation, final int i)
	{
		final Element collocationElement = NodeFactory.makeNode(doc, parent, "collocation", null);
		NodeFactory.makeAttribute(collocationElement, "ith", Integer.toString(i));
		NodeFactory.makeAttribute(collocationElement, "collocationid", Long.toString(collocation.collocationId));
		NodeFactory.makeAttribute(collocationElement, "word1id", Long.toString(collocation.word1Id));
		NodeFactory.makeAttribute(collocationElement, "word2id", Long.toString(collocation.word2Id));
		NodeFactory.makeAttribute(collocationElement, "synset1id", Long.toString(collocation.synset1Id));
		NodeFactory.makeAttribute(collocationElement, "synset2id", Long.toString(collocation.synset2Id));

		final Element word1Element = NodeFactory.makeNode(doc, collocationElement, "word", collocation.word1);
		NodeFactory.makeAttribute(word1Element, "wordid", Long.toString(collocation.word1Id));
		NodeFactory.makeAttribute(word1Element, "which", "1");
		final Element word2Element = NodeFactory.makeNode(doc, collocationElement, "word", collocation.word2);
		NodeFactory.makeAttribute(word2Element, "wordid", Long.toString(collocation.word2Id));
		NodeFactory.makeAttribute(word2Element, "which", "2");

		final Element synset1Element = NodeFactory.makeNode(doc, collocationElement, "synset", collocation.definition1);
		NodeFactory.makeAttribute(synset1Element, "synsetid", Long.toString(collocation.synset1Id));
		NodeFactory.makeAttribute(synset1Element, "pos", Character.toString(collocation.pos1));
		NodeFactory.makeAttribute(synset1Element, "which", "1");
		final Element synset2Element = NodeFactory.makeNode(doc, collocationElement, "synset", collocation.definition2);
		NodeFactory.makeAttribute(synset2Element, "synsetid", Long.toString(collocation.synset2Id));
		NodeFactory.makeAttribute(synset1Element, "pos", Character.toString(collocation.pos2));
		NodeFactory.makeAttribute(synset2Element, "which", "2");

		return collocationElement;
	}

	/**
	 * Make the collocation node
	 *
	 * @param doc         is the DOM Document being built
	 * @param parent      is the parent node to attach this node to
	 * @param collocation is the collocation information
	 * @param i           the ith collocation
	 */
	@SuppressWarnings("UnusedReturnValue")
	static public Node makeSelectorCollocationNode(@NonNull final Document doc, final Node parent, @NonNull final Collocation collocation, final int i)
	{
		final Element collocationElement = NodeFactory.makeNode(doc, parent, "collocation", null);
		NodeFactory.makeAttribute(collocationElement, "ith", Integer.toString(i));
		NodeFactory.makeAttribute(collocationElement, "collocationid", Long.toString(collocation.collocationId));
		NodeFactory.makeAttribute(collocationElement, "word1id", Long.toString(collocation.word1Id));
		NodeFactory.makeAttribute(collocationElement, "word2id", Long.toString(collocation.word2Id));
		NodeFactory.makeAttribute(collocationElement, "synset1id", Long.toString(collocation.synset1Id));
		NodeFactory.makeAttribute(collocationElement, "synset2id", Long.toString(collocation.synset2Id));

		final Element word1Element = NodeFactory.makeNode(doc, collocationElement, "word", collocation.word1);
		NodeFactory.makeAttribute(word1Element, "wordid", Long.toString(collocation.word1Id));
		NodeFactory.makeAttribute(word1Element, "which", "1");
		final Element word2Element = NodeFactory.makeNode(doc, collocationElement, "word", collocation.word2);
		NodeFactory.makeAttribute(word2Element, "wordid", Long.toString(collocation.word2Id));
		NodeFactory.makeAttribute(word2Element, "which", "2");
		return collocationElement;
	}
}