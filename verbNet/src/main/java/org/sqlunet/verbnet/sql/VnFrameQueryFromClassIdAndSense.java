/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

import java.sql.Types;

import androidx.annotation.Nullable;

/**
 * VerbNet frame query from sense
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class VnFrameQueryFromClassIdAndSense extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.VerbNetFramesQueryFromClassIdAndSense;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param classId    target class id
	 * @param wordId     target word id
	 * @param synsetId   target synset id
	 */
	@SuppressWarnings("boxing")
	public VnFrameQueryFromClassIdAndSense(final SQLiteDatabase connection, final long classId, final long wordId, @Nullable final Long synsetId)
	{
		super(connection, VnFrameQueryFromClassIdAndSense.QUERY);
		setParams(classId, wordId);
		if (synsetId != null)
		{
			this.statement.setLong(2, synsetId);
		}
		else
		{
			this.statement.setNull(2, Types.DECIMAL);
		}
	}

	/**
	 * Get the frame id from the result set
	 *
	 * @return the frame id from the result set
	 */
	public long getFrameId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the frame number from the result set
	 *
	 * @return the frame number from the result set
	 */
	public String getNumber()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get the frame xtag from the result set
	 *
	 * @return the frame xtag from the result set
	 */
	public String getXTag()
	{
		assert this.cursor != null;
		return this.cursor.getString(2);
	}

	/**
	 * Get the frame major description from the result set
	 *
	 * @return the frame major description from the result set
	 */
	public String getDescription1()
	{
		assert this.cursor != null;
		return this.cursor.getString(3);
	}

	/**
	 * Get the frame minor description from the result set
	 *
	 * @return the frame minor description from the result set
	 */
	public String getDescription2()
	{
		assert this.cursor != null;
		return this.cursor.getString(4);
	}

	/**
	 * Get the frame syntax from the result set
	 *
	 * @return the frame syntax from the result set
	 */
	public String getSyntax()
	{
		assert this.cursor != null;
		return this.cursor.getString(5);
	}

	/**
	 * Get the frame semantics from the result set
	 *
	 * @return the frame semantics from the result set
	 */
	public String getSemantics()
	{
		assert this.cursor != null;
		return this.cursor.getString(6);
	}

	/**
	 * Get the frame example from the result set
	 *
	 * @return the frame example from the result set
	 */
	public String getExample()
	{
		assert this.cursor != null;
		return this.cursor.getString(7);
	}

	/**
	 * Get the frame quality from the result set
	 *
	 * @return the frame quality from the result set
	 */
	public int getQuality()
	{
		assert this.cursor != null;
		return this.cursor.getInt(8);
	}

	/**
	 * Get synset-specific flag from the result set
	 *
	 * @return the synset-specific flag from the result set
	 */
	public boolean getSynsetSpecific()
	{
		assert this.cursor != null;
		final int result = this.cursor.getInt(9);
		return result == 0;
	}
}
