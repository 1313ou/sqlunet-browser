/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.view;

import android.util.Log;
import android.view.ViewGroup;

import org.sqlunet.browser.TreeFragment;
import org.sqlunet.treeview.control.CompositeValue;
import org.sqlunet.treeview.control.Controller;
import org.sqlunet.treeview.control.TreeController;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeOp.TreeOpCode;

/**
 * TreeOp executor
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TreeOpExecute
{
	private static final String TAG = "TreeOpExecute";

	private final TreeFragment fragment;

	public TreeOpExecute(final TreeFragment fragment)
	{
		this.fragment = fragment;
	}

	public void exec(final TreeOp[] ops)
	{
		execImpl(ops);
	}

	@SuppressWarnings("EmptyMethod")
	private void noopImpl(@SuppressWarnings("unused") final TreeOp[] ops)
	{
	}

	private void execImpl(final TreeOp[] ops)
	{
		final TreeView treeView = this.fragment.getTreeView();

		int n = ops.length;
		if (n == 1)
		{
			final TreeOp op = ops[0];
			execOp(op, treeView, 0);
		}
		else if (n > 1)
		{
			for (int i = 1; i < n; i++)
			{
				final TreeOp op = ops[i];
				execOp(op, treeView, -1);
			}
			execFirst(ops[0], treeView);
		}
	}

	private void execFirst(final TreeOp op, final TreeView treeView)
	{
		final TreeNode node = op.getNode();
		final Controller<?> controller = node.getController();
		if (controller instanceof TreeController)
		{
			Log.d(TAG, "/// " + op.getCode() + " " + node.toString());
		}
	}

	private void execOp(final TreeOp op, final TreeView treeView, final int levels)
	{
		final TreeOpCode code = op.getCode();
		final TreeNode node = op.getNode();
		/*
		if (isNodeWithCompositeValueText(node, "Agent Agent"))
		{
			Log.d(TAG, "xxx " + op.getCode() + " " + node);
		}
		*/
		
		switch (code)
		{
			case ANCHOR:
				Log.d(TAG, "@@@ " + op.getCode() + " " + node.toString());
				break;

			case NEW:
			{
				final TreeNode parent = node.getParent();
				final Controller<?> parentController = parent.getController();
				final ViewGroup childrenView = parentController.getChildrenView();
				if (childrenView == null || !TreeView.isExpanded(parent))
				{
					Log.d(TAG, "*** " + op.getCode() + " " + node.toString());
					treeView.expandNode(parent, levels, false, false);
				}
				else
				{
					Log.d(TAG, "+++ " + op.getCode() + " " + node.toString());
					int index = parent.indexOf(node);
					treeView.addSubtreeView(childrenView, node, index);
				}
			}
			break;

			case COLLAPSE:
			{
				final Controller<?> controller = node.getController();
				final ViewGroup childrenView = controller.getChildrenView();
				if (childrenView != null && TreeView.isExpanded(node))
				{
					Log.d(TAG, "^^^ " + op.getCode() + " " + node.toString());
					treeView.collapseNode(node, true);
				}
			}
			break;

			case REMOVE:
				Log.d(TAG, "--- " + op.getCode() + " " + node.toString());
				treeView.remove(node);
				break;

			case UPDATE:
				Log.d(TAG, "!!! " + op.getCode() + " " + node.toString());
				treeView.update(node);
				break;

			case DEADEND:
				Log.d(TAG, "xxx " + op.getCode() + " " + node.toString());
				treeView.deadend(node);
				break;

			case BREAK_EXPAND_AT:
			{
				Log.d(TAG, "||| " + op.getCode() + " " + node.toString());
				final Controller<?> controller = node.getController();
				controller.setBreakExpand(true);
			}
			break;

			default:
			case NOOP:
				break;
		}
	}

	private boolean isNodeWithCompositeValueText(final TreeNode node, final String text)
	{
		final Object value = node.getValue();
		return (value instanceof CompositeValue) && text.equals(((CompositeValue) value).text.toString());
	}
}
