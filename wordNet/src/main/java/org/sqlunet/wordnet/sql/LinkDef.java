package org.sqlunet.wordnet.sql;

/**
 * LinkDef, utility class to encapsulate link data
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

class LinkDef
{
	/**
	 * <code>id</code> link id
	 */
	public final int id;

	/**
	 * <code>pos</code> part-of-speech id
	 */
	@SuppressWarnings("unused")
	public int pos;

	/**
	 * <code>name</code> link name
	 */
	public final String name;

	/**
	 * <code>recurses</code> is whether the link recurses
	 */
	public final boolean recurses;

	/**
	 * Constructor
	 *
	 * @param id       link id
	 * @param name     link name
	 * @param recurses whether the link recurses
	 */
	public LinkDef(final int id, final String name, final boolean recurses)
	{
		super();
		this.id = id;
		this.name = name;
		this.recurses = recurses;
	}
}
