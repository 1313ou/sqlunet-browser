package org.sqlunet.treeview.control;

import org.sqlunet.treeview.R;

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
		this.layoutRes = R.layout.layout_query;
	}

	@Override
	public void onExpandEvent(boolean unused)
	{
		this.junctionView.setImageResource(this.node.isEnabled() ? R.drawable.ic_query_expanded : R.drawable.ic_leaf);
	}

	@Override
	public void onCollapseEvent()
	{
		this.junctionView.setImageResource(this.node.isEnabled() ? R.drawable.ic_query_collapsed : R.drawable.ic_leaf);
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
