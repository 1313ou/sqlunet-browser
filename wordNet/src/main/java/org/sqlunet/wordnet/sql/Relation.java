/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.sql;

/**
 * Relation, utility class to encapsulate relation data
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

class Relation
{
	/**
	 * <code>id</code> relation id
	 */
	public final int id;

	/**
	 * <code>pos</code> part-of-speech id
	 */
	public int pos;

	/**
	 * <code>name</code> relation name
	 */
	public final String name;

	/**
	 * <code>recurses</code> is whether the relation recurses
	 */
	public final boolean recurses;

	/**
	 * Constructor
	 *
	 * @param id       relation id
	 * @param name     relation name
	 * @param recurses whether the relation recurses
	 */
	public Relation(final int id, final String name, final boolean recurses)
	{
		super();
		this.id = id;
		this.name = name;
		this.recurses = recurses;
	}
}
