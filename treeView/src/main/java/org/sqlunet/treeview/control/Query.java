/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.treeview.control;

import org.sqlunet.treeview.model.TreeNode;

/**
 * Query data
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public abstract class Query
{
	/**
	 * Id used in query
	 */
	public final long id;

	/**
	 * Constructor
	 *
	 * @param id id
	 */
	protected Query(final long id)
	{
		this.id = id;
	}

	/**
	 * Process query
	 *
	 * @param node node
	 */
	abstract public void process(final TreeNode node);
}
