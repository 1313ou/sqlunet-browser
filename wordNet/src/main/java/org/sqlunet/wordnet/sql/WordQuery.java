/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * Word query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class WordQuery extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.WordQuery;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param wordId     is the word id
	 */
	public WordQuery(final SQLiteDatabase connection, final long wordId)
	{
		super(connection, WordQuery.QUERY);
		setParams(wordId);
	}

	/**
	 * Get the word id from the result set
	 *
	 * @return the word id value from the result set
	 */
	public int getId()
	{
		assert this.cursor != null;
		return this.cursor.getInt(0);
	}

	/**
	 * Get the word from the result set
	 *
	 * @return the word string value from the result set
	 */
	public String getWord()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}
}
