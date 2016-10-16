package org.sqlunet.verbnet.sql;

import java.sql.Types;

import org.sqlunet.sql.DBQueryCommand;

import android.database.sqlite.SQLiteDatabase;

/**
 * Query command for VerbNet roles
 *
 * @author Bernard Bou
 */
class VnRoleQueryFromSenseCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.VerbNetThematicRolesFromClassAndSenseQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection
	 *            is the database connection
	 * @param thisClassId
	 *            is the target classid
	 * @param thisWordId
	 *            is the target wordid
	 * @param thisSynsetId
	 *            is the target synsetid
	 */
	@SuppressWarnings("boxing")
	public VnRoleQueryFromSenseCommand(final SQLiteDatabase thisConnection, final long thisClassId, final long thisWordId, final Long thisSynsetId)
	{
		super(thisConnection, VnRoleQueryFromSenseCommand.theQuery);
		setParams(thisClassId, thisWordId);
		if (thisSynsetId != null)
		{
			this.statement.setLong(2, thisSynsetId);
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
	@SuppressWarnings("unused")
	public long getRoleId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the role type id from the result set
	 *
	 * @return the role type id from the result set
	 */
	@SuppressWarnings("unused")
	public long getRoleTypeId()
	{
		return this.cursor.getLong(1);
	}

	/**
	 * Get the role type from the result set
	 *
	 * @return the role type from the result set
	 */
	public String getRoleType()
	{
		return this.cursor.getString(2);
	}

	/**
	 * Get the role selectional restriction from the result set
	 *
	 * @return the role selectional restriction from the result set
	 */
	public String getSelectionRestriction()
	{
		return this.cursor.getString(3);
	}

	/**
	 * Get the quality from the result set
	 *
	 * @return the quality from the result set
	 */
	@SuppressWarnings("unused")
	public int getQuality()
	{
		return this.cursor.getInt(4);
	}

	/**
	 * Get synset-specific flag from the result set
	 *
	 * @return the synset-specific flag from the result set
	 */
	@SuppressWarnings("unused")
	public boolean getSynsetSpecific()
	{
		final int thisResult = this.cursor.getInt(5);
		return thisResult == 0;
	}
}
