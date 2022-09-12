/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * FrameNet sentence query from lex unit
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnSentenceQueryFromLexUnitId extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.FrameNetSentencesQueryFromLexUnitId;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param luId       target lex unit id
	 */
	@SuppressWarnings("boxing")
	public FnSentenceQueryFromLexUnitId(final SQLiteDatabase connection, final long luId)
	{
		super(connection, FnSentenceQueryFromLexUnitId.QUERY);
		setParams(luId);
	}

	/**
	 * Get the governor id from the result set
	 *
	 * @return the governor id from the result set
	 */
	public long getSentenceId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the text from the result set
	 *
	 * @return the text from the result set
	 */
	public String getText()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}
}