/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * FrameNet frame query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnFrameQuery extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.FrameNetFrameQuery;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param frameId    target frame id
	 */
	@SuppressWarnings("boxing")
	public FnFrameQuery(final SQLiteDatabase connection, final long frameId)
	{
		super(connection, FnFrameQuery.QUERY);
		setParams(frameId);
	}

	/**
	 * Get the annoSetId from the result set
	 *
	 * @return the annoSetId from the result set
	 */
	public long getFrameId()
	{
		assert this.cursor != null;
		return this.cursor.getInt(0);
	}

	/**
	 * Get the frame from the result set
	 *
	 * @return the frame from the result set or null if none
	 */
	public String getFrame()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get the frame description from the result set
	 *
	 * @return the frame description from the result set or null if none
	 */
	public String getFrameDescription()
	{
		assert this.cursor != null;
		return this.cursor.getString(2);
	}

	/**
	 * Get the semtypes from the result set
	 *
	 * @return the the semtypes from the result set or null if none
	 */
	public String getSemTypes()
	{
		assert this.cursor != null;
		return this.cursor.getString(3);
	}

	/**
	 * Get the related frames from the result set
	 *
	 * @return the related frames from the result set or null if none
	 */
	public String getRelatedFrames()
	{
		assert this.cursor != null;
		return this.cursor.getString(4);
	}
}
