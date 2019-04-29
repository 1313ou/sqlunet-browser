package org.sqlunet.view;

import android.util.Log;
import android.view.ViewGroup;

import org.sqlunet.browser.TreeFragment;
import org.sqlunet.treeview.control.Controller;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;

import androidx.annotation.NonNull;

/**
 * Event firer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FireEvent
{
	private static final String TAG = "LIVE";

	private TreeFragment fragment;

	public FireEvent(final TreeFragment fragment)
	{
		this.fragment = fragment;
	}

	public void live0(final TreeNode[] nodes)
	{
	}

	public void live1(final TreeNode[] nodes)
	{
		final TreeView treeView = this.fragment.getTreeView();
		final TreeNode node = nodes[0];
		treeView.expandContainer(node, true);
	}

	public void live(final TreeNode[] nodes)
	{
		final TreeView treeView = this.fragment.getTreeView();

		int n = nodes.length;
		if (n == 1)
		{
			final TreeNode node = nodes[0];
			handleNode(node, treeView, false);
		}
		else if (n > 1)
		{
			for (int i = 1; i < n; i++)
			{
				final TreeNode node = nodes[i];
				handleNode(node, treeView, true);
			}
			handleNode(nodes[0], treeView, false);
		}
	}

	private void handleNode(final TreeNode node, final TreeView treeView, final boolean includeSubnodes)
	{
		if (node.isZombie())
		{
			Log.d(TAG, "--- " + node.toString());
			treeView.remove(node);
		}
		else
		{
			final TreeNode parent = node.getParent();
			final Controller<?> parentController = parent.getController();
			final ViewGroup viewGroup = parentController.getChildrenContainerView();
			if (viewGroup == null || !parent.isExpanded())
			{
				Log.d(TAG, "### " + node.toString());
				treeView.expandContainer(parent, includeSubnodes);
			}
			else
			{
				Log.d(TAG, "+++ " + node.toString());
				treeView.addNodeView(viewGroup, node);
				parentController.onExpandEvent(true);
			}
		}
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
		// TODO TreeView.expandContainer(node, false);
	}

	/**
	 * Results have been attached to this node
	 *
	 * @param node   node
	 * @param levels number of levels
	 */
	static public void onResults(@NonNull final TreeNode node, int levels)
	{
		// TODO TreeView.expandContainer(node, levels);
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
}
