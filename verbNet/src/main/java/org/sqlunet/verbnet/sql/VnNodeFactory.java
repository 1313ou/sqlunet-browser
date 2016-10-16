/*
 * @author Bernard Bou
 * Created on 31 dec. 2004
 * Filename : FnNodeFactory.java
 * Class encapsulates creation of DOM nodes
 */
package org.sqlunet.verbnet.sql;

import org.sqlunet.wordnet.sql.NodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DOM node factory
 *
 * @author Bernard Bou
 */
class VnNodeFactory extends NodeFactory
{
	/**
	 * Make VerbNet root node
	 *
	 * @param thisDoc      is the DOM Document being built
	 * @param thisWordId   is the target word id
	 * @param thisSynsetId is the target synset id (0 for all)
	 * @return newly created node
	 */
	static public Node makeVnRootNode(final Document thisDoc, final long thisWordId, final long thisSynsetId)
	{
		final Element thisRootNode = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisDoc, "verbnet", null); //$NON-NLS-1$
		if (thisSynsetId == 0)
		{
			org.sqlunet.sql.NodeFactory.makeTargetNode(thisDoc, thisRootNode, "word-id", Long.toString(thisWordId)); //$NON-NLS-1$
		} else
		{
			org.sqlunet.sql.NodeFactory.makeTargetNode(thisDoc, thisRootNode, "word-id", Long.toString(thisWordId), "synset-id", Long.toString(thisSynsetId)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return thisRootNode;
	}

	/**
	 * Make VerbNet root node
	 *
	 * @param thisDoc     is the DOM Document being built
	 * @param thisClassId is the target class id
	 * @return newly created node
	 */
	public static Node makeVnRootClassNode(final Document thisDoc, final long thisClassId)
	{
		final Element thisRootNode = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisDoc, "verbnet", null); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeTargetNode(thisDoc, thisRootNode, "class-id", Long.toString(thisClassId)); //$NON-NLS-1$
		return thisRootNode;
	}

