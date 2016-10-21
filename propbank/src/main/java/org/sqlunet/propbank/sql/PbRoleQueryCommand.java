package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

import java.util.Locale;

/**
 * PropBank roles query command
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class PbRoleQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.PropBankRolesQuery;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 */
	@SuppressWarnings("boxing")
	public PbRoleQueryCommand(final SQLiteDatabase connection, final long roleSetId)
	{
		super(connection, PbRoleQueryCommand.QUERY);
		setParams(roleSetId);
	}

	/**
	 * Set id parameters in prepared SQL statement
	 *
	 * @param roleSetId target role set id
	 */
	@SuppressWarnings("unused")
	public void setId(final long roleSetId)
	{
		this.statement.setLong(0, roleSetId);
	}

	/**
	 * Get the role id from the result set
	 *
	 * @return the role id from the result set
	 */
	public long getRoleId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the role description from the result set
	 *
	 * @return the role description from the result set
	 */
	public String getRoleDescr()
	{
		return this.cursor.getString(1).toLowerCase(Locale.ENGLISH);
	}

	/**
	 * Get role N
	 *
	 * @return role N
	 */
	public String getNArg()
	{
		return this.cursor.getString(2);
	}

	/**
	 * Get role F
	 *
	 * @return role F
	 */
	public String getRoleFunc()
	{
		return this.cursor.getString(3);
	}

	/**
	 * Get role VerbNet theta
	 *
	 * @return role VerbNet theta
	 */
	public String getRoleTheta()
	{
		return this.cursor.getString(4);
	}
}
