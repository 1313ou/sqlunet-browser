package org.sqlunet.verbnet.sql;

import org.sqlunet.sql.DBQueryCommand;

import android.database.sqlite.SQLiteDatabase;

/**
 * Query command for VerbNet roles
 *
 * @author Bernard Bou
 */
class VnRoleQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.VerbNetThematicRolesQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection
	 *            is the database connection
	 * @param thisClassId
	 *            is the target classid
	 */
	@SuppressWarnings("boxing")
	public VnRoleQueryCommand(final SQLiteDatabase thisConnection, final long thisClassId)
	{
		super(thisConnection, VnRoleQueryCommand.theQuery);
		setParams(thisClassId);
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
}
