package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

public class VnClass
{
	public final String className;

	public final long classId;

	@SuppressWarnings("unused")
	public final String groupings;

	/**
	 * Constructor
	 * @param className
	 *            class name
	 * @param classId
	 *            class id
	 * @param groupings
	 *            groupings
	 */
	private VnClass(final String className, final long classId, final String groupings)
	{
		super();
		this.className = className;
		this.classId = classId;
		this.groupings = groupings;
	}

	/**
	 * Make sets of VerbNet memberships from query built from classid
	 *
	 * @param connection
	 *            is the database connection
	 * @param classId
	 *            is the class id to build the query from
	 * @return list of VerbNet classes
	 */
	static public VnClass make(final SQLiteDatabase connection, final long classId)
	{
		VnClassQueryCommand query = null;
		try
		{
			query = new VnClassQueryCommand(connection, classId);
			query.execute();

			if (query.next())
			{
				final String className = query.getClassName();
				final String groupings = query.getGroupings();

				return new VnClass(className, classId, groupings);
			}
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return null;
	}
}
