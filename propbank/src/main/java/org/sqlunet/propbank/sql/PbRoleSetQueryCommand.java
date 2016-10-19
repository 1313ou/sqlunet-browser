package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * PropBank role sets query command
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class PbRoleSetQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.PropBankRoleset;

	/**
	 * Constructor
	 *
	 * @param connection is the database connection
	 * @param roleSetId  is the target roleset id
	 */
	@SuppressWarnings("boxing")
	public PbRoleSetQueryCommand(final SQLiteDatabase connection, final long roleSetId)
	{
		super(connection, PbRoleSetQueryCommand.QUERY);
		setParams(roleSetId);
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
