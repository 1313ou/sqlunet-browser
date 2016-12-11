package org.sqlunet.view;

import android.content.Context;

import org.sqlunet.treeview.control.NodeController;
import org.sqlunet.treeview.control.LeafController;
import org.sqlunet.treeview.control.Link;
import org.sqlunet.treeview.control.LinkLeafController;
import org.sqlunet.treeview.control.LinkNodeController;
import org.sqlunet.treeview.control.LinkQueryController;
import org.sqlunet.treeview.control.LinkTreeController;
import org.sqlunet.treeview.control.Query;
import org.sqlunet.treeview.control.QueryController;
import org.sqlunet.treeview.control.TextController;
import org.sqlunet.treeview.control.TreeController;
import org.sqlunet.treeview.control.Value;
import org.sqlunet.treeview.model.TreeNode;

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
	 * @param text    text
	 * @param context context
	 * @return created node
	 */
	static public TreeNode newTextNode(final CharSequence text, final Context context)
	{
		return new TreeNode(text).setController(new TextController(context));
	}

	/**
	 * Make icon-text node
	 *
	 * @param text    text
	 * @param icon    icon
	 * @param context context
	 * @return created node
	 */
	static public TreeNode newNode(final CharSequence text, final int icon, final Context context)
	{
		return new TreeNode(new Value(text, icon), false).setController(new NodeController(context));
	}

	/**
	 * Make icon-text-link node
	 *
	 * @param text    text
	 * @param icon    icon
	 * @param link    link
	 * @param context context
	 * @return created node
	 */
	static public TreeNode newLinkNode(final CharSequence text, final int icon, final Link link, final Context context)
	{
		return new TreeNode(new Value(text, icon, link), false).setController(new LinkNodeController(context));
	}

	// TREE

	/**
	 * Make leaf node
	 *
	 * @param text    text
	 * @param icon    icon (extra icon after tree icon)
	 * @param context context
	 * @return created node
	 */
	static public TreeNode newLeafNode(final CharSequence text, final int icon, final Context context)
	{
		return new TreeNode(new Value(text, icon), false).setController(new LeafController(context));
	}

	/**
	 * Make link leaf node
	 *
	 * @param text    text
	 * @param icon    icon
	 * @param link    link
	 * @param context context
	 * @return created node
	 */
	static public TreeNode newLinkLeafNode(final CharSequence text, final int icon, final Link link, final Context context)
	{
		return new TreeNode(new Value(text, icon, link)).setController(new LinkLeafController(context));
	}

	/**
	 * Make tree node
	 *
	 * @param text    text
	 * @param icon    icon
	 * @param context context
	 * @return created node
	 */
	static public TreeNode newTreeNode(final CharSequence text, final int icon, final Context context)
	{
		return new TreeNode(new Value(text, icon)).setController(new TreeController(context));
	}

	/**
	 * Make link tree node
	 *
	 * @param text    text
	 * @param icon    icon
	 * @param link    link
	 * @param context context
	 * @return created node
	 */
	static public TreeNode newLinkTreeNode(final CharSequence text, final int icon, final Link link, final Context context)
	{
		return new TreeNode(new Value(text, icon, link)).setController(new LinkTreeController(context));
	}

	/**
	 * Make query node
	 *
	 * @param text       label text
	 * @param icon       icon
	 * @param query      query
	 * @param triggerNow whether to trigger query immediately
	 * @param context    context
	 * @return created node
	 */
	static public TreeNode newQueryNode(final CharSequence text, final int icon, final Query query, final boolean triggerNow, final Context context)
	{
		return new TreeNode(new Value(text, icon, query)).setController(new QueryController(context, triggerNow));
	}

	/**
	 * Make query node
	 *
	 * @param text       label text
	 * @param icon       icon
	 * @param query      query
	 * @param link       link
	 * @param triggerNow whether to trigger query immediately
	 * @param context    context
	 * @return created node
	 */
	static public TreeNode newLinkQueryNode(final CharSequence text, final int icon, final Query query, final Link link, final boolean triggerNow, final Context context)
	{
		return new TreeNode(new Value(text, icon, query, link)).setController(new LinkQueryController(context, triggerNow));
	}

	/**
	 * Add text node(s)
	 *
	 * @param parent  parent node
	 * @param value   character sequence
	 * @param context context
	 */
	static public TreeNode addTextNode(final TreeNode parent, final CharSequence value, final Context context)
	{
		final TreeNode result = TreeFactory.newTextNode(value, context);
		parent.addChild(result);
		return result;
	}

	/**
	 * Add leaf node(s)
	 *
	 * @param parent  parent node
	 * @param value   character sequence
	 * @param icon    icon resource id
	 * @param context context
	 */
	@SuppressWarnings("unused")
	static public TreeNode addLeafNode(final TreeNode parent, final CharSequence value, final int icon, final Context context)
	{
		final TreeNode result = TreeFactory.newLeafNode(value, icon, context);
		parent.addChild(result);
		return result;
	}

	/**
	 * Add tree node(s)
	 *
	 * @param parent  parent node
	 * @param value   character sequence
	 * @param icon    icon resource id
	 * @param context context
	 */
	static public TreeNode addTreeNode(final TreeNode parent, final CharSequence value, final int icon, final Context context)
	{
		final TreeNode result = TreeFactory.newTreeNode(value, icon, context);
		parent.addChild(result);
		return result;
	}
}
