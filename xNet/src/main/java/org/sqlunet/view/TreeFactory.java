package org.sqlunet.view;

import android.content.Context;
import android.widget.TextView;

import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.renderer.IconLeafRenderer;
import org.sqlunet.treeview.renderer.IconTreeItem;
import org.sqlunet.treeview.renderer.IconTreeRenderer;
import org.sqlunet.treeview.renderer.LinkHolder;
import org.sqlunet.treeview.renderer.LinkHolder.Link;
import org.sqlunet.treeview.renderer.QueryHolder;
import org.sqlunet.treeview.renderer.QueryHolder.Query;
import org.sqlunet.treeview.renderer.TextRenderer;

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
	static public TreeNode newLinkNode(final Link link, final Context context)
	{
		return new TreeNode(link).setRenderer(new LinkHolder(context));
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
		return new TreeNode(new IconTreeItem(icon, text)).setRenderer(new IconLeafRenderer(context));
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
		return new TreeNode(new IconTreeItem(icon, text)).setRenderer(new IconTreeRenderer(context));
	}

	/**
	 * Make query node
	 *
	 * @param query   query
	 * @param context context
	 * @return created node
	 */
	static public TreeNode newQueryNode(final Query query, final Context context)
	{
		return new TreeNode(query).setRenderer(new QueryHolder(context));
	}

	/**
	 * Add text node(s)
	 *
	 * @param parent   parent node
	 * @param value    character sequence
	 * @param context  context
	 * @param siblings sibling nodes to add
	 */
	@SuppressWarnings("UnusedReturnValue")
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
	static public TreeNode addTreeItemNode(final TreeNode parent, final CharSequence value, final int icon, final Context context, final TreeNode... siblings)
	{
		final TreeNode result = TreeFactory.newTreeNode(value, icon, context);
		parent.addChild(result);
		parent.addChildren(siblings);
		return result;
	}

	/**
	 * Set node text
	 *
	 * @param node  node
	 * @param value character sequence
	 */
	static public void setNodeValue(final TreeNode node, final CharSequence value)
	{
		// update view
		node.setValue(value);

		// update view
		final TextRenderer renderer = (TextRenderer) node.getRenderer();
		final TextView textView = (TextView) renderer.getNodeView();
		if (textView != null)
		{
			textView.setText(value);
		}
	}
}
