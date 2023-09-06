/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * Query for a relation enumeration
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class RelationsQuery extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.RelationsQuery;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 */
	public RelationsQuery(final SQLiteDatabase connection)
	{
		super(connection, RelationsQuery.QUERY);
	}

	/**
	 * Get the id from the result set
	 *
	 * @return the relation id value from the result set
	 */
	public int getId()
	{
		assert this.cursor != null;
		return this.cursor.getInt(0);
	}

	/**
	 * Get the name from the result set
	 *
	 * @return the relation name value from the result set
	 */
	public String getName()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get the recurse capability from the result set
	 *
	 * @return the relation recurse capability value from the result set
	 */
	public boolean getRecurse()
	{
		assert this.cursor != null;
		return this.cursor.getInt(2) != 0;
	}
}