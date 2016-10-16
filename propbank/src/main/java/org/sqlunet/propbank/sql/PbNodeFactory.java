/*
 * @author Bernard Bou
 * Created on 31 dec. 2004
 * Filename : FnNodeFactory.java
 * Class encapsulates creation of DOM nodes
 */
package org.sqlunet.propbank.sql;

import org.sqlunet.sql.NodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOM node factory
 *
 * @author Bernard Bou
 */
class PbNodeFactory extends NodeFactory
{
	/**
	 * Make PropBank root node
	 *
	 * @param thisDoc
	 *            is the DOM Document being built
	 * @param thisWordId
	 *            is the target word id
	 * @return newly created node
	 */
	static public Node makePbRootNode(final Document thisDoc, final long thisWordId)
	{
		final Element thisRootNode = NodeFactory.makeNode(thisDoc, thisDoc, "propbank", null); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeTargetNode(thisDoc, thisRootNode, "word-id", Long.toString(thisWordId)); //$NON-NLS-1$
		return thisRootNode;
	}

	/**
	 * Make PropBank root node
	 *
	 * @param thisDoc
	 *            is the DOM Document being built
	 * @param thisRoleSetId
	 *            is the target roleset id
	 * @return newly created node
	 */
	public static Node makePbRootRoleSetNode(Document thisDoc, long thisRoleSetId)
	{
		final Element thisRootNode = NodeFactory.makeNode(thisDoc, thisDoc, "propbank", null); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeTargetNode(thisDoc, thisRootNode, "roleset-id", Long.toString(thisRoleSetId)); //$NON-NLS-1$
		return thisRootNode;
	}

	/**
	 * Make the roleset node
	 *
	 * @param thisDoc
	 *            is the DOM Document being built
	 * @param thisParent
	 *            is the parent node to attach this node to
	 * @param thisRoleSet
	 *            is the roleset information
	 * @param i
	 *            the ith roleset
	 */
	public static Node makePbRoleSetNode(final Document thisDoc, final Node thisParent, final PbRoleSet thisRoleSet, final int i)
	{
		final Element thisElement = NodeFactory.makeNode(thisDoc, thisParent, "roleset", null); //$NON-NLS-1$
		NodeFactory.makeAttribute(thisElement, "num", Integer.toString(i)); //$NON-NLS-1$
		NodeFactory.makeAttribute(thisElement, "name", thisRoleSet.theRoleSetName); //$NON-NLS-1$
		NodeFactory.makeAttribute(thisElement, "rolesetid", Long.toString(thisRoleSet.theRoleSetId)); //$NON-NLS-1$
		NodeFactory.makeAttribute(thisElement, "head", thisRoleSet.theRoleSetHead); //$NON-NLS-1$
		NodeFactory.makeText(thisDoc, thisElement, thisRoleSet.theRoleSetDescr);
		return thisElement;
	}

	/**
	 * Make the roleset node
	 *  @param thisDoc
	 *            is the DOM Document being built
	 * @param thisParent
	 *            is the parent node to attach this node to
	 * @param thisRole
 *            is the role information
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static Node makePbRoleNode(final Document thisDoc, final Node thisParent, final PbRole thisRole)
	{
		final Element thisElement = NodeFactory.makeNode(thisDoc, thisParent, "role", null); //$NON-NLS-1$
		NodeFactory.makeAttribute(thisElement, "roleid", Long.toString(thisRole.theRoleId)); //$NON-NLS-1$
		NodeFactory.makeAttribute(thisElement, "narg", thisRole.theNArg); //$NON-NLS-1$
		NodeFactory.makeAttribute(thisElement, "theta", thisRole.theRoleTheta); //$NON-NLS-1$
		NodeFactory.makeAttribute(thisElement, "func", thisRole.theRoleFunc); //$NON-NLS-1$
		NodeFactory.makeText(thisDoc, thisElement, thisRole.theRoleDescr);
		return thisElement;
	}

	@SuppressWarnings("UnusedReturnValue")
	public static Node makePbExampleNode(final Document thisDoc, final Node thisParent, final PbExample thisExample)
	{
		final Element thisElement = NodeFactory.makeNode(thisDoc, thisParent, "example", null); //$NON-NLS-1$
		NodeFactory.makeAttribute(thisElement, "exampleid", Long.toString(thisExample.theExampleId)); //$NON-NLS-1$
		if (thisExample.theAspect != null)
		{
			NodeFactory.makeAttribute(thisElement, "aspect", thisExample.theAspect); //$NON-NLS-1$
		}
		if (thisExample.theForm != null)
		{
			NodeFactory.makeAttribute(thisElement, "form", thisExample.theForm); //$NON-NLS-1$
		}
		if (thisExample.theTense != null)
		{
			NodeFactory.makeAttribute(thisElement, "tense", thisExample.theTense); //$NON-NLS-1$
		}
		if (thisExample.theVoice != null)
		{
			NodeFactory.makeAttribute(thisElement, "voice", thisExample.theVoice); //$NON-NLS-1$
		}
		if (thisExample.thePerson != null)
		{
			NodeFactory.makeAttribute(thisElement, "person", thisExample.thePerson); //$NON-NLS-1$
		}

		NodeFactory.makeText(thisDoc, thisElement, thisExample.theText);
		NodeFactory.makeNode(thisDoc, thisElement, "rel", thisExample.theRel.subText); //$NON-NLS-1$
		if (thisExample.theArgs != null)
		{
			for (final PbArg thisArg : thisExample.theArgs)
			{
				final Element thisElement3 = NodeFactory.makeNode(thisDoc, thisElement, "arg", null); //$NON-NLS-1$
				NodeFactory.makeAttribute(thisElement3, "narg", thisArg.theNArg); //$NON-NLS-1$
				if (thisArg.theF != null)
				{
					NodeFactory.makeAttribute(thisElement3, "f", thisArg.theF); //$NON-NLS-1$
				}
				if (thisArg.theDescription != null)
				{
					NodeFactory.makeAttribute(thisElement3, "descr", thisArg.theDescription); //$NON-NLS-1$
				}
				if (thisArg.theVnTheta != null)
				{
					NodeFactory.makeAttribute(thisElement3, "theta", thisArg.theVnTheta); //$NON-NLS-1$
				}
				NodeFactory.makeText(thisDoc, thisElement3, thisArg.theSubText);
			}
		}
		return thisElement;
	}
}