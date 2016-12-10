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

	public final boolean triggerNow;

	private boolean processed = false;

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
		if (!this.processed)
		{
			this.processed = true;
			final Query query = getQuery();
			query.process(this.node);
		}
	}

	/**
	 * Get query
	 */
	protected Query getQuery()
	{
		final Value value = (Value) this.node.getValue();
		return (Query) value.payload[0];
	}

	// D A T A

}
