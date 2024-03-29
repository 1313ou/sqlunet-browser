/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

/**
 * VerbNet class
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VnClass
{
	/**
	 * Class name
	 */
	public final String className;

	/**
	 * Class id
	 */
	public final long classId;

	/**
	 * Groupings
	 */
	public final String groupings;

	/**
	 * Constructor
	 *
	 * @param className class name
	 * @param classId   class id
	 * @param groupings groupings
	 */
	private VnClass(final String className, final long classId, final String groupings)
	{
		super();
		this.className = className;
		this.classId = classId;
		this.groupings = groupings;
	}

	/**
	 * Make sets of VerbNet class from query built from classId
	 *
	 * @param connection connection
	 * @param classId    is the class id to build the query from
	 * @return list of VerbNet classes
	 */
	@Nullable
	static public VnClass make(final SQLiteDatabase connection, final long classId)
	{
		try (VnClassQuery query = new VnClassQuery(connection, classId))
		{
			query.execute();

			if (query.next())
			{
				final String className = query.getClassName();
				final String groupings = query.getGroupings();

				return new VnClass(className, classId, groupings);
			}
		}
		return null;
	}
}
