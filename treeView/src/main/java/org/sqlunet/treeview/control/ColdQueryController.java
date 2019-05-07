package org.sqlunet.treeview.control;

/**
 * Query controller (expanding this controller will trigger query)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class ColdQueryController extends QueryController
{
	// static private final String TAG = "ColdQueryController";

	/**
	 * Constructor
	 */
	public ColdQueryController()
	{
		super();
	}

	@Override
	public void fire()
	{
		if (this.node.isLeaf())
		{
			processQuery();
		}
	}
}
