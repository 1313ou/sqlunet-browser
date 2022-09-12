/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.syntagnet.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * SyntagNet Collocation query from word id
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class CollocationQueryFromWordIds extends BaseCollocationQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.SyntagNetCollocationQueryFromWordIds;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param wordid     target word id
	 * @param word2id    target word 2 id
	 */
	@SuppressWarnings("boxing")
	public CollocationQueryFromWordIds(final SQLiteDatabase connection, final long wordid, final long word2id)
	{
		super(connection, CollocationQueryFromWordIds.QUERY);
		setParams(wordid, word2id);
	}
}
