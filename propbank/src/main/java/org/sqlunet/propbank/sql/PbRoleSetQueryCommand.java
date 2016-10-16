package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * PropBank RoleSets query command
 *
 * @author Bernard Bou
 */
class PbRoleSetQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.PropBankRoleset;

	/**
	 * Constructor
	 *
	 * @param thisConnection is the database connection
	 * @param thisRoleSetId  is the target roleset id
	 */
	@SuppressWarnings("boxing")
	public PbRoleSetQueryCommand(final SQLiteDatabase thisConnection, final long thisRoleSetId)
	{
		super(thisConnection, PbRoleSetQueryCommand.theQuery);
		setParams(thisRoleSetId);
	}

	/**
	 * Get the roleset id from the result set
	 *
	 * @return the roleset id from the result set
	 */
	@SuppressWarnings("unused")
	public long getRoleSetId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the roleset name from the result set
	 *
	 * @return the roleset name from the result set
	 */
	public String getRoleSetName()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get roleset head
	 *
	 * @return roleset head
	 */
	public String getRoleSetHead()
	{
		return this.cursor.getString(2);
	}

	/**
	 * Get roleset description
	 *
	 * @return roleset description
	 */
	public String getRoleSetDescr()
	{
		return this.cursor.getString(3);
	}
}
