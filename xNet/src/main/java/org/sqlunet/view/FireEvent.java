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

	public void live(final TreeNode[] nodes)
	{
		//Log.d(TAG, "Live " + "\n" + nodes[0].toStringWithChildren());
		Log.d(TAG, "Live " + "\n" + nodes[0].toString());
		TreeView treeView = this.fragment.getTreeView();

		int n = nodes.length;
		if (n == 1)
		{
			final TreeNode node = nodes[0];
			if (node.isZombie())
			{
				treeView.remove(node);
			}
			else
			{
				treeView.expand(node, false);
			}
		}
		else if (n > 1)
		{
			for (int i = 1; i < n; i++)
			{
				final TreeNode node = nodes[i];
				final TreeNode parent = node.getParent();
				if (node.isZombie())
				{
					treeView.remove(node);
				}
				else
				{
					if (parent.isExpanded())
					{
						final Controller<?> controller = parent.getController();
						final ViewGroup viewGroup = controller.getChildrenContainerView();
						if (viewGroup != null)
						{
							treeView.addNodeView(viewGroup, node);
						}
					}
					else
					{
						treeView.expand(parent, true);
					}
				}
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
}
