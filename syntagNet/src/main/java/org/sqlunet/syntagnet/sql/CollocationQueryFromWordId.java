/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * SyntagNet Collocation query from word id
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class CollocationQueryFromWordId extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.SyntagNetRoleSetQueryFromWordId;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 */
	@SuppressWarnings("boxing")
	public CollocationQueryFromWordId(final SQLiteDatabase connection, final long wordId)
	{
		super(connection, CollocationQueryFromWordId.QUERY);
		setParams(wordId);
	}

	/**
	 * Get the word 1 id from the result set
	 *
	 * @return the word 1 id from the result set
	 */
	public long getWord1Id()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the word 2 id from the result set
	 *
	 * @return the word 2 id from the result set
	 */
	public long getWord2Id()
	{
		assert this.cursor != null;
		return this.cursor.getLong(1);
	}

	/**
	 * Get the synset 1  id from the result set
	 *
	 * @return the synset 1 id from the result set
	 */
	public long getSynset1Id()
	{
		assert this.cursor != null;
		return this.cursor.getLong(2);
	}

	/**
	 * Get the synset 2 id from the result set
	 *
	 * @return the synset 2 id from the result set
	 */
	public long getSynset2Id()
	{
		assert this.cursor != null;
		return this.cursor.getLong(3);
	}

	/**
	 * Get the word 1 from the result set
	 *
	 * @return the word 1 from the result set
	 */
	public String getWord1()
	{
		assert this.cursor != null;
		return this.cursor.getString(4);
	}

	/**
	 * Get word 2 from the result set
	 *
	 * @return word 2 from the result set
	 */
	public String getWord2()
	{
		assert this.cursor != null;
		return this.cursor.getString(5);
	}
}