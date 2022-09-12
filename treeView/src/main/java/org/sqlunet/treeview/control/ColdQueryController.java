/*
 * Copyright (c) 2022. Bernard Bou
 */

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
	 *
	 * @param breakExpand whether this controller breaks expansion
	 */
	public ColdQueryController(final boolean breakExpand)
	{
		super(breakExpand);
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
