package org.sqlunet.treeview.control;

import android.content.Context;

import org.sqlunet.treeview.model.TreeNode;

/**
 * Query controller (expanding this controller will trigger query)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class QueryController extends IconTreeController
{
	// private static final String TAG = "QueryController";
	public final boolean triggerNow;

	public boolean processed = false;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public QueryController(final Context context, final boolean triggerNow)
	{
		super(context);
		this.triggerNow = triggerNow;
	}

	@Override
	public void onExpandEvent(boolean expand)
	{
		super.onExpandEvent(expand);

		if (expand && !this.triggerNow && this.node.isLeaf())
		{
			processQuery();
		}
	}

	/**
	 * Add data to tree by launching the query
	 */
	synchronized public void processQuery()
	{
		if(!processed)
		{
			processed = true;
			final Query query = (Query) this.node.getValue();
			query.process(this.node);
		}
	}

	// D A T A

	/**
	 * Query data
	 */
	public static abstract class Query extends Value
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
