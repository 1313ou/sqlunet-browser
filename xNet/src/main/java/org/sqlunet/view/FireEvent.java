package org.sqlunet.view;

import android.util.Log;
import android.view.ViewGroup;

import org.sqlunet.browser.TreeFragment;
import org.sqlunet.treeview.control.Controller;
import org.sqlunet.treeview.control.TreeController;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeOp.TreeOpCode;

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

	public void live(final TreeOp[] ops)
	{
		live3(ops);
	}

	public void live0(final TreeOp[] ops)
	{
	}

	public void live1(final TreeOp[] ops)
	{
		final TreeView treeView = this.fragment.getTreeView();
		final TreeNode node = ops[0].getNode();
		treeView.expandContainer(node, true);
	}

	public void live2(final TreeOp[] ops)
	{
		final TreeView treeView = this.fragment.getTreeView();

		int n = ops.length;
		if (n == 1)
		{
			final TreeOp op = ops[0];
			handleNode2(op, treeView, false);
		}
		else if (n > 1)
		{
			for (int i = 1; i < n; i++)
			{
				final TreeOp op = ops[i];
				handleNode2(op, treeView, true);
			}
			handleJunction2(ops[0], treeView);
		}
	}

	private void handleJunction2(final TreeOp op, final TreeView treeView)
	{
		final TreeNode node = op.getNode();
		final Controller<?> controller = node.getController();
		if (controller instanceof TreeController)
		{
			final TreeController treeController = (TreeController) controller;
			// View v = treeController.getNodeView();
			// treeController.onExpandEvent();
		}
	}

	private void handleNode2(final TreeOp op, final TreeView treeView, final boolean includeSubnodes)
	{
		final TreeNode node = op.getNode();
		if (node.isZombie())
		{
			Log.d(TAG, "--- " + op.getCode() + " " + node.toString());
			treeView.remove(node);
		}
		else if (node.isDeadend())
		{
			Log.d(TAG, "000 " + op.getCode() + " " + node.toString());
			treeView.disable(node);
		}
		else
		{
			final TreeNode parent = node.getParent();
			final Controller<?> parentController = parent.getController();
			final ViewGroup viewGroup = parentController.getChildrenContainerView();
			if (viewGroup == null || !TreeView.isExpanded(parent))
			{
				Log.d(TAG, "*** " + op.getCode() + " " + node.toString());
				treeView.expandContainer(parent, includeSubnodes);
			}
			else
			{
				Log.d(TAG, "+++ " + op.getCode() + " " + node.toString());
				int index = parent.indexOf(node);
				treeView.addNodeView(viewGroup, node, index);
				// parentController.onExpandEvent();
			}
		}
	}

	public void live3(final TreeOp[] ops)
	{
		final TreeView treeView = this.fragment.getTreeView();

		int n = ops.length;
		if (n == 1)
		{
			final TreeOp op = ops[0];
			handleOp3(op, treeView, false);
		}
		else if (n > 1)
		{
			for (int i = 1; i < n; i++)
			{
				final TreeOp op = ops[i];
				handleOp3(op, treeView, true);
			}
			handleJunction3(ops[0], treeView);
		}
	}

	private void handleJunction3(final TreeOp op, final TreeView treeView)
	{
		final TreeNode node = op.getNode();
		final Controller<?> controller = node.getController();
		if (controller instanceof TreeController)
		{
			final TreeController treeController = (TreeController) controller;
			// View v = treeController.getNodeView();
			// treeController.onExpandEvent();
		}
	}

	private void handleOp3(final TreeOp op, final TreeView treeView, final boolean includeSubnodes)
	{
		final TreeOpCode code = op.getCode();
		final TreeNode node = op.getNode();
		switch (code)
		{
			case ANCHOR:
				break;
			case NEW:
				final TreeNode parent = node.getParent();
				final Controller<?> parentController = parent.getController();
				final ViewGroup viewGroup = parentController.getChildrenContainerView();
				if (viewGroup == null || !TreeView.isExpanded(parent))
				{
					Log.d(TAG, "*** " + op.getCode() + " " + node.toString());
					treeView.expandContainer(parent, includeSubnodes);
				}
				else
				{
					Log.d(TAG, "+++ " + op.getCode() + " " + node.toString());
					int index = parent.indexOf(node);
					treeView.addNodeView(viewGroup, node, index);
					// parentController.onExpandEvent();
				}
				break;
			case UPDATE:
				Log.d(TAG, "!!! " + op.getCode() + " " + node.toString());
				treeView.update(node);
				break;
			case TERMINATE:
				Log.d(TAG, "xxx " + op.getCode() + " " + node.toString());
				treeView.disable(node);
				break;
			case REMOVE:
				Log.d(TAG, "--- " + op.getCode() + " " + node.toString());
				treeView.remove(node);
				break;
			default:
			case NOOP:
				break;
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
