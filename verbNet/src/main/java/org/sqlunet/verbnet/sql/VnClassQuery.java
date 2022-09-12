/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * VerbNet class query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class VnClassQuery extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.VerbNetClassQuery;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param classId    target class id
	 */
	@SuppressWarnings("boxing")
	public VnClassQuery(final SQLiteDatabase connection, final long classId)
	{
		super(connection, VnClassQuery.QUERY);
		setParams(classId);
	}

	/**
	 * Get the class id from the result set
	 *
	 * @return the class id from the result set
	 */
	public long getClassId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the class name from the result set
	 *
	 * @return the class name from the result set
	 */
	public String getClassName()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get the groupings from the result set
	 *
	 * @return the (|-separated) groupings from the result set
	 */
	public String getGroupings()
	{
		assert this.cursor != null;
		return this.cursor.getString(2);
	}
}
