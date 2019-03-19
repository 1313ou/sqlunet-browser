package org.sqlunet.view;

import android.support.annotation.NonNull;

import org.sqlunet.treeview.control.Controller;
import org.sqlunet.treeview.control.QueryController;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;

/**
 * Event firer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FireEvent
{
	// R E S U L T S  A V A I L A B L E

	/**
	 * No results have been attached to this node
	 *
	 * @param node       node
	 * @param addNewNode whether results were supposed to be new subnodes or replace query node
	 */
	static public void onNoResult(@NonNull final TreeNode node, boolean addNewNode)
	{
		if (addNewNode)
		{
			TreeView.disable(node);
		}
		else
		{
			TreeView.remove(node);
		}
	}

	/**
	 * Results have been attached to this node
	 *
	 * @param node node
	 */
	static public void onResults(@NonNull final TreeNode node)
	{
		TreeView.expand(node, false);
	}

	/**
	 * Results have been attached to this node
	 *
	 * @param node   node
	 * @param levels number of levels
	 */
	static public void onResults(@NonNull final TreeNode node, int levels)
	{
		TreeView.expand(node, levels);
	}

	/**
	 * Result value is returned to be set as this node's value
	 *
	 * @param node  node
	 * @param value node value
	 */
	static public void onResults(@NonNull final TreeNode node, final CharSequence value)
	{
		TreeView.setNodeValue(node, value);
	}

	// Q U E R Y  R E A D Y

	static public void onQueryReady(@NonNull final TreeNode node)
	{
		final Controller<?> controller = node.getController();
		if (controller instanceof QueryController)
		{
			final QueryController queryController = (QueryController) controller;
			if (queryController.triggerNow)
			{
				queryController.processQuery();
			}
		}
	}
}
