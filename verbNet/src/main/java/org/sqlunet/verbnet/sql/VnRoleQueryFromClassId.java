/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * Query for VerbNet roles
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class VnRoleQueryFromClassId extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.VerbNetThematicRolesQueryFromClassId;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param classId    target classId
	 */
	@SuppressWarnings("boxing")
	public VnRoleQueryFromClassId(final SQLiteDatabase connection, final long classId)
	{
		super(connection, VnRoleQueryFromClassId.QUERY);
		setParams(classId);
	}

	/**
	 * Get the role id from the result set
	 *
	 * @return the role id from the result set
	 */
	public long getRoleId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the role type id from the result set
	 *
	 * @return the role type id from the result set
	 */
	public long getRoleTypeId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(1);
	}

	/**
	 * Get the role type from the result set
	 *
	 * @return the role type from the result set
	 */
	public String getRoleType()
	{
		assert this.cursor != null;
		return this.cursor.getString(2);
	}

	/**
	 * Get the role selectional restriction from the result set
	 *
	 * @return the role selectional restriction from the result set
	 */
	public String getSelectionRestriction()
	{
		assert this.cursor != null;
		return this.cursor.getString(3);
	}
}
