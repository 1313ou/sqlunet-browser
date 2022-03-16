/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * Word query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class WordQueryFromWord extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.WordQueryFromWord;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param word       is the word
	 */
	public WordQueryFromWord(final SQLiteDatabase connection, final String word)
	{
		super(connection, WordQueryFromWord.QUERY);
		setParams(word);
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
