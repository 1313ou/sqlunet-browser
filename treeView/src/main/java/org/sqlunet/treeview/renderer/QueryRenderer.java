package org.sqlunet.treeview.renderer;

import android.content.Context;

import org.sqlunet.treeview.model.TreeNode;

/**
 * QueryRenderer renderer (expanding this renderer will trigger link search)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class QueryRenderer extends IconTreeRenderer
{
	// private static final String TAG = "QueryRenderer"; //

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public QueryRenderer(final Context context)
	{
		super(context);
	}

	@Override
	public void onExpandEvent(boolean expand)
	{
		super.onExpandEvent(expand);
		if (expand && this.node.isLeaf())
		{
			// Log.d(TAG, "size=" + this.node.size());
			addData();
		}
	}

	/**
	 * Add data to tree by launching the query
	 */
	private void addData()
	{
		final QueryData query = (QueryData) this.node.getValue();
		query.process(this.node);
	}

	// D A T A

	/**
	 * QueryData data
	 */
	public static abstract class QueryData extends Value
	{
		/**
		 * Id used in query
		 */
		public final long id;

		/**
		 * Constructor
		 *
		 * @param id   id
		 * @param icon extra icon
		 * @param text label text
		 */
		public QueryData(long id, int icon, CharSequence text)
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
