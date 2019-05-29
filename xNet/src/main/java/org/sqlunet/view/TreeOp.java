/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.view;

import org.sqlunet.treeview.model.TreeNode;

import java.util.ArrayList;

public class TreeOp
{
	public enum TreeOpCode
	{
		NOOP, NEWTREE, NEWCHILD, NEWUNIQUE, NEWMAIN, NEWEXTRA, UPDATE, TRANSFER, DEADEND, REMOVE, COLLAPSE, BREAK_EXPAND_AT,
	}

	private final TreeOpCode code;

	private final TreeNode node;

	private TreeOp(final TreeOpCode code, final TreeNode node)
	{
		this.code = code;
		this.node = node;
	}

	public TreeOpCode getCode()
	{
		return code;
	}

	public TreeNode getNode()
	{
		return node;
	}

	static public TreeOp[] seq(Object... items)
	{
		final TreeOp[] result = new TreeOp[items.length / 2];
		int j = 0;
		for (int i = 0; i < items.length - 1; i += 2)
		{
			result[j++] = new TreeOp((TreeOpCode) items[i], (TreeNode) items[i + 1]);
		}
		return result;
	}

	public static class TreeOps extends ArrayList<TreeOp>
	{
		public TreeOps(Object... items)
		{
			add(items);
		}

		public void add(Object... items)
		{
			prepend(items);
		}

		/*
		private void append(Object... items)
		{
			for (int i = 0; i < items.length - 1; i += 2)
			{
				add(new TreeOp((TreeOpCode) items[i], (TreeNode) items[i + 1]));
			}
		}
		*/

		private void prepend(Object... items)
		{
			for (int i = 0; i < items.length - 1; i += 2)
			{
				add(0, new TreeOp((TreeOpCode) items[i], (TreeNode) items[i + 1]));
			}
		}

		public TreeOp[] toArray()
		{
			return toArray(new TreeOp[0]);
		}
	}
}
