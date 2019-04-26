package org.sqlunet.treeview.control;

import android.content.Context;

/**
 * Query controller (expanding this controller will trigger query)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class QueryController extends TreeController
{
	// static private final String TAG = "QueryController";

	private boolean processed = false;

	/**
	 * Constructor
	 */
	public QueryController()
	{
		super();
	}

	@Override
	public void onExpandEvent(boolean triggerQueries)
	{
		super.onExpandEvent(triggerQueries);

		if (triggerQueries && this.node.isLeaf())
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
