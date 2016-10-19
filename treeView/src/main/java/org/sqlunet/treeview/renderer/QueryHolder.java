package org.sqlunet.treeview.renderer;

import android.content.Context;

import org.sqlunet.treeview.model.TreeNode;

/**
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class QueryHolder extends IconTreeRenderer
{
	// private static final String TAG = "QueryHolder"; //$NON-NLS-1$

	/**
	 * Constructor
	 *
	 * @param context context
	 */
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

	/**
	 * Add data to tree by launching the query
	 */
	private void addData()
	{
		final Query query = (Query) this.mNode.getValue();
		query.process(this.mNode);
	}

	// D A T A

	/**
	 * Query data
	 */
	public static abstract class Query extends IconTreeItem
	{
		/**
		 * Id used in query
		 */
		public final long id;

		/**
		 * Constructor
		 *
		 * @param id id
		 * @param icon extra icon
		 * @param text label text
		 */
		public Query(long id, int icon, CharSequence text)
		{
			super(icon, text);
			this.id = id;
		}

		/**
		 * Process query
		 *
		 * @param node node
		 */
		abstract public void process(final TreeNode node);
	}
}
