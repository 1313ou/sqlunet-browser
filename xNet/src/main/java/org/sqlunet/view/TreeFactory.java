package org.sqlunet.view;

import android.content.Context;

import org.sqlunet.treeview.control.IconLeafController;
import org.sqlunet.treeview.control.TextController;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.control.IconTreeController;
import org.sqlunet.treeview.control.LinkController;
import org.sqlunet.treeview.control.LinkController.LinkData;
import org.sqlunet.treeview.control.QueryController;
import org.sqlunet.treeview.control.Value;

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
		return new TreeNode(text).setController(new TextController(context));
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
		return new TreeNode(link).setController(new LinkController(context));
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
		return new TreeNode(new Value(icon, text)).setController(new IconLeafController(context));
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
		return new TreeNode(new Value(icon, text)).setController(new IconTreeController(context));
	}

	/**
	 * Make query node
	 *
	 * @param query      query
	 * @param triggerNow whether to trigger query immediately
	 * @param context    context
	 * @return created node
	 */
	static public TreeNode newQueryNode(final QueryController.Query query, final boolean triggerNow, final Context context)
	{
		return new TreeNode(query).setController(new QueryController(context, triggerNow));
	}

	/**
	 * Add text node(s)
	 *
	 * @param parent   parent node
	 * @param value    character sequence
	 * @param context  context
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
	 * @param parent   parent node
	 * @param value    character sequence
	 * @param icon     icon resource id
	 * @param context  context
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
	 * @param parent   parent node
	 * @param value    character sequence
	 * @param icon     icon resource id
	 * @param context  context
	 */
	static public TreeNode addTreeNode(final TreeNode parent, final CharSequence value, final int icon, final Context context)
	{
		final TreeNode result = TreeFactory.newTreeNode(value, icon, context);
		parent.addChild(result);
		return result;
	}
}
