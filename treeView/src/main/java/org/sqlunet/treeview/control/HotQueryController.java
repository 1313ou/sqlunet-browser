package org.sqlunet.treeview.control;

import org.sqlunet.treeview.R;

/**
 * Query controller (expanding this controller will trigger query)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class HotQueryController extends QueryController
{
	// static private final String TAG = "HotQueryController";

	/**
	 * Constructor
	 */
	public HotQueryController()
	{
		super();
		this.layoutRes = R.layout.layout_query;
	}

	@Override
	protected void markExpanded()
	{
		this.junctionView.setImageResource(R.drawable.ic_hotquery_expanded);
	}

	@Override
	protected void markCollapsed()
	{
		this.junctionView.setImageResource(R.drawable.ic_hotquery_collapsed);
	}

	@Override
	protected void markDeadend()
	{
		this.junctionView.setImageResource(R.drawable.ic_hotquery_deadend);
	}
}
