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
	 * @param doc    is the DOM Document being built
	 * @param wordId is the target word id
	 * @return newly created node
	 */
	static public Node makePbRootNode(final Document doc, final long wordId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "propbank", null); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeTargetNode(doc, rootNode, "word-id", Long.toString(wordId)); //$NON-NLS-1$
		return rootNode;
	}

	/**
	 * Make PropBank root node
	 *
	 * @param doc       is the DOM Document being built
	 * @param roleSetId is the target roleset id
	 * @return newly created node
	 */
	public static Node makePbRootRoleSetNode(final Document doc, long roleSetId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "propbank", null); //$NON-NLS-1$
		org.sqlunet.sql.NodeFactory.makeTargetNode(doc, rootNode, "roleset-id", Long.toString(roleSetId)); //$NON-NLS-1$
		return rootNode;
	}

	/**
	 * Make the roleset node
	 *
	 * @param doc     is the DOM Document being built
	 * @param parent  is the parent node to attach this node to
	 * @param roleSet is the roleset information
	 * @param i       the ith roleset
	 */
	public static Node makePbRoleSetNode(final Document doc, final Node parent, final PbRoleSet roleSet, final int i)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "roleset", null); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "num", Integer.toString(i)); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "name", roleSet.roleSetName); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "rolesetid", Long.toString(roleSet.roleSetId)); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "head", roleSet.roleSetHead); //$NON-NLS-1$
		NodeFactory.makeText(doc, element, roleSet.roleSetDescr);
		return element;
	}

	/**
	 * Make the roleset node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @param role   is the role information
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static Node makePbRoleNode(final Document doc, final Node parent, final PbRole role)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "role", null); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "roleid", Long.toString(role.roleId)); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "narg", role.nArg); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "theta", role.roleTheta); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "func", role.roleFunc); //$NON-NLS-1$
		NodeFactory.makeText(doc, element, role.roleDescr);
		return element;
	}

	@SuppressWarnings("UnusedReturnValue")
	public static Node makePbExampleNode(final Document doc, final Node parent, final PbExample example)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "example", null); //$NON-NLS-1$
		NodeFactory.makeAttribute(element, "exampleid", Long.toString(example.exampleId)); //$NON-NLS-1$
		if (example.aspect != null)
		{
			NodeFactory.makeAttribute(element, "aspect", example.aspect); //$NON-NLS-1$
		}
		if (example.form != null)
		{
			NodeFactory.makeAttribute(element, "form", example.form); //$NON-NLS-1$
		}
		if (example.tense != null)
		{
			NodeFactory.makeAttribute(element, "tense", example.tense); //$NON-NLS-1$
		}
		if (example.voice != null)
		{
			NodeFactory.makeAttribute(element, "voice", example.voice); //$NON-NLS-1$
		}
		if (example.person != null)
		{
			NodeFactory.makeAttribute(element, "person", example.person); //$NON-NLS-1$
		}

		NodeFactory.makeText(doc, element, example.text);
		NodeFactory.makeNode(doc, element, "rel", example.rel); //$NON-NLS-1$
		if (example.args != null)
		{
			for (final PbArg arg : example.args)
			{
				final Element element3 = NodeFactory.makeNode(doc, element, "arg", null); //$NON-NLS-1$
				NodeFactory.makeAttribute(element3, "narg", arg.nArg); //$NON-NLS-1$
				if (arg.f != null)
				{
					NodeFactory.makeAttribute(element3, "f", arg.f); //$NON-NLS-1$
				}
				if (arg.description != null)
				{
					NodeFactory.makeAttribute(element3, "descr", arg.description); //$NON-NLS-1$
				}
				if (arg.vnTheta != null)
				{
					NodeFactory.makeAttribute(element3, "theta", arg.vnTheta); //$NON-NLS-1$
				}
				NodeFactory.makeText(doc, element3, arg.subText);
			}
		}
		return element;
	}
}