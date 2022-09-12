/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

import java.sql.Types;

import androidx.annotation.Nullable;

/**
 * Query for VerbNet roles from sense
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class VnRoleQueryFromClassIdAndSense extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.VerbNetThematicRolesQueryFromClassIdAndSense;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param classId    target class id
	 * @param wordId     target word id
	 * @param synsetId   target synset id
	 */
	@SuppressWarnings("boxing")
	public VnRoleQueryFromClassIdAndSense(final SQLiteDatabase connection, final long classId, final long wordId, @Nullable final Long synsetId)
	{
		super(connection, VnRoleQueryFromClassIdAndSense.QUERY);
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
	 * Get the role id from the result set
	 *
	 * @return the role id from the result set
	 */
	public long getRoleId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the role type id from the result set
	 *
	 * @return the role type id from the result set
	 */
	public long getRoleTypeId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(1);
	}

	/**
	 * Get the role type from the result set
	 *
	 * @return the role type from the result set
	 */
	public String getRoleType()
	{
		assert this.cursor != null;
		return this.cursor.getString(2);
	}

	/**
	 * Get the role selectional restriction from the result set
	 *
	 * @return the role selectional restriction from the result set
	 */
	public String getSelectionRestriction()
	{
		assert this.cursor != null;
		return this.cursor.getString(3);
	}

	/**
	 * Get the quality from the result set
	 *
	 * @return the quality from the result set
	 */
	public int getQuality()
	{
		assert this.cursor != null;
		return this.cursor.getInt(4);
	}

	/**
	 * Get synset-specific flag from the result set
	 *
	 * @return the synset-specific flag from the result set
	 */
	public boolean getSynsetSpecific()
	{
		assert this.cursor != null;
		final int result = this.cursor.getInt(5);
		return result == 0;
	}
}
