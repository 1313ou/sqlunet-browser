/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.treeview.control;

/**
 * Link Data
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public abstract class Link
{
	/**
	 * Id used in link
	 */
	public final long id;

	/**
	 * Constructor
	 *
	 * @param id id
	 */
	protected Link(long id)
	{
		this.id = id;
	}

	/**
	 * Process
	 */
	abstract public void process();
}
