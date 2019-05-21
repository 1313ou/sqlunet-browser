/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * PropBank role sets query from word id
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class PbRoleSetQueryFromWordId extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.PropBankRoleSetQueryFromWordId;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 */
	@SuppressWarnings("boxing")
	public PbRoleSetQueryFromWordId(final SQLiteDatabase connection, final long wordId)
	{
		super(connection, PbRoleSetQueryFromWordId.QUERY);
		setParams(wordId);
	}

	/**
	 * Get the role set id from the result set
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
	 * @return role set head
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
