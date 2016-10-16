package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * PropBank RoleSets query command
 *
 * @author Bernard Bou
 */
class PbRoleSetQueryFromWordCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.PropBankRolesetFromWord;

	/**
	 * Constructor
	 *
	 * @param thisConnection is the database connection
	 * @param thisWordId     is the target wordid
	 */
	@SuppressWarnings("boxing")
	public PbRoleSetQueryFromWordCommand(final SQLiteDatabase thisConnection, final String thisWordId)
	{
		super(thisConnection, PbRoleSetQueryFromWordCommand.theQuery);
		setParams(thisWordId);
	}

	/**
	 * Get the word id from the result set
	 *
	 * @return the word id from the result set
	 */
	public long getWordId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the roleset id from the result set
	 *
	 * @return the roleset id from the result set
	 */
	public long getRoleSetId()
	{
		return this.cursor.getLong(1);
	}

	/**
	 * Get the roleset name from the result set
	 *
	 * @return the roleset name from the result set
	 */
	public String getRoleSetName()
	{
		return this.cursor.getString(2);
	}

	/**
	 * Get roleset head
	 *
	 * @return roleset head
	 */
	public String getRoleSetHead()
	{
		return this.cursor.getString(3);
	}

	/**
	 * Get roleset description
	 *
	 * @return roleset description
	 */
	public String getRoleSetDescr()
	{
		return this.cursor.getString(4);
	}
}
