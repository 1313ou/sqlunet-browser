package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * PropBank RoleSets query command
 *
 * @author Bernard Bou
 */
class PbRoleSetQueryFromWordIdCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.PropBankRolesetFromWordId;

	/**
	 * Constructor
	 *
	 * @param thisConnection is the database connection
	 * @param thisWordId     is the target wordid
	 */
	@SuppressWarnings("boxing")
	public PbRoleSetQueryFromWordIdCommand(final SQLiteDatabase thisConnection, final long thisWordId)
	{
		super(thisConnection, PbRoleSetQueryFromWordIdCommand.theQuery);
		setParams(thisWordId);
	}

	/**
	 * Get the roleset id from the result set
	 *
	 * @return the roleset id from the result set
	 */
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
