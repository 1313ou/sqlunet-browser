/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * Query for a link enumeration
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class LinksQuery extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.AllLinksQuery;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 */
	public LinksQuery(final SQLiteDatabase connection)
	{
		super(connection, LinksQuery.QUERY);
	}

	/**
	 * Get the id from the result set
	 *
	 * @return the link id value from the result set
	 */
	public int getId()
	{
		assert this.cursor != null;
		return this.cursor.getInt(0);
	}

	/**
	 * Get the name from the result set
	 *
	 * @return the link name value from the result set
	 */
	public String getName()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get the recurse capability from the result set
	 *
	 * @return the link recurse capability value from the result set
	 */
	public boolean getRecurse()
	{
		assert this.cursor != null;
		return this.cursor.getInt(2) != 0;
	}
}