package org.sqlunet.propbank.sql;

import java.util.Locale;

import org.sqlunet.sql.DBQueryCommand;

import android.database.sqlite.SQLiteDatabase;

/**
 * Query command for VerbNet roles
 *
 * @author Bernard Bou
 */
class PbRoleQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.PropBankRolesQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection
	 *            is the database connection
	 */
	@SuppressWarnings("boxing")
	public PbRoleQueryCommand(final SQLiteDatabase thisConnection, final long thisRoleSetId)
	{
		super(thisConnection, PbRoleQueryCommand.theQuery);
		setParams(thisRoleSetId);
	}

	/**
	 * Set id parameters in prepared SQL statement
	 *
	 * @param thisRoleSetId
	 *            is the target roleset id
	 */
	@SuppressWarnings("unused")
	public void setId(final long thisRoleSetId)
	{
		this.statement.setLong(0, thisRoleSetId);
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
	 * Get role theta
	 *
	 * @return role theta
	 */
	public String getRoleTheta()
	{
		return this.cursor.getString(4);
	}
}
