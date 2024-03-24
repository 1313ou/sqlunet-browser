/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

import androidx.annotation.Nullable;

/**
 * FrameNet lex unit query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnLexUnitQueryFromFnWordId extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.FrameNetLexUnitQueryFromFnWordId;
	static private final String QUERYFN = SqLiteDialect.FnFrameNetLexUnitQueryFromFnWordId;

	/**
	 * <code>QUERYWITHPOS</code> is the SQL statement with Pos input
	 */
	static private final String QUERYWITHPOS = SqLiteDialect.FrameNetLexUnitQueryFromFnWordIdAndPos;
	static private final String QUERYWITHPOSFN = SqLiteDialect.FnFrameNetLexUnitQueryFromFnWordIdAndPos;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param standalone standalone query
	 * @param fnWordId   target fn word id
	 * @param pos        target pos or null
	 */
	@SuppressWarnings("boxing")
	public FnLexUnitQueryFromFnWordId(final SQLiteDatabase connection, final boolean standalone, final long fnWordId, @Nullable final Character pos)
	{
		super(connection, pos != null ? //
				(standalone ? FnLexUnitQueryFromFnWordId.QUERYWITHPOSFN : FnLexUnitQueryFromFnWordId.QUERYWITHPOS) : //
				(standalone ? FnLexUnitQueryFromFnWordId.QUERYFN : FnLexUnitQueryFromFnWordId.QUERY));
		setParams(fnWordId, pos != null ? pos.toString().toUpperCase() : null);
	}

	/**
	 * Get the lex unit id from the result set
	 *
	 * @return the lex unit id from the result set
	 */
	public long getLuId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the lex unit from the result set
	 *
	 * @return the lex unit from the result set
	 */
	public String getLexUnit()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get the pos from the result set
	 *
	 * @return the pos from the result set
	 */
	public String getPos()
	{
		assert this.cursor != null;
		return this.cursor.getString(2);
	}

	/**
	 * Get the definition from the result set
	 *
	 * @return the definition from the result set
	 */
	public String getDefinition()
	{
		assert this.cursor != null;
		return this.cursor.getString(3);
	}

	/**
	 * Get the definition dictionary from the result set
	 *
	 * @return the definition dictionary from the result set
	 */
	public String getDictionary()
	{
		assert this.cursor != null;
		return this.cursor.getString(4);
	}

	/**
	 * Get the incorporated FE from the result set
	 *
	 * @return the incorporated FE from the result set or null if none
	 */
	public String getIncorporatedFe()
	{
		assert this.cursor != null;
		return this.cursor.getString(5);
	}

	/**
	 * Get the frame id from the result set
	 *
	 * @return the frame id from the result set
	 */
	public long getFrameId()
	{
		assert this.cursor != null;
		return this.cursor.getInt(6);
	}

	/**
	 * Get the frame from the result set
	 *
	 * @return the frame from the result set or null if none
	 */
	public String getFrame()
	{
		assert this.cursor != null;
		return this.cursor.getString(7);
	}

	/**
	 * Get the frame description from the result set
	 *
	 * @return the frame description from the result set or null if none
	 */
	public String getFrameDescription()
	{
		assert this.cursor != null;
		return this.cursor.getString(8);
	}
}
