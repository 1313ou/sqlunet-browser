/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * PropBank role sets query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class PbRoleSetQuery extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.PropBankRoleSetQuery;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param roleSetId  target role set id
	 */
	@SuppressWarnings("boxing")
	public PbRoleSetQuery(final SQLiteDatabase connection, final long roleSetId)
	{
		super(connection, PbRoleSetQuery.QUERY);
		setParams(roleSetId);
	}

	/**
	 * Get the roleSet id from the result set
	 *
	 * @return the role set id from the result set
	 */
	public long getRoleSetId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the role set name from the result set
	 *
	 * @return the role set name from the result set
	 */
	public String getRoleSetName()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get role set head
	 *
	 * @return roleSet head
	 */
	public String getRoleSetHead()
	{
		assert this.cursor != null;
		return this.cursor.getString(2);
	}

	/**
	 * Get role set description
	 *
	 * @return role set description
	 */
	public String getRoleSetDescr()
	{
		assert this.cursor != null;
		return this.cursor.getString(3);
	}
}
