package org.sqlunet.treeview.control;

import android.content.Context;

/**
 * Query controller (expanding this controller will trigger query)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class HotQueryController extends TreeController
{
	// static private final String TAG = "QueryController";

	private boolean processed = false;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public HotQueryController(final Context context)
	{
		super(context);
	}

	@Override
	public void onExpandEvent(boolean expand)
	{
		super.onExpandEvent(expand);

		if (expand && !true && this.node.isLeaf())
		{
			processQuery();
		}
	}

	/**
	 * Add data to tree by launching the query
	 */
	synchronized public void processQuery()
	{
		if (!this.processed)
		{
			this.processed = true;
			final Query query = getQuery();
			assert query != null;
			query.process(this.node);
		}
	}

	/**
	 * Get query
	 */
	private Query getQuery()
	{
		final Value value = (Value) this.node.getValue();
		if (value != null)
		{
			assert value.payload != null;
			return (Query) value.payload[0];
		}
		return null;
	}
}
