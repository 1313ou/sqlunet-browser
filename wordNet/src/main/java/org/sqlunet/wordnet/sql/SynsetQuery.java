/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * Query for synsets containing a given word
 *
 * @author Bernard
 */
class SynsetQuery extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.SynsetQuery; // ;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param synsetId   target synset id
	 */
	@SuppressWarnings("boxing")
	public SynsetQuery(final SQLiteDatabase connection, final long synsetId)
	{
		super(connection, SynsetQuery.QUERY);
		setParams(synsetId);
	}

	/**
	 * Get synset id
	 *
	 * @return synset id
	 */
	public long getSynsetId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get synset definition
	 *
	 * @return definition
	 */
	public String getDefinition()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get synset domain id
	 *
	 * @return synset domain id
	 */
	public int getDomainId()
	{
		assert this.cursor != null;
		return this.cursor.getInt(2);
	}

	/**
	 * Get sample data
	 *
	 * @return samples as a semicolon-separated string
	 */
	public String getSample()
	{
		assert this.cursor != null;
		return this.cursor.getString(3);
	}
}