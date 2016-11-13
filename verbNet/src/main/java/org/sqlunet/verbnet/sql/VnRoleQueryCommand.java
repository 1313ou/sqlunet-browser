package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * Query command for VerbNet roles
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class VnRoleQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.VerbNetThematicRolesQuery;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param classId    target classId
	 */
	@SuppressWarnings("boxing")
	public VnRoleQueryCommand(final SQLiteDatabase connection, final long classId)
	{
		super(connection, VnRoleQueryCommand.QUERY);
		setParams(classId);
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
