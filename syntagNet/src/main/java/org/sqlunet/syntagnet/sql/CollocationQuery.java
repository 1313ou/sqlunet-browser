/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * SyntagNet collocation query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class CollocationQuery extends BaseCollocationQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.SyntagNetCollocationQuery;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param collocationId  target collocation id
	 */
	@SuppressWarnings("boxing")
	public CollocationQuery(final SQLiteDatabase connection, final long collocationId)
	{
		super(connection, CollocationQuery.QUERY);
		setParams(collocationId);
	}
}
