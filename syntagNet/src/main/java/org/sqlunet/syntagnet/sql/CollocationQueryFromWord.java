/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.syntagnet.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * SyntagNet collocations query from word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class CollocationQueryFromWord extends BaseCollocationQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.SyntagNetCollocationQueryFromWord;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param word       target word
	 */
	public CollocationQueryFromWord(final SQLiteDatabase connection, final String word)
	{
		super(connection, CollocationQueryFromWord.QUERY);
		setParams(word, word);
	}
}
