/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * FrameNet frame element query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnFrameElementQueryFromFrameId extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.FrameNetFEQueryFromFrameId;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param frameId    target frame id
	 */
	@SuppressWarnings("boxing")
	public FnFrameElementQueryFromFrameId(final SQLiteDatabase connection, final long frameId)
	{
		super(connection, FnFrameElementQueryFromFrameId.QUERY);
		setParams(frameId);
	}

	/**
	 * Get the fe type id from the result set
	 *
	 * @return the fe type id from the result set
	 */
	public long getFETypeId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the fe type from the result set
	 *
	 * @return the fe type from the result set
	 */
	public String getFEType()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get the fe id from the result set
	 *
	 * @return the fe id from the result set
	 */
	public long getFEId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(2);
	}

	/**
	 * Get the fe definition from the result set
	 *
	 * @return the fe definition from the result set
	 */
	public String getFEDefinition()
	{
		assert this.cursor != null;
		return this.cursor.getString(3);
	}

	/**
	 * Get the fe abbrev from the result set
	 *
	 * @return the fe abbrev from the result set
	 */
	public String getFEAbbrev()
	{
		assert this.cursor != null;
		return this.cursor.getString(4);
	}

	/**
	 * Get the fe core type from the result set
	 *
	 * @return the fe core type from the result set
	 */
	public String getFECoreType()
	{
		assert this.cursor != null;
		return this.cursor.getString(5);
	}

	/**
	 * Get the fe sem types from the result set
	 *
	 * @return the fe sem types from the result set
	 */
	public String getSemTypes()
	{
		assert this.cursor != null;
		return this.cursor.getString(6);
	}

	/**
	 * Get the fe core membership type from the result set
	 *
	 * @return the fe core membership from the result set
	 */
	public boolean getIsCore()
	{
		assert this.cursor != null;
		return this.cursor.getInt(7) != 0;
	}

	/**
	 * Get the fe core set from the result set
	 *
	 * @return the fe coreset from the result set or 0 if node
	 */
	public int getCoreSet()
	{
		assert this.cursor != null;
		return this.cursor.getInt(8);
	}
}
