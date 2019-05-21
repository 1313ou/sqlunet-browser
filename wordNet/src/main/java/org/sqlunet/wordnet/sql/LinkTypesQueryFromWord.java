/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * Query for link types
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class LinkTypesQueryFromWord extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.LinkTypesQueryFromWord; // ;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 */
	public LinkTypesQueryFromWord(final SQLiteDatabase connection)
	{
		super(connection, LinkTypesQueryFromWord.QUERY);
	}

	/**
	 * Set word parameter in prepared SQL statement
	 *
	 * @param word target word
	 */
	public void setWord(final String word)
	{
		this.statement.setString(0, word);
		this.statement.setString(1, word);
	}

	/**
	 * Get link type
	 *
	 * @return link type
	 */
	public int getLinkType()
	{
		assert this.cursor != null;
		return this.cursor.getInt(0);
	}

	/**
	 * Get lexdomain id
	 *
	 * @return lexdomain id
	 */
	public int getLexDomainId()
	{
		assert this.cursor != null;
		return this.cursor.getInt(1);
	}
}