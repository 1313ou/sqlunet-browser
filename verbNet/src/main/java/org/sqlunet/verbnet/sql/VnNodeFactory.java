package org.sqlunet.verbnet.sql;

import org.sqlunet.wordnet.sql.NodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 * @param doc      is the DOM Document being built
	 * @param wordId   target word id
	 * @param synsetId target synset id (0 for all)
	 * @return newly created node
	 */
	static public Node makeVnRootNode(final Document doc, final long wordId, final Long synsetId)
	{
		final Element rootNode = org.sqlunet.sql.NodeFactory.makeNode(doc, doc, "verbnet", null); //
		if (synsetId == null || synsetId == 0)
		{
			org.sqlunet.sql.NodeFactory.makeTargetNode(doc, rootNode, "word-id", Long.toString(wordId)); //
		}
		else
		{
			org.sqlunet.sql.NodeFactory.makeTargetNode(doc, rootNode, "word-id", Long.toString(wordId), "synset-id", Long.toString(synsetId)); //
		}
		return rootNode;
	}

	/**
	 * Make VerbNet root node
	 *
	 * @param doc     is the DOM Document being built
	 * @param classId target class id
	 * @return newly created node
	 */
	public static Node makeVnRootClassNode(final Document doc, final long classId)
	{
		final Element rootNode = org.sqlunet.sql.NodeFactory.makeNode(doc, doc, "verbnet", null); //
		org.sqlunet.sql.NodeFactory.makeTargetNode(doc, rootNode, "class-id", Long.toString(classId)); //
		return rootNode;
	}

	/**
	 * Make the class membership node
	 *
	 * @param doc             is the DOM Document being built
	 * @param parent          is the parent node to attach this node to
	 * @param classMembership is the class membership information
	 */
	public static Node makeVnClassMembershipNode(final Document doc, final Node parent, final VnClassMembership classMembership)
	{
		final Element element = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "vnclass", null); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "name", classMembership.className); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "classid", Long.toString(classMembership.classId)); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "synsetid", Long.toString(classMembership.synsetId)); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "sensenum", Integer.toString(classMembership.senseNum)); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "sensekey", classMembership.senseKey); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "groupings", classMembership.groupings); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "quality", Integer.toString(classMembership.quality)); //
		return element;
	}

	/**
	 * Make VerbNet class node
	 *
	 * @param doc       is the DOM Document being built
	 * @param parent    is the parent node to attach this node to
	 * @param className is the class
	 * @return newly created node
	 */
	private static Node makeVnClassNode(final Document doc, final Node parent, final String className)
	{
		final Element element = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "vnclass", null); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "name", className); //
		return element;
	}

	/**
	 * Make VerbNet class node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @param clazz  is the class
	 * @return newly created node
	 */
	public static Node makeVnClassNode(final Document doc, final Node parent, final VnClass clazz)
	{
		return VnNodeFactory.makeVnClassNode(doc, parent, clazz.className);
	}

	/**
	 * Make FnRole roles node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @return newly created node
	 */
	static public Node makeVnRolesNode(final Document doc, final Node parent)
	{
		return org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "themroles", null);
	}

	/**
	 * Make FnRole role node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @param role   is the role
	 * @param i      is the index
	 * @return newly created node
	 */
	static public Node makeVnRoleNode(final Document doc, final Node parent, final VnRole role, final int i)
	{
		final Element element = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "themrole", null); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "type", role.roleType); //
		// TODO NodeFactory.makeAttribute(element, "synset", role.isSynsetSpecific ? "true" : "false");
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "id", Long.toString(i)); //
		if (role.selectionRestrictions != null)
		{
			final Element restrsElement = org.sqlunet.sql.NodeFactory.makeNode(doc, element, "restrs", null); //
			restrsElement.setAttribute("value", role.selectionRestrictions); //
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
	static public Node makeVnFramesNode(final Document doc, final Node parent)
	{
		return org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "frames", null);
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
	static public Node makeVnFrameNode(final Document doc, final Node parent, final VnFrame frame, final int i)
	{
		final Element element = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "frame", null); //
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "id", Integer.toString(i)); //
		// TODO NodeFactory.makeAttribute(element, "synset", frame.isSynsetSpecific ? "true" : "false");
		org.sqlunet.sql.NodeFactory.makeAttribute(element, "description", frame.description1 + " - " + frame.description2); //

		final Element descriptionElement = org.sqlunet.sql.NodeFactory.makeNode(doc, element, "description", null); //
		org.sqlunet.sql.NodeFactory.makeAttribute(descriptionElement, "descriptionNumber", frame.number); //
		org.sqlunet.sql.NodeFactory.makeAttribute(descriptionElement, "xtag", frame.xTag); //
		org.sqlunet.sql.NodeFactory.makeAttribute(descriptionElement, "primary", frame.description1); //
		org.sqlunet.sql.NodeFactory.makeAttribute(descriptionElement, "secondary", frame.description2); //

		// Element syntaxElement = makeNode(doc, element, "syntax", null);
		// syntaxElement.appendChild(doc.createTextNode(frame.getSyntax()));
		// Element semanticsElement = makeNode(doc, element, "semantics", null);
		// semanticsElement.appendChild(doc.createTextNode(frame.getSemantics()));

		final Element syntaxElement = org.sqlunet.sql.NodeFactory.makeNode(doc, element, "syntax", null); //
		final String syntaxConcat = frame.getSyntax();
		final String[] syntaxItems = syntaxConcat.split("\n"); //
		for (final String syntaxItem : syntaxItems)
		{
			final Element syntaxItemElement = org.sqlunet.sql.NodeFactory.makeNode(doc, syntaxElement, "synitem", null); //
			VnNodeFactory.makeVnSyntaxNodes(doc, syntaxItemElement, syntaxItem);
			// syntaxItemElement.setTextContent(syntaxItem);
		}

		final Element semanticsElement = org.sqlunet.sql.NodeFactory.makeNode(doc, element, "semantics", null); //
		final String semanticsConcat = frame.getSemantics();
		final String[] semanticsItems = semanticsConcat.split("\n"); //
		for (final String semanticItem : semanticsItems)
		{
			final Element semanticItemElement = org.sqlunet.sql.NodeFactory.makeNode(doc, semanticsElement, "semitem", null); //
			VnNodeFactory.makeVnSemanticNodes(doc, semanticItemElement, semanticItem);
			// semanticItemElement.setTextContent(semanticItem);
		}

		final Element examplesElement = org.sqlunet.sql.NodeFactory.makeNode(doc, element, "examples", null); //
		for (final String example : frame.examples)
		{
			final Element exampleElement = org.sqlunet.sql.NodeFactory.makeNode(doc, examplesElement, "example", null); //
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
	private static String[] parse(final CharSequence text, final Pattern pattern)
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
	static private final Pattern syntaxPattern = Pattern.compile("^([^\\s]+) ?(\\p{Upper}\\p{Lower}*)? ?(.+)?"); //

	/**
	 * Make syntax nodes
	 *
	 * @param doc       doc
	 * @param parent    parent node
	 * @param statement statement
	 * @return node
	 */
	private static Node makeVnSyntaxNodes(final Document doc, final Node parent, final CharSequence statement)
	{
		final String[] fields = VnNodeFactory.parse(statement, VnNodeFactory.syntaxPattern);
		if (fields != null && fields.length == 3)
		{
			final Element categoryElement = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "cat", null); //
			categoryElement.setAttribute("value", fields[0]); //
			// categoryElement.setTextContent(fields[0]);

			if (fields[1] != null)
			{
				final Element valueElement = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "value", null); //
				valueElement.setAttribute("value", fields[1]); //
				// valueElement.setTextContent(fields[1]);
			}

			if (fields[2] != null)
			{
				final Element restrsElement = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "restrs", null); //
				restrsElement.setAttribute("value", fields[2]); //
				// restrsElement.setTextContent(fields[2]);
			}
		}
		return parent;
	}

	/**
	 * Semantics pattern
	 */
	private static final Pattern semanticsPattern = Pattern.compile("([^\\(]+)\\((.*)\\)"); //

	/**
	 * Arguments pattern
	 */
	private static final String argsPattern = "[\\s,]+"; //

	/**
	 * Make semantics nodes
	 *
	 * @param doc       doc
	 * @param parent    parent node
	 * @param statement statement
	 * @return node
	 */
	private static Node makeVnSemanticNodes(final Document doc, final Node parent, final CharSequence statement)
	{
		final String[] relArgs = VnNodeFactory.parse(statement, VnNodeFactory.semanticsPattern);
		if (relArgs != null && relArgs.length == 2)
		{
			final Element relElement = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "rel", null); //
			relElement.setAttribute("value", relArgs[0]); //

			final String[] args = relArgs[1].split(VnNodeFactory.argsPattern);
			int i = 1;
			for (final String arg : args)
			{
				if (arg == null)
				{
					continue;
				}
				final Element argElement = org.sqlunet.sql.NodeFactory.makeNode(doc, parent, "arg", null); //
				argElement.setAttribute("value", arg); //
				argElement.setAttribute("narg", Integer.toString(i++)); //
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
	@SuppressWarnings("unused")
	static public Node makeSynsetNodeFlagged(final Document doc, final Node parent, final int size, final long id, final boolean flag)
	{
		final Element element = NodeFactory.makeSynsetNode(doc, parent, size, id);
		if (flag)
		{
			org.sqlunet.sql.NodeFactory.makeAttribute(element, "flagged", "true"); //
		}
		return element;
	}
}