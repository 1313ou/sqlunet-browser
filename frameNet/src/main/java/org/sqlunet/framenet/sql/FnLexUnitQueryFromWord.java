/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * FrameNet lex unit query from word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnLexUnitQueryFromWord extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.FrameNetLexUnitQueryFromWord;
	static private final String QUERYFN = SqLiteDialect.FnFrameNetLexUnitQueryFromWord;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param standalone standalone query
	 * @param word       target word
	 */
	public FnLexUnitQueryFromWord(final SQLiteDatabase connection, final boolean standalone, final String word)
	{
		super(connection, standalone ? FnLexUnitQueryFromWord.QUERYFN : FnLexUnitQueryFromWord.QUERY);
		setParams(word);
	}

	/**
	 * Get the word id from the result set
	 *
	 * @return the word id from the result set
	 */
	public long getWordId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the lexical unit id from the result set
	 *
	 * @return the lexical unit id from the result set
	 */
	public long getLuId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(1);
	}

	/**
	 * Get the lex unit from the result set
	 *
	 * @return the lex unit from the result set
	 */
	public String getLexUnit()
	{
		assert this.cursor != null;
		return this.cursor.getString(2);
	}

	/**
	 * Get the pos from the result set
	 *
	 * @return the pos from the result set
	 */
	public String getPos()
	{
		assert this.cursor != null;
		return this.cursor.getString(3);
	}

	/**
	 * Get the lex unit definition from the result set
	 *
	 * @return the lex unit definition from the result set
	 */
	public String getLexUnitDefinition()
	{
		assert this.cursor != null;
		return this.cursor.getString(4);
	}

	/**
	 * Get the lex unit dictionary from the result set
	 *
	 * @return the lex unit dictionary from the result set
	 */
	public String getLexUnitDictionary()
	{
		assert this.cursor != null;
		return this.cursor.getString(5);
	}

	/**
	 * Get the incorporated fe from the result set
	 *
	 * @return the incorporated fe from the result set
	 */
	public String getIncorporatedFe()
	{
		assert this.cursor != null;
		return this.cursor.getString(6);
	}

	/**
	 * Get the frame id from the result set
	 *
	 * @return the frame id from the result set
	 */
	public long getFrameId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(7);
	}

	/**
	 * Get the frame
	 *
	 * @return the frame
	 */
	public String getFrame()
	{
		assert this.cursor != null;
		return this.cursor.getString(8);
	}

	/**
	 * Get the frame definition
	 *
	 * @return the frame definition
	 */
	public String getFrameDefinition()
	{
		assert this.cursor != null;
		return this.cursor.getString(9);
	}
}
