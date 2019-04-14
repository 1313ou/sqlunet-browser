package org.sqlunet.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;

import org.sqlunet.treeview.control.HotQueryController;
import org.sqlunet.treeview.control.IconTextController;
import org.sqlunet.treeview.control.LeafController;
import org.sqlunet.treeview.control.Link;
import org.sqlunet.treeview.control.LinkLeafController;
import org.sqlunet.treeview.control.LinkNodeController;
import org.sqlunet.treeview.control.LinkQueryController;
import org.sqlunet.treeview.control.LinkTreeController;
import org.sqlunet.treeview.control.MoreController;
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
	 * Add icon-text-link node
	 *
	 * @param text text
	 * @param icon icon
	 * @param link link
	 * @return created node
	 */
	static public TreeNode addLinkNode(@NonNull final TreeNode parent, final CharSequence text, final int icon, final Link link, final Context context)
	{
		final TreeNode result = new TreeNode(new Value(text, icon, link), new LinkNodeController(context), false);
		parent.addChild(result);
		return result;
	}

	// TREE

	/**
	 * Add leaf node
	 *
	 * @param parent parent node
	 * @param text   text
	 * @param icon   icon (extra icon after tree icon)
	 * @return created node
	 */
	static public TreeNode addLeafNode(@NonNull final TreeNode parent, final CharSequence text, final int icon, final Context context)
	{
		final TreeNode result = new TreeNode(new Value(text, icon), new LeafController(context), false);
		parent.addChild(result);
		return result;
	}

	/**
	 * Make more (leaf) node
	 *
	 * @param parent parent node
	 * @param text   text
	 * @param icon   icon (extra icon after tree icon)
	 * @return created node
	 */
	static public TreeNode addMoreNode(@NonNull final TreeNode parent, final CharSequence text, final int icon, final Context context)
	{
		final TreeNode result = new TreeNode(new Value(text, icon), new MoreController(context), false);
		parent.addChild(result);
		return result;
	}

	/**
	 * Make link leaf node
	 *
	 * @param parent parent node
	 * @param text   text
	 * @param icon   icon
	 * @param link   link
	 * @return created node
	 */
	static public TreeNode addLinkLeafNode(@NonNull final TreeNode parent, final CharSequence text, final int icon, final Link link, final Context context)
	{
		final TreeNode result = new TreeNode(new Value(text, icon, link), new LinkLeafController(context));
		parent.addChild(result);
		return result;
	}

	/**
	 * Make link tree node
	 *
	 * @param parent parent node
	 * @param text   text
	 * @param icon   icon
	 * @param link   link
	 * @return created node
	 */
	static public TreeNode addLinkTreeNode(@NonNull final TreeNode parent, final CharSequence text, final int icon, final Link link, final Context context)
	{
		final TreeNode result = new TreeNode(new Value(text, icon, link), new LinkTreeController(context));
		parent.addChild(result);
		return result;
	}

	/**
	 * Make query node
	 *
	 * @param parent parent node
	 * @param text   label text
	 * @param icon   icon
	 * @param query  query
	 * @return created node
	 */
	static public TreeNode addQueryNode(@NonNull final TreeNode parent, final CharSequence text, final int icon, final Query query, final Context context)
	{
		final TreeNode result = new TreeNode(new Value(text, icon, query), new QueryController(context));
		parent.addChild(result);
		return result;
	}

	/**
	 * Make hot (self-triggered) query node
	 *
	 * @param parent parent node
	 * @param text   label text
	 * @param icon   icon
	 * @param query  query
	 * @return created node
	 */
	static public TreeNode addHotQueryNode(@NonNull final TreeNode parent, final CharSequence text, final int icon, final Query query, final Context context)
	{
		final HotQueryController controller = new HotQueryController(context);
		final TreeNode result = new TreeNode(new Value(text, icon, query), controller);
		parent.addChild(result);

		final Handler handler = new Handler(Looper.getMainLooper());
		handler.post(controller::processQuery);

		return result;
	}

	/**
	 * Make link query node
	 *
	 * @param parent parent node
	 * @param text   label text
	 * @param icon   icon
	 * @param query  query
	 * @param link   link
	 * @return created node
	 */
	static public TreeNode addLinkQueryNode(@NonNull final TreeNode parent, final CharSequence text, final int icon, final Query query, final Link link, final Context context)
	{
		final TreeNode result = new TreeNode(new Value(text, icon, query, link), new LinkQueryController(context));
		parent.addChild(result);
		return result;
	}

	/**
	 * Add text node(s)
	 *
	 * @param parent parent node
	 * @param value  character sequence
	 * @return created node
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	static public TreeNode addTextNode(@NonNull final TreeNode parent, final CharSequence value, final Context context)
	{
		final TreeNode result = new TreeNode(value, new TextController(context));
		parent.addChild(result);
		return result;
	}

	/**
	 * Make icon-text node
	 *
	 * @param parent parent node
	 * @param text   text
	 * @param icon   icon
	 * @return created node
	 */
	static public TreeNode addIconTextNode(@NonNull final TreeNode parent, @SuppressWarnings("SameParameterValue") final CharSequence text, final int icon, final Context context)
	{
		final TreeNode result = new TreeNode(new Value(text, icon), new IconTextController(context), false);
		parent.addChild(result);
		return result;
	}

	/**
	 * Add tree node(s)
	 *
	 * @param parent parent node
	 * @param value  character sequence
	 * @param icon   icon resource id
	 * @return created node
	 */
	@NonNull
	static public TreeNode addTreeNode(@NonNull final TreeNode parent, final CharSequence value, final int icon, final Context context)
	{
		final TreeNode result = new TreeNode(new Value(value, icon), new TreeController(context));
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
