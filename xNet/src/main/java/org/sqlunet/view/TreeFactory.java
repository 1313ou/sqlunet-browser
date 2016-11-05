package org.sqlunet.view;

import android.content.Context;

import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.renderer.IconLeafRenderer;
import org.sqlunet.treeview.renderer.IconTreeRenderer;
import org.sqlunet.treeview.renderer.LinkRenderer;
import org.sqlunet.treeview.renderer.LinkRenderer.LinkData;
import org.sqlunet.treeview.renderer.QueryRenderer;
import org.sqlunet.treeview.renderer.TextRenderer;
import org.sqlunet.treeview.renderer.Value;

/**
 * Tree factory
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TreeFactory
{
	/**
	 * Make text node
	 *
	 * @param text    text
	 * @param context context
	 * @return created node
	 */
	static public TreeNode newTextNode(final CharSequence text, final Context context)
	{
		return new TreeNode(text).setRenderer(new TextRenderer(context));
	}

	/**
	 * Make link node
	 *
	 * @param link    link
	 * @param context context
	 * @return created node
	 */
	static public TreeNode newLinkNode(final LinkData link, final Context context)
	{
		return new TreeNode(link).setRenderer(new LinkRenderer(context));
	}

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
		return new TreeNode(new Value(icon, text)).setRenderer(new IconLeafRenderer(context));
	}

	/**
	 * Make tree node
	 *
	 * @param text    text
	 * @param icon    icon (extra icon after tree icon)
	 * @param context context
	 * @return created node
	 */
	static public TreeNode newTreeNode(final CharSequence text, final int icon, final Context context)
	{
		return new TreeNode(new Value(icon, text)).setRenderer(new IconTreeRenderer(context));
	}

	/**
	 * Make query node
	 *
	 * @param query      query
	 * @param triggerNow whether to triggger query immediately
	 * @param context    context
	 * @return created node
	 */
	static public TreeNode newQueryNode(final QueryRenderer.Query query, final boolean triggerNow, final Context context)
	{
		return new TreeNode(query).setRenderer(new QueryRenderer(context, triggerNow));
	}

	/**
	 * Add text node(s)
	 *
	 * @param parent   parent node
	 * @param value    character sequence
	 * @param context  context
	 * @param siblings sibling nodes to add
	 */
	static public TreeNode addTextNode(final TreeNode parent, final CharSequence value, final Context context, final TreeNode... siblings)
	{
		final TreeNode result = TreeFactory.newTextNode(value, context);
		parent.addChild(result);
		parent.addChildren(siblings);
		return result;
	}

	/**
	 * Add leaf node(s)
	 *
	 * @param parent   parent node
	 * @param value    character sequence
	 * @param icon     icon resource id
	 * @param context  context
	 * @param siblings sibling nodes to add
	 */
	@SuppressWarnings("unused")
	static public TreeNode addLeafNode(final TreeNode parent, final CharSequence value, final int icon, final Context context, final TreeNode... siblings)
	{
		final TreeNode result = TreeFactory.newLeafNode(value, icon, context);
		parent.addChild(result);
		parent.addChildren(siblings);
		return result;
	}

	/**
	 * Add tree node(s)
	 *
	 * @param parent   parent node
	 * @param value    character sequence
	 * @param icon     icon resource id
	 * @param context  context
	 * @param siblings sibling nodes to add
	 */
	static public TreeNode addTreeNode(final TreeNode parent, final CharSequence value, final int icon, final Context context, final TreeNode... siblings)
	{
		final TreeNode result = TreeFactory.newTreeNode(value, icon, context);
		parent.addChild(result);
		parent.addChildren(siblings);
		return result;
	}
}
