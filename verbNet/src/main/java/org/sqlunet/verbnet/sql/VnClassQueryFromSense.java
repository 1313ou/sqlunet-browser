/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * VerbNet class-with-sense query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class VnClassQueryFromSense extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.VerbNetClassQueryFromSense;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @param synsetId   target synset id (null corresponds to no value)
	 */
	@SuppressWarnings("boxing")
	public VnClassQueryFromSense(final SQLiteDatabase connection, final long wordId, final Long synsetId)
	{
		super(connection, VnClassQueryFromSense.QUERY);
		setParams(wordId, synsetId);
	}

	/**
	 * Get the class id from the result set
	 *
	 * @return the class id from the result set
	 */
	public long getClassId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the class name from the result set
	 *
	 * @return the class name from the result set
	 */
	public String getClassName()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get the frame xtag from the result set
	 *
	 * @return the frame xtag from the result set
	 */
	public boolean getSynsetSpecific()
	{
		assert this.cursor != null;
		return this.cursor.getInt(2) == 0;
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
	 * Get the sensenum from the result set
	 *
	 * @return the sensenum from the result set
	 */
	public int getSenseNum()
	{
		assert this.cursor != null;
		return this.cursor.getInt(4);
	}

	/**
	 * Get the sensekey from the result set
	 *
	 * @return the sensekey from the result set
	 */
	public String getSenseKey()
	{
		assert this.cursor != null;
		return this.cursor.getString(5);
	}

	/**
	 * Get the quality from the result set
	 *
	 * @return the quality from the result set
	 */
	public float getQuality()
	{
		assert this.cursor != null;
		return this.cursor.getFloat(6);
	}

	/**
	 * Get the groupings from the result set
	 *
	 * @return the (|-separated) groupings from the result set
	 */
	public String getGroupings()
	{
		assert this.cursor != null;
		return this.cursor.getString(7);
	}
}
