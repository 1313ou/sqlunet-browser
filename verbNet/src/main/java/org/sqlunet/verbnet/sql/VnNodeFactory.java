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
	 * @param doc     is the DOM Document being built
	 * @param classId target class id
	 * @return newly created node
	 */
	public static Node makeVnRootClassNode(final Document doc, final long classId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "verbnet", null, VerbNetImplementation.VN_NS);
		NodeFactory.addAttributes(rootNode, "classid", Long.toString(classId));
		return rootNode;
	}

	/**
	 * Make the class membership node
	 *
	 * @param doc             is the DOM Document being built
	 * @param parent          is the parent node to attach this node to
	 * @param classMembership is the class membership information
	 */
	public static Node makeVnClassMembershipNode(final Document doc, final Node parent, final VnClassSenseMap classMembership)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "vnclass", null);
		NodeFactory.addAttributes(element, //
				"name", classMembership.className, //
				"classid", Long.toString(classMembership.classId), //
				"synsetid", Long.toString(classMembership.synsetId), //
				"sensenum", Integer.toString(classMembership.senseNum), //
				"sensekey", classMembership.senseKey, //
				"groupings", classMembership.groupings, //
				"quality", Float.toString(classMembership.quality));
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
	static private Node makeVnClassNode(final Document doc, final Node parent, final String className)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "vnclass", null);
		NodeFactory.makeAttribute(element, "name", className);
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
		return NodeFactory.makeNode(doc, parent, "themroles", null);
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
	static public Node makeVnFramesNode(final Document doc, final Node parent)
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
	static public Node makeVnFrameNode(final Document doc, final Node parent, final VnFrame frame, final int i)
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
	static private String[] parse(final CharSequence text, final Pattern pattern)
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
	static private final Pattern syntaxPattern = Pattern.compile("^([^\\s]+) ?(\\p{Upper}\\p{Lower}*)? ?(.+)?");

	/**
	 * Make syntax nodes
	 *
	 * @param doc       doc
	 * @param parent    parent node
	 * @param statement statement
	 * @return node
	 */
	static private Node makeVnSyntaxNodes(final Document doc, final Node parent, final CharSequence statement)
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
	static private final Pattern semanticsPattern = Pattern.compile("([^\\(]+)\\((.*)\\)");
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
	static private Node makeVnSemanticNodes(final Document doc, final Node parent, final CharSequence statement)
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
				argElement.setAttribute("narg", Integer.toString(i++));
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
			NodeFactory.makeAttribute(element, "flagged", "true");
		}
		return element;
	}
}