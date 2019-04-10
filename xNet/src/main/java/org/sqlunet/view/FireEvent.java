package org.sqlunet.view;

import android.util.Log;

import org.sqlunet.treeview.control.Controller;
import org.sqlunet.treeview.control.HotQueryController;
import org.sqlunet.treeview.control.QueryController;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.NonNull;

/**
 * Event firer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FireEvent
{
	private static final String TAG = "LIVE";

	static public void live(TreeNode node)
	{
		Log.d(TAG, "Live " + "\n" + node.toStringWithChildren());
	}

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
			//TODO TreeView.disable(node);
		}
		else
		{
			//TODO TreeView.remove(node);
		}
	}

	/**
	 * Results have been attached to this node
	 *
	 * @param node node
	 */
	static public void onResults(@NonNull final TreeNode node)
	{
		// TODO TreeView.expand(node, false);
	}

	/**
	 * Results have been attached to this node
	 *
	 * @param node   node
	 * @param levels number of levels
	 */
	static public void onResults(@NonNull final TreeNode node, int levels)
	{
		// TODO TreeView.expand(node, levels);
	}

	/**
	 * Result value is returned to be set as this node's value
	 *
	 * @param node  node
	 * @param value node value
	 */
	static public void onResults(@NonNull final TreeNode node, final CharSequence value)
	{
		// TODO TreeView.setNodeValue(node, value);
	}

	// Q U E R Y  R E A D Y

	static public void onQueryReady(@NonNull final TreeNode node)
	{
		final Controller<?> controller = node.getController();
		if (controller != null && controller instanceof HotQueryController)
		{
			final QueryController queryController = (QueryController) controller;
			queryController.processQuery();
		}
	}
}
