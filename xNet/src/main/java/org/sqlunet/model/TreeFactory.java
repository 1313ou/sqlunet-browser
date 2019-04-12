package org.sqlunet.model;

import android.content.Context;
import android.text.SpannableStringBuilder;

import org.sqlunet.treeview.control.HotQueryController;
import org.sqlunet.treeview.control.LeafController;
import org.sqlunet.treeview.control.Link;
import org.sqlunet.treeview.control.LinkLeafController;
import org.sqlunet.treeview.control.LinkNodeController;
import org.sqlunet.treeview.control.LinkQueryController;
import org.sqlunet.treeview.control.LinkTreeController;
import org.sqlunet.treeview.control.MoreController;
import org.sqlunet.treeview.control.NodeController;
import org.sqlunet.treeview.control.Query;
import org.sqlunet.treeview.control.QueryController;
import org.sqlunet.treeview.control.TextController;
import org.sqlunet.treeview.control.TreeController;
import org.sqlunet.treeview.control.Value;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.NonNull;

/**
 * Tree factory
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TreeFactory
{
	// NON-TREE (without tree junction icon)

	/**
	 * Make text node
	 *
	 * @param text text
	 * @return created node
	 */
	static public TreeNode newTextNode(final CharSequence text, final Context context)
	{
		return new TreeNode(text, new TextController(context));
	}

	/**
	 * Make icon-text node
	 *
	 * @param text text
	 * @param icon icon
	 * @return created node
	 */
	static public TreeNode newNode(@SuppressWarnings("SameParameterValue") final CharSequence text, final int icon, final Context context)
	{
		return new TreeNode(new Value(text, icon), new NodeController(context), false);
	}

	/**
	 * Make icon-text-link node
	 *
	 * @param text text
	 * @param icon icon
	 * @param link link
	 * @return created node
	 */
	static public TreeNode newLinkNode(final CharSequence text, final int icon, final Link link, final Context context)
	{
		return new TreeNode(new Value(text, icon, link), new LinkNodeController(context), false);
	}

	// TREE

	/**
	 * Make leaf node
	 *
	 * @param text text
	 * @param icon icon (extra icon after tree icon)
	 * @return created node
	 */
	static public TreeNode newLeafNode(final CharSequence text, final int icon, final Context context)
	{
		return new TreeNode(new Value(text, icon), new LeafController(context), false);
	}

	/**
	 * Make more (leaf) node
	 *
	 * @param text text
	 * @param icon icon (extra icon after tree icon)
	 * @return created node
	 */
	static public TreeNode newMoreNode(final CharSequence text, final int icon, final Context context)
	{
		return new TreeNode(new Value(text, icon), new MoreController(context), false);
	}

	/**
	 * Make link leaf node
	 *
	 * @param text text
	 * @param icon icon
	 * @param link link
	 * @return created node
	 */
	static public TreeNode newLinkLeafNode(final CharSequence text, final int icon, final Link link, final Context context)
	{
		return new TreeNode(new Value(text, icon, link), new LinkLeafController(context));
	}

	/**
	 * Make tree node
	 *
	 * @param text text
	 * @param icon icon
	 * @return created node
	 */
	static public TreeNode newTreeNode(final CharSequence text, final int icon, final Context context)
	{
		return new TreeNode(new Value(text, icon), new TreeController(context));
	}

	/**
	 * Make link tree node
	 *
	 * @param text text
	 * @param icon icon
	 * @param link link
	 * @return created node
	 */
	static public TreeNode newLinkTreeNode(final CharSequence text, final int icon, final Link link, final Context context)
	{
		return new TreeNode(new Value(text, icon, link), new LinkTreeController(context));
	}

	/**
	 * Make query node
	 *
	 * @param text  label text
	 * @param icon  icon
	 * @param query query
	 * @return created node
	 */
	static public TreeNode newQueryNode(final CharSequence text, final int icon, final Query query, final Context context)
	{
		return new TreeNode(new Value(text, icon, query), new QueryController(context));
	}

	/**
	 * Make query node
	 *
	 * @param text  label text
	 * @param icon  icon
	 * @param query query
	 * @return created node
	 */
	static public TreeNode newHotQueryNode(final CharSequence text, final int icon, final Query query, final Context context)
	{
		return new TreeNode(new Value(text, icon, query), new HotQueryController(context));
	}

	/**
	 * Make query node
	 *
	 * @param text  label text
	 * @param icon  icon
	 * @param query query
	 * @param link  link
	 * @return created node
	 */
	static public TreeNode newLinkQueryNode(final CharSequence text, final int icon, final Query query, final Link link, final Context context)
	{
		return new TreeNode(new Value(text, icon, query, link), new LinkQueryController(context));
	}

	/**
	 * Add text node(s)
	 *
	 * @param parent parent node
	 * @param value  character sequence
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	static public TreeNode addTextNode(@NonNull final TreeNode parent, final CharSequence value, final Context context)
	{
		final TreeNode result = TreeFactory.newTextNode(value, context);
		parent.addChild(result);
		return result;
	}

	/**
	 * Add leaf node(s)
	 *
	 * @param parent parent node
	 * @param value  character sequence
	 * @param icon   icon resource id
	 */
	@NonNull
	@SuppressWarnings({"unused", "UnusedReturnValue"})
	static public TreeNode addLeafNode(@NonNull final TreeNode parent, final CharSequence value, final int icon, final Context context)
	{
		final TreeNode result = TreeFactory.newLeafNode(value, icon, context);
		parent.addChild(result);
		return result;
	}

	/**
	 * Add tree node(s)
	 *
	 * @param parent parent node
	 * @param value  character sequence
	 * @param icon   icon resource id
	 */
	@NonNull
	static public TreeNode addTreeNode(@NonNull final TreeNode parent, final CharSequence value, final int icon, final Context context)
	{
		final TreeNode result = TreeFactory.newTreeNode(value, icon, context);
		parent.addChild(result);
		return result;
	}

	/**
	 * No results have been attached to this node
	 *
	 * @param node       node
	 * @param addNewNode whether results were supposed to be new subnodes or replace query node
	 */
	static public void setNoResult(@NonNull final TreeNode node, boolean addNewNode)
	{
		if (addNewNode)
		{
			//TODO TreeView.disable(node);
			node.disable();
			node.setDeadend(true);
		}
		else
		{
			final TreeNode parent = node.getParent();
			parent.deleteChild(node);
		}
	}

	public static void setTextNode(final TreeNode node, final SpannableStringBuilder sb)
	{
		node.setValue(sb);
	}

	public static void setLevels(final TreeNode node, final int levels)
	{
		// TODO
	}
}
