package org.sqlunet.treeview.renderer;

import org.sqlunet.treeview.model.TreeNode;

import android.content.Context;

/**
 * @author Bernard Bou
 */
public class QueryHolder extends IconTreeRenderer
{
	// private static final String TAG = "QueryHolder"; //$NON-NLS-1$

	public QueryHolder(Context context)
	{
		super(context);
	}

	@Override
	public void toggle(boolean active)
	{
		super.toggle(active);
		if (active && this.mNode.isLeaf())
		{
			// Log.d(TAG, "size=" + this.mNode.size());
			addData();
		}
	}

	// D A T A

	private void addData()
	{
		final Query query = (Query) this.mNode.getValue();
		query.process(this.mNode);
	}

	public static abstract class Query extends IconTreeItem
	{
		public final long id;

		public Query(long id0, int icon, CharSequence text)
		{
			super(icon, text);
			this.id = id0;
		}
		
		abstract public void process(final TreeNode node);
	}
}
