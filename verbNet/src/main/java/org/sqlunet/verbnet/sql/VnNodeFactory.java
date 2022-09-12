/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.verbnet.sql;

import org.sqlunet.wordnet.sql.NodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * VerbNet DOM node factory
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class VnNodeFactory extends NodeFactory
{
	/**
	 * Make VerbNet root node
	 *
	 * @param doc     is the DOM Document being built
	 * @param classId target class id
	 * @return newly created node
	 */
	@NonNull
	static public Node makeVnRootClassNode(@NonNull final Document doc, final long classId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "verbnet", null, VerbNetImplementation.VN_NS);
		NodeFactory.addAttributes(rootNode, "classid", Long.toString(classId));
		return rootNode;
	}

	/**
	 * Make VerbNet root node
	 *
	 * @param doc      is the DOM Document being built
	 * @param wordId   target word id
	 * @param synsetId target synset id (0 for all)
	 * @return newly created node
	 */
	@NonNull
	static public Node makeVnRootNode(@NonNull final Document doc, final long wordId, @Nullable final Long synsetId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "verbnet", null, VerbNetImplementation.VN_NS);
		if (synsetId == null || synsetId == 0)
		{
			NodeFactory.addAttributes(rootNode, "wordid", Long.toString(wordId));
		}
		else
		{
			NodeFactory.addAttributes(rootNode, "wordid", Long.toString(wordId), "synsetid", Long.toString(synsetId));
		}
		return rootNode;
	}

	/**
	 * Make VerbNet root node
	 *
	 * @param doc  is the DOM Document being built
	 * @param word target word
	 * @return newly created node
	 */
	@NonNull
	static public Node makeVnRootNode(@NonNull final Document doc, final String word)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "verbnet", null, VerbNetImplementation.VN_NS);
		NodeFactory.addAttributes(rootNode, "word", word);
		return rootNode;
	}

	/**
	 * Make class node
	 *
	 * @param doc     is the DOM Document being built
	 * @param parent  is the parent node to attach this node to
	 * @param vnClass is the class
	 * @return newly created node
	 */
	static public Node makeVnClassNode(@NonNull final Document doc, final Node parent, @NonNull final VnClass vnClass)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "vnclass", null);
		NodeFactory.makeAttribute(element, "name", vnClass.className);
		return element;
	}

	/**
	 * Make the class-with-sense node
	 *
	 * @param doc     is the DOM Document being built
	 * @param parent  is the parent node to attach this node to
	 * @param vnClass is the vn class with sense
	 */
	static public Node makeVnClassWithSenseNode(@NonNull final Document doc, final Node parent, @NonNull final VnClassWithSense vnClass)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "vnclass", null);
		NodeFactory.addAttributes(element, //
				"name", vnClass.className, //
				"classid", Long.toString(vnClass.classId), //
				"wordid", Long.toString(vnClass.wordId), //
				"synsetid", Long.toString(vnClass.synsetId), //
				"sensenum", Integer.toString(vnClass.senseNum), //
				"sensekey", vnClass.senseKey, //
				"groupings", vnClass.groupings, //
				"quality", Float.toString(vnClass.quality));
		return element;
	}

	/**
	 * Make FnRole roles node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @return newly created node
	 */
	static public Node makeVnRolesNode(@NonNull final Document doc, final Node parent)
	{
		return NodeFactory.makeNode(doc, parent, "themroles", null);
	}

	/**
	 * Make FnRole role node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @param role   is the role
	 * @param i      is the indexOf
	 * @return newly created node
	 */
	@SuppressWarnings("UnusedReturnValue")
	static public Node makeVnRoleNode(@NonNull final Document doc, final Node parent, @NonNull final VnRole role, final int i)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "themrole", null);
		NodeFactory.makeAttribute(element, "type", role.roleType);
		NodeFactory.makeAttribute(element, "id", Long.toString(i));
		if (role.selectionRestrictions != null)
		{
			final Element restrsElement = NodeFactory.makeNode(doc, element, "restrs", null);
			restrsElement.setAttribute("value", role.selectionRestrictions);
			// restrsElement.setTextContent(role.selectionRestrictions);
		}
		return element;
	}

	/**
	 * Make FnRole frames node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @return newly created node
	 */
	static public Node makeVnFramesNode(@NonNull final Document doc, final Node parent)
	{
		return NodeFactory.makeNode(doc, parent, "frames", null);
	}

	/**
	 * Make FnFrameElement frame node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @param frame  is the frame
	 * @param i      is the rank
	 * @return newly created node
	 */
	@SuppressWarnings("UnusedReturnValue")
	static public Node makeVnFrameNode(@NonNull final Document doc, final Node parent, @NonNull final VnFrame frame, final int i)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "frame", null);
		NodeFactory.makeAttribute(element, "id", Integer.toString(i));
		NodeFactory.makeAttribute(element, "description", frame.description1 + " - " + frame.description2);
		final Element descriptionElement = NodeFactory.makeNode(doc, element, "description", null);
		NodeFactory.makeAttribute(descriptionElement, "descriptionNumber", frame.number);
		NodeFactory.makeAttribute(descriptionElement, "xtag", frame.xTag);
		NodeFactory.makeAttribute(descriptionElement, "primary", frame.description1);
		NodeFactory.makeAttribute(descriptionElement, "secondary", frame.description2);
		// Element syntaxElement = makeNode(doc, element, "syntax", null);
		// syntaxElement.appendChild(doc.createTextNode(frame.getSyntax()));
		// Element semanticsElement = makeNode(doc, element, "semantics", null);
		// semanticsElement.appendChild(doc.createTextNode(frame.getSemantics()));

		final Element syntaxElement = NodeFactory.makeNode(doc, element, "syntax", null);
		final String syntaxConcat = frame.getSyntax();
		final String[] syntaxItems = syntaxConcat.split("\n");
		for (final String syntaxItem : syntaxItems)
		{
			final Element syntaxItemElement = NodeFactory.makeNode(doc, syntaxElement, "synitem", null);
			VnNodeFactory.makeVnSyntaxNodes(doc, syntaxItemElement, syntaxItem);
			// syntaxItemElement.setTextContent(syntaxItem);
		}

		final Element semanticsElement = NodeFactory.makeNode(doc, element, "semantics", null);
		final String semanticsConcat = frame.getSemantics();
		final String[] semanticsItems = semanticsConcat.split("\n");
		for (final String semanticItem : semanticsItems)
		{
			final Element semanticItemElement = NodeFactory.makeNode(doc, semanticsElement, "semitem", null);
			VnNodeFactory.makeVnSemanticNodes(doc, semanticItemElement, semanticItem);
			// semanticItemElement.setTextContent(semanticItem);
		}

		final Element examplesElement = NodeFactory.makeNode(doc, element, "examples", null);
		for (final String example : frame.examples)
		{
			final Element exampleElement = NodeFactory.makeNode(doc, examplesElement, "example", null);
			exampleElement.appendChild(doc.createTextNode(example));
		}
		return element;
	}

	/**
	 * Parse text into segments
	 *
	 * @param text    input text
	 * @param pattern delimiter pattern
	 * @return segments
	 */
	@Nullable
	static private String[] parse(@NonNull final CharSequence text, @NonNull final Pattern pattern)
	{
		// general pattern
		final Matcher matcher = pattern.matcher(text);
		if (matcher.find())
		{
			final int n = matcher.groupCount();
			final String[] fields = new String[n];

			for (int i = 1; i <= n; i++)
			{
				// final String group = matcher.group(i);
				// final int start = matcher.start(i);
				// final int end = matcher.end(i);
				fields[i - 1] = matcher.group(i);
			}
			return fields;
		}
		return null;
	}

	/**
	 * Syntax pattern
	 */
	static private final Pattern syntaxPattern = Pattern.compile("^([^\\s]+) ?(\\p{Upper}[\\p{Lower}_\\p{Upper}]*)? ?(.+)?");

	/**
	 * Make syntax nodes
	 *
	 * @param doc       doc
	 * @param parent    parent node
	 * @param statement statement
	 * @return node
	 */
	@SuppressWarnings("UnusedReturnValue")
	static private Node makeVnSyntaxNodes(@NonNull final Document doc, final Node parent, @NonNull final CharSequence statement)
	{
		final String[] fields = VnNodeFactory.parse(statement, VnNodeFactory.syntaxPattern);
		if (fields != null && fields.length == 3)
		{
			final Element categoryElement = NodeFactory.makeNode(doc, parent, "cat", null);
			categoryElement.setAttribute("value", fields[0]);
			// categoryElement.setTextContent(fields[0]);

			if (fields[1] != null)
			{
				final Element valueElement = NodeFactory.makeNode(doc, parent, "value", null);
				valueElement.setAttribute("value", fields[1]);
				// valueElement.setTextContent(fields[1]);
			}

			if (fields[2] != null)
			{
				final Element restrsElement = NodeFactory.makeNode(doc, parent, "restrs", null);
				restrsElement.setAttribute("value", fields[2]);
				// restrsElement.setTextContent(fields[2]);
			}
		}
		return parent;
	}

	/**
	 * Semantics pattern
	 */
	static private final Pattern semanticsPattern = Pattern.compile("([^(]+)\\((.*)\\)");

	/**
	 * Arguments pattern
	 */
	static private final String argsPattern = "[\\s,]+";

	/**
	 * Make semantics nodes
	 *
	 * @param doc       doc
	 * @param parent    parent node
	 * @param statement statement
	 * @return node
	 */
	@SuppressWarnings("UnusedReturnValue")
	static private Node makeVnSemanticNodes(@NonNull final Document doc, final Node parent, @NonNull final CharSequence statement)
	{
		final String[] relArgs = VnNodeFactory.parse(statement, VnNodeFactory.semanticsPattern);
		if (relArgs != null && relArgs.length == 2)
		{
			final Element relElement = NodeFactory.makeNode(doc, parent, "rel", null);
			relElement.setAttribute("value", relArgs[0]);
			final String[] args = relArgs[1].split(VnNodeFactory.argsPattern);
			int i = 1;
			for (final String arg : args)
			{
				if (arg == null)
				{
					continue;
				}
				final Element argElement = NodeFactory.makeNode(doc, parent, "arg", null);
				argElement.setAttribute("value", arg);
				argElement.setAttribute("argtype", Integer.toString(i++));
				// argElement.setTextContent(arg);
			}
		}
		return parent;
	}

	/**
	 * Make synset node with a flag
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @param size   is the synset's size (the number of words in the synset)
	 * @param id     is the synset's id in the database
	 * @param flag   is the synset's flag
	 * @return newly created node
	 */
	static public Node makeSynsetNodeFlagged(@NonNull final Document doc, final Node parent, final int size, final long id, final boolean flag)
	{
		final Element element = NodeFactory.makeSynsetNode(doc, parent, id, size);
		if (flag)
		{
			NodeFactory.makeAttribute(element, "flagged", "true");
		}
		return element;
	}
}