	/**
	 * Make the class membership node
	 *
	 * @param thisDoc             is the DOM Document being built
	 * @param thisParent          is the parent node to attach this node to
	 * @param thisClassMembership is the class membership information
	 */
	public static Node makeVnClassMembershipNode(final Document thisDoc, final Node thisParent, final VnClassMembership thisClassMembership)
	{
		final Element thisElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "vnclass", null); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "name", thisClassMembership.className); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "classid", Long.toString(thisClassMembership.classId)); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "synsetid", Long.toString(thisClassMembership.synsetId)); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "sensenum", Integer.toString(thisClassMembership.senseNum)); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "sensekey", thisClassMembership.senseKey); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "groupings", thisClassMembership.groupings); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "quality", Integer.toString(thisClassMembership.quality)); //$NON-NLS-1$
		return thisElement;
	}

	/**
	 * Make VerbNet class node
	 *
	 * @param thisDoc       is the DOM Document being built
	 * @param thisParent    is the parent node to attach this node to
	 * @param thisClassName is the class
	 * @return newly created node
	 */
	private static Node makeVnClassNode(final Document thisDoc, final Node thisParent, final String thisClassName)
	{
		final Element thisElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "vnclass", null); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "name", thisClassName); //$NON-NLS-1$
		return thisElement;
	}

	/**
	 * Make VerbNet class node
	 *
	 * @param thisDoc    is the DOM Document being built
	 * @param thisParent is the parent node to attach this node to
	 * @param thisClass  is the class
	 * @return newly created node
	 */
	public static Node makeVnClassNode(final Document thisDoc, final Node thisParent, final VnClass thisClass)
	{
		return VnNodeFactory.makeVnClassNode(thisDoc, thisParent, thisClass.className);
	}

	/**
	 * Make FnRole roles node
	 *
	 * @param thisDoc    is the DOM Document being built
	 * @param thisParent is the parent node to attach this node to
	 * @return newly created node
	 */
	static public Node makeVnRolesNode(final Document thisDoc, final Node thisParent)
	{
		return org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "themroles", null);
	}

	/**
	 * Make FnRole role node
	 *
	 * @param thisDoc    is the DOM Document being built
	 * @param thisParent is the parent node to attach this node to
	 * @param thisRole   is the role
	 * @param i          is the index
	 * @return newly created node
	 */
	@SuppressWarnings("UnusedReturnValue")
	static public Node makeVnRoleNode(final Document thisDoc, final Node thisParent, final VnRole thisRole, final int i)
	{
		final Element thisElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "themrole", null); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "type", thisRole.theRoleType); //$NON-NLS-1$
		// TODO NodeFactory.makeAttribute(thisElement, "synset", thisRole.isSynsetSpecific ? "true" : "false");
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "id", Long.toString(i)); //$NON-NLS-1$
		if (thisRole.theSelectionRestrictions != null)
		{
			final Element thisRestrsElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisElement, "restrs", null); //$NON-NLS-1$
			thisRestrsElement.setAttribute("value", thisRole.theSelectionRestrictions); //$NON-NLS-1$
			// thisRestrsElement.setTextContent(thisRole.theSelectionRestrictions);
		}
		return thisElement;
	}

	/**
	 * Make FnRole frames node
	 *
	 * @param thisDoc    is the DOM Document being built
	 * @param thisParent is the parent node to attach this node to
	 * @return newly created node
	 */
	static public Node makeVnFramesNode(final Document thisDoc, final Node thisParent)
	{
		return org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "frames", null);
	}

	/**
	 * Make FnFrameElement frame node
	 *
	 * @param thisDoc    is the DOM Document being built
	 * @param thisParent is the parent node to attach this node to
	 * @param thisFrame  is the frame
	 * @param i          is the rank
	 * @return newly created node
	 */
	@SuppressWarnings("UnusedReturnValue")
	static public Node makeVnFrameNode(final Document thisDoc, final Node thisParent, final VnFrame thisFrame, final int i)
	{
		final Element thisElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "frame", null); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "id", Integer.toString(i)); //$NON-NLS-1$
		// TODO NodeFactory.makeAttribute(thisElement, "synset", thisFrame.isSynsetSpecific ? "true" : "false");
		org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "description", thisFrame.description1 + " - " + thisFrame.description2); //$NON-NLS-1$ //$NON-NLS-2$

		final Element thisDescriptionElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisElement, "description", null); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisDescriptionElement, "descriptionNumber", thisFrame.number); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisDescriptionElement, "xtag", thisFrame.xTag); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisDescriptionElement, "primary", thisFrame.description1); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeAttribute(thisDescriptionElement, "secondary", thisFrame.description2); //$NON-NLS-1$

		// Element thisSyntaxElement = makeNode(thisDoc, thisElement, "syntax", null);
		// thisSyntaxElement.appendChild(thisDoc.createTextNode(thisFrame.getSyntax()));
		// Element thisSemanticsElement = makeNode(thisDoc, thisElement, "semantics", null);
		// thisSemanticsElement.appendChild(thisDoc.createTextNode(thisFrame.getSemantics()));

		final Element thisSyntaxElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisElement, "syntax", null); //$NON-NLS-1$
		final String thisSyntax = thisFrame.getSyntax();
		final String[] theseSyntaxItems = thisSyntax.split("\n"); //$NON-NLS-1$
		for (final String thisItem : theseSyntaxItems)
		{
			final Element thisItemElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisSyntaxElement, "synitem", null); //$NON-NLS-1$
			VnNodeFactory.makeVnSyntaxNodes(thisDoc, thisItemElement, thisItem);
			// thisItemElement.setTextContent(thisItem);
		}

		final Element thisSemanticsElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisElement, "semantics", null); //$NON-NLS-1$
		final String thisSemantics = thisFrame.getSemantics();
		final String[] theseSemanticsItems = thisSemantics.split("\n"); //$NON-NLS-1$
		for (final String thisItem : theseSemanticsItems)
		{
			final Element thisItemElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisSemanticsElement, "semitem", null); //$NON-NLS-1$
			VnNodeFactory.makeVnSemanticNodes(thisDoc, thisItemElement, thisItem);
			// thisItemElement.setTextContent(thisItem);
		}

		final Element thisExamplesElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisElement, "examples", null); //$NON-NLS-1$
		for (final String thisExample : thisFrame.examples)
		{
			final Element thisExampleElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisExamplesElement, "example", null); //$NON-NLS-1$
			thisExampleElement.appendChild(thisDoc.createTextNode(thisExample));
		}
		return thisElement;
	}

	private static String[] parse(final String thisText, final Pattern thisPattern)
	{
		// general pattern
		final Matcher matcher = thisPattern.matcher(thisText);
		if (matcher.find())
		{
			final int n = matcher.groupCount();
			final String[] theseFields = new String[n];

			for (int i = 1; i <= n; i++)
			{
				// final String group = matcher.group(i);
				// final int start = matcher.start(i);
				// final int end = matcher.end(i);
				theseFields[i - 1] = matcher.group(i);
			}
			return theseFields;
		}
		return null;
	}

	static private final Pattern syntaxPattern = Pattern.compile("^([^\\s]+) ?(\\p{Upper}\\p{Lower}*)? ?(.+)?"); //$NON-NLS-1$

	@SuppressWarnings("UnusedReturnValue")
	private static Node makeVnSyntaxNodes(final Document thisDoc, final Node thisParent, final String thisStatement)
	{
		final String[] theseFields = VnNodeFactory.parse(thisStatement, VnNodeFactory.syntaxPattern);
		if (theseFields != null && theseFields.length == 3)
		{
			final Element thisCategoryElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "cat", null); //$NON-NLS-1$
			thisCategoryElement.setAttribute("value", theseFields[0]); //$NON-NLS-1$
			// thisCategoryElement.setTextContent(theseFields[0]);

			if (theseFields[1] != null)
			{
				final Element thisValueElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "value", null); //$NON-NLS-1$
				thisValueElement.setAttribute("value", theseFields[1]); //$NON-NLS-1$
				// thisValueElement.setTextContent(theseFields[1]);
			}

			if (theseFields[2] != null)
			{
				final Element thisRestrsElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "restrs", null); //$NON-NLS-1$
				thisRestrsElement.setAttribute("value", theseFields[2]); //$NON-NLS-1$
				// thisRestrsElement.setTextContent(theseFields[2]);
			}
		}
		return thisParent;
	}

	private static final Pattern semanticsPattern = Pattern.compile("([^\\(]+)\\((.*)\\)"); //$NON-NLS-1$

	private static final String argsPattern = "[\\s,]+"; //$NON-NLS-1$

	@SuppressWarnings("UnusedReturnValue")
	private static Node makeVnSemanticNodes(final Document thisDoc, final Node thisParent, final String thisStatement)
	{
		final String[] thisRelArgs = VnNodeFactory.parse(thisStatement, VnNodeFactory.semanticsPattern);
		if (thisRelArgs != null && thisRelArgs.length == 2)
		{
			final Element thisRelElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "rel", null); //$NON-NLS-1$
			thisRelElement.setAttribute("value", thisRelArgs[0]); //$NON-NLS-1$

			final String[] theseArgs = thisRelArgs[1].split(VnNodeFactory.argsPattern);
			int i = 1;
			for (final String thisArg : theseArgs)
			{
				if (thisArg == null)
				{
					continue;
				}
				final Element thisArgElement = org.sqlunet.sql.NodeFactory.makeNode(thisDoc, thisParent, "arg", null); //$NON-NLS-1$
				thisArgElement.setAttribute("value", thisArg); //$NON-NLS-1$
				thisArgElement.setAttribute("narg", Integer.toString(i++)); //$NON-NLS-1$
				// thisArgElement.setTextContent(thisArg);
			}
		}
		return thisParent;
	}

	/**
	 * Make synset node with a flag
	 *
	 * @param thisDoc    is the DOM Document being built
	 * @param thisParent is the parent node to attach this node to
	 * @param thisSize   is the synset's size (the number of words in the synset)
	 * @param thisId     is the synset's id in the database
	 * @param thisFlag   is the synset's flag
	 * @return newly created node
	 */
	@SuppressWarnings("unused")
	static public Node makeSynsetNodeFlagged(final Document thisDoc, final Node thisParent, final int thisSize, final long thisId, final boolean thisFlag)
	{
		final Element thisElement = NodeFactory.makeSynsetNode(thisDoc, thisParent, thisSize, thisId);
		if (thisFlag)
		{
			org.sqlunet.sql.NodeFactory.makeAttribute(thisElement, "flagged", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return thisElement;
	}
}