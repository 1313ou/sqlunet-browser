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
		this.junctionView.setImageResource(this.node.isEnabled() && !this.node.isDeadend() ? R.drawable.ic_hotquery_expanded : R.drawable.ic_leaf);
	}

	@Override
	protected void markCollapsed()
	{
		this.junctionView.setImageResource(this.node.isEnabled() && !this.node.isDeadend() ? R.drawable.ic_hotquery_collapsed : R.drawable.ic_leaf);
	}
}
