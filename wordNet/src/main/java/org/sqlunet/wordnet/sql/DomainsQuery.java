/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * Query for domain enumeration
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class DomainsQuery extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.AllDomainsQuery; // ;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 */
	public DomainsQuery(final SQLiteDatabase connection)
	{
		super(connection, DomainsQuery.QUERY);
	}

	/**
	 * Get the domain id from the result set
	 *
	 * @return the domain id value from the result set
	 */
	public int getId()
	{
		assert this.cursor != null;
		return this.cursor.getInt(0);
	}

	/**
	 * Get the domain name (with pos prefix) from the result set
	 *
	 * @return the domain name (with pos prefix) from the result set
	 */
	public String getPosDomainName()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get the part-of-speech code value from the result set
	 *
	 * @return the part-of-speech value from the result set (in the range n,v,a,r)
	 */
	public int getPos()
	{
		assert this.cursor != null;
		String posStr = this.cursor.getString(2);
		if (posStr != null)
			return posStr.charAt(0);
		return 0;
	}
}