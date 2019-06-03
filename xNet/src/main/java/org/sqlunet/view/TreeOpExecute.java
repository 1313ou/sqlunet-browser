/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.view;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.TreeFragment;
import org.sqlunet.treeview.control.CompositeValue;
import org.sqlunet.treeview.control.Controller;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeOp.TreeOpCode;

import androidx.annotation.NonNull;

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

	public void exec(@NonNull final TreeOp[] ops)
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
		if (treeView == null)
		{
			return;
		}
		int n = ops.length;
		if (n == 1)
		{
			final TreeOp op = ops[0];
			execOp(op, treeView, 0);
		}
		else if (n > 1)
		{
			for (final TreeOp op : ops)
			{
				execOp(op, treeView, -1);
			}
		}
	}

	private void execOp(final TreeOp op, @NonNull final TreeView treeView, final int levels)
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
			// V I E W   L A Y O U T   C H A N G I N G

			case NEWTREE:
			{
				Log.d(TAG, "vvv " + op.getCode() + " " + node.toString());
				final View view = treeView.expandNode(node, -1, false, false);
				if (node.getController().takeEnsureVisible())
				{
					treeView.ensureVisible(view);
				}
			}
			break;

			case NEWCHILD:
				Log.d(TAG, "+++ " + op.getCode() + " " + node.toString());
				//treeView.newNodeView(node, levels);
				break;

			case NEWMAIN:
			case NEWEXTRA:
			case NEWUNIQUE:
			{
				Log.d(TAG, "... " + op.getCode() + " " + node.toString());
				final View view = treeView.newNodeView(node, levels);
				if (node.getController().takeEnsureVisible())
				{
					treeView.ensureVisible(view);
				}
			}
			break;

			case UPDATE:
			{
				Log.d(TAG, "!!! " + op.getCode() + " " + node.toString());
				final View view = treeView.update(node);
				if (node.getController().takeEnsureVisible() && view != null)
				{
					treeView.ensureVisible(view);
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
			{
				Log.d(TAG, "--- " + op.getCode() + " " + node.toString());
				treeView.remove(node);
			}
			break;

			// V I E W   C H A N G I N G

			case DEADEND:
			{
				Log.d(TAG, "xxx " + op.getCode() + " " + node.toString());
				treeView.deadend(node);
			}
			break;

			default:
			case NOOP:
				break;
		}
	}

	private boolean isNodeWithCompositeValueText(final TreeNode node, @NonNull final String text)
	{
		final Object value = node.getValue();
		return (value instanceof CompositeValue) && text.equals(((CompositeValue) value).text.toString());
	}
}
