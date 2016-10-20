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
	 * @param connection database connection
	 * @param roleSetId  target roleSet id
	 */
	@SuppressWarnings("boxing")
	public PbRoleSetQueryCommand(final SQLiteDatabase connection, final long roleSetId)
	{
		super(connection, PbRoleSetQueryCommand.QUERY);
		setParams(roleSetId);
	}

	/**
	 * Get the roleSet id from the result set
	 *
	 * @return the roleSet id from the result set
	 */
	@SuppressWarnings("unused")
	public long getRoleSetId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the roleSet name from the result set
	 *
	 * @return the roleSet name from the result set
	 */
	public String getRoleSetName()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get roleSet head
	 *
	 * @return roleSet head
	 */
	public String getRoleSetHead()
	{
		return this.cursor.getString(2);
	}

	/**
	 * Get roleSet description
	 *
	 * @return roleSet description
	 */
	public String getRoleSetDescr()
	{
		return this.cursor.getString(3);
	}
}
