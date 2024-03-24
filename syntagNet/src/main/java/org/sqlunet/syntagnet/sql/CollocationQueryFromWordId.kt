/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.syntagnet.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * SyntagNet Collocation query from word id
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class CollocationQueryFromWordId extends BaseCollocationQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.SyntagNetCollocationQueryFromWordId;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param wordid     target word id
	 */
	@SuppressWarnings("boxing")
	public CollocationQueryFromWordId(final SQLiteDatabase connection, final long wordid)
	{
		super(connection, CollocationQueryFromWordId.QUERY);
		setParams(wordid, wordid);
	}
}
