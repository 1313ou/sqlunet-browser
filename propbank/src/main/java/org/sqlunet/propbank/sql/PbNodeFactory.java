package org.sqlunet.propbank.sql;

import android.support.annotation.NonNull;

import org.sqlunet.sql.NodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOM node factory
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class PbNodeFactory extends NodeFactory
{
	/**
	 * Make PropBank root node
	 *
	 * @param doc    is the DOM Document being built
	 * @param wordId target word id
	 * @return newly created node
	 */
	static public Node makePbRootNode(@NonNull final Document doc, final long wordId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "propbank", null, PropBankImplementation.PB_NS);
		NodeFactory.addAttributes(rootNode, "wordid", Long.toString(wordId));
		return rootNode;
	}

	/**
	 * Make PropBank root node
	 *
	 * @param doc  is the DOM Document being built
	 * @param word target word
	 * @return newly created node
	 */
	static public Node makePbRootNode(@NonNull final Document doc, final String word)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "propbank", null, PropBankImplementation.PB_NS);
		NodeFactory.addAttributes(rootNode, "word", word);
		return rootNode;
	}

	/**
	 * Make PropBank root node
	 *
	 * @param doc       is the DOM Document being built
	 * @param roleSetId target roleSet id
	 * @return newly created node
	 */
	static public Node makePbRootRoleSetNode(@NonNull final Document doc, long roleSetId)
	{
		final Element rootNode = NodeFactory.makeNode(doc, doc, "propbank", null, PropBankImplementation.PB_NS);
		NodeFactory.addAttributes(rootNode, "rolesetid", Long.toString(roleSetId));
		return rootNode;
	}

	/**
	 * Make the roleSet node
	 *
	 * @param doc     is the DOM Document being built
	 * @param parent  is the parent node to attach this node to
	 * @param roleSet is the roleSet information
	 * @param i       the ith roleSet
	 */
	static public Node makePbRoleSetNode(@NonNull final Document doc, final Node parent, @NonNull final PbRoleSet roleSet, final int i)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "roleset", null);
		NodeFactory.makeAttribute(element, "num", Integer.toString(i));
		NodeFactory.makeAttribute(element, "name", roleSet.roleSetName);
		NodeFactory.makeAttribute(element, "rolesetid", Long.toString(roleSet.roleSetId));
		NodeFactory.makeAttribute(element, "head", roleSet.roleSetHead);
		if (roleSet.wordId != 0)
		{
			NodeFactory.makeAttribute(element, "wordid", Long.toString(roleSet.wordId));
		}
		NodeFactory.makeText(doc, element, roleSet.roleSetDescr);
		return element;
	}

	/**
	 * Make the roleSet node
	 *
	 * @param doc    is the DOM Document being built
	 * @param parent is the parent node to attach this node to
	 * @param role   is the role information
	 */
	@SuppressWarnings("UnusedReturnValue")
	static public Node makePbRoleNode(@NonNull final Document doc, final Node parent, @NonNull final PbRole role)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "role", null);
		NodeFactory.makeAttribute(element, "roleid", Long.toString(role.roleId));
		NodeFactory.makeAttribute(element, "narg", role.nArg);
		NodeFactory.makeAttribute(element, "theta", role.roleTheta);
		NodeFactory.makeAttribute(element, "func", role.roleFunc);
		NodeFactory.makeText(doc, element, role.roleDescr);
		return element;
	}

	@SuppressWarnings("UnusedReturnValue")
	static public Node makePbExampleNode(@NonNull final Document doc, final Node parent, @NonNull final PbExample example)
	{
		final Element element = NodeFactory.makeNode(doc, parent, "example", null);
		NodeFactory.makeAttribute(element, "exampleid", Long.toString(example.exampleId));
		if (example.aspect != null)
		{
			NodeFactory.makeAttribute(element, "aspect", example.aspect);
		}
		if (example.form != null)
		{
			NodeFactory.makeAttribute(element, "form", example.form);
		}
		if (example.tense != null)
		{
			NodeFactory.makeAttribute(element, "tense", example.tense);
		}
		if (example.voice != null)
		{
			NodeFactory.makeAttribute(element, "voice", example.voice);
		}
		if (example.person != null)
		{
			NodeFactory.makeAttribute(element, "person", example.person);
		}

		NodeFactory.makeText(doc, element, example.text);
		NodeFactory.makeNode(doc, element, "rel", example.rel);
		if (example.args != null)
		{
			for (final PbArg arg : example.args)
			{
				final Element element3 = NodeFactory.makeNode(doc, element, "arg", null);
				NodeFactory.makeAttribute(element3, "narg", arg.nArg);
				if (arg.f != null)
				{
					NodeFactory.makeAttribute(element3, "f", arg.f);
				}
				if (arg.description != null)
				{
					NodeFactory.makeAttribute(element3, "descr", arg.description);
				}
				if (arg.vnTheta != null)
				{
					NodeFactory.makeAttribute(element3, "theta", arg.vnTheta);
				}
				NodeFactory.makeText(doc, element3, arg.subText);
			}
		}
		return element;
	}
}