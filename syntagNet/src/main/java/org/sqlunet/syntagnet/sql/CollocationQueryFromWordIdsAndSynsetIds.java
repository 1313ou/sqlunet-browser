/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.syntagnet.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * SyntagNet Collocation query from word id and synset id
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class CollocationQueryFromWordIdsAndSynsetIds extends BaseCollocationQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.SyntagNetCollocationQueryFromWordIdsAndSynsetIds;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param wordid     target word id
	 * @param synsetid   target synset id
	 * @param word2id     target word 2 id
	 * @param synset2id   target synset 2 id
	 */
	@SuppressWarnings("boxing")
	public CollocationQueryFromWordIdsAndSynsetIds(final SQLiteDatabase connection, final long wordid, final long synsetid, final long word2id, final long synset2id)
	{
		super(connection, CollocationQueryFromWordIdsAndSynsetIds.QUERY);
		setParams(wordid, synsetid, word2id, synset2id);
	}
}
