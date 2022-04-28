/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.model;

import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;

import org.sqlunet.treeview.control.ColdQueryController;
import org.sqlunet.treeview.control.CompositeValue;
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
import org.sqlunet.treeview.control.TextController;
import org.sqlunet.treeview.control.TreeController;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.NonNull;

/**
 * Tree factory
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TreeFactory
{
	// private static final String TAG = "TreeFactory";

	// NON-TREE (without tree junction icon)

	/**
	 * Make text node
	 *
	 * @param value       character sequence
	 * @param breakExpand break expand flag
	 * @return created node
	 */
	@NonNull
	static public TreeNode makeTextNode(@NonNull final CharSequence value, final boolean breakExpand)
	{
		return new TreeNode(value, new TextController(breakExpand));
	}

	/**
	 * Make icon-text node
	 *
	 * @param text        text
	 * @param icon        icon
	 * @param breakExpand break expand flag
	 * @return created node
	 */
	@NonNull
	static public TreeNode makeIconTextNode(@NonNull final CharSequence text, final int icon, final boolean breakExpand)
	{
		return new TreeNode(new CompositeValue(text, icon), new IconTextController(breakExpand), false);
	}

	/**
	 * Make leaf node
	 *
	 * @param text        text
	 * @param icon        icon (extra icon after tree icon)
	 * @param breakExpand break expand flag
	 * @return created node
	 */
	@NonNull
	static public TreeNode makeLeafNode(@NonNull final CharSequence text, final int icon, final boolean breakExpand)
	{
		return new TreeNode(new CompositeValue(text, icon), new LeafController(breakExpand), false);
	}

	/**
	 * Make more (leaf) node
	 *
	 * @param text        text
	 * @param icon        icon (extra icon after tree icon)
	 * @param breakExpand break expand flag
	 * @return created node
	 */
	@NonNull
	static public TreeNode makeMoreNode(@NonNull final CharSequence text, final int icon, final boolean breakExpand)
	{
		return new TreeNode(new CompositeValue(text, icon), new MoreController(breakExpand), false);
	}

	/**
	 * Make icon-text-link node
	 *
	 * @param text        text
	 * @param icon        icon
	 * @param link        link
	 * @param breakExpand break expand flag
	 * @return created node
	 */
	@NonNull
	static public TreeNode makeLinkNode(@NonNull final CharSequence text, final int icon, final boolean breakExpand, final Link link)
	{
		return new TreeNode(new CompositeValue(text, icon, link), new LinkNodeController(breakExpand), false);
	}

	/**
	 * Make link leaf node
	 *
	 * @param text        text
	 * @param icon        icon
	 * @param breakExpand break expand flag
	 * @param link        link
	 * @return created node
	 */
	@NonNull
	static public TreeNode makeLinkLeafNode(@NonNull final CharSequence text, final int icon, final boolean breakExpand, final Link link)
	{
		return new TreeNode(new CompositeValue(text, icon, link), new LinkLeafController(breakExpand));
	}

	// TREE

	/**
	 * Add tree node
	 *
	 * @param value       character sequence
	 * @param icon        icon resource id
	 * @param breakExpand break expand flag
	 * @return created node
	 */
	@NonNull
	static public TreeNode makeTreeNode(@NonNull final CharSequence value, final int icon, final boolean breakExpand)
	{
		return new TreeNode(new CompositeValue(value, icon), new TreeController(breakExpand));
	}

	/**
	 * Make link tree node
	 *
	 * @param text        text
	 * @param icon        icon
	 * @param breakExpand break expand flag
	 * @param link        link
	 * @return created node
	 */
	@NonNull
	static public TreeNode makeLinkTreeNode(@NonNull final CharSequence text, final int icon, final boolean breakExpand, final Link link)
	{
		return new TreeNode(new CompositeValue(text, icon, link), new LinkTreeController(breakExpand));
	}

	/**
	 * Make query node
	 *
	 * @param text        label text
	 * @param icon        icon
	 * @param breakExpand break expand flag
	 * @param query       query
	 * @return created node
	 */
	@NonNull
	static public TreeNode makeQueryNode(@NonNull final CharSequence text, final int icon, final boolean breakExpand, final Query query)
	{
		return new TreeNode(new CompositeValue(text, icon, query), new ColdQueryController(breakExpand));
	}

	/**
	 * Make hot (self-triggered) query node
	 *
	 * @param text        label text
	 * @param icon        icon
	 * @param breakExpand break expand flag
	 * @param query       query
	 * @return created node
	 */
	@NonNull
	static public TreeNode makeHotQueryNode(@NonNull final CharSequence text, final int icon, final boolean breakExpand, final Query query)
	{
		final HotQueryController controller = new HotQueryController(breakExpand);
		final TreeNode result = new TreeNode(new CompositeValue(text, icon, query), controller);

		final Handler handler = new Handler(Looper.getMainLooper());
		handler.post(controller::processQuery);

		return result;
	}

	/**
	 * Make link query node
	 *
	 * @param text        label text
	 * @param icon        icon
	 * @param breakExpand break expand flag
	 * @param query       query
	 * @param link        link
	 * @return created node
	 */
	@NonNull
	static public TreeNode makeLinkQueryNode(@NonNull final CharSequence text, final int icon, final boolean breakExpand, final Query query, final Link link)
	{
		return new TreeNode(new CompositeValue(text, icon, query, link), new LinkQueryController(breakExpand));
	}

	// H E L P E R S

	/**
	 * No results have been attached to this node
	 *
	 * @param node node
	 */
	static public void setNoResult(@NonNull final TreeNode node)
	{
		// Log.d(TAG, "Deadend " + node);
		node.setDeadend(true);
		node.setEnabled(false);
	}

	public static void setTextNode(@NonNull final TreeNode node, final SpannableStringBuilder sb)
	{
		node.setValue(sb);
	}
}
