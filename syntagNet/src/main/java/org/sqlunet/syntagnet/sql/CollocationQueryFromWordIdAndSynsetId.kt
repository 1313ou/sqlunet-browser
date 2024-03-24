/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.syntagnet.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * SyntagNet Collocation query from word id and synset id
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class CollocationQueryFromWordIdAndSynsetId extends BaseCollocationQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.SyntagNetCollocationQueryFromWordIdAndSynsetId;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param wordid     target word id
	 * @param synsetid   target synset id
	 */
	@SuppressWarnings("boxing")
	public CollocationQueryFromWordIdAndSynsetId(final SQLiteDatabase connection, final long wordid, final long synsetid)
	{
		super(connection, CollocationQueryFromWordIdAndSynsetId.QUERY);
		setParams(wordid, synsetid, wordid, synsetid);
	}
}
