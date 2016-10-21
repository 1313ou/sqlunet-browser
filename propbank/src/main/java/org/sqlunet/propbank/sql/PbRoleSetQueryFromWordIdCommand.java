package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * PropBank role sets query command from word id
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class PbRoleSetQueryFromWordIdCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.PropBankRolesetFromWordId;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 */
	@SuppressWarnings("boxing")
	public PbRoleSetQueryFromWordIdCommand(final SQLiteDatabase connection, final long wordId)
	{
		super(connection, PbRoleSetQueryFromWordIdCommand.QUERY);
		setParams(wordId);
	}

	/**
	 * Get the role set id from the result set
	 *
	 * @return the role set id from the result set
	 */
	public long getRoleSetId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the role set name from the result set
	 *
	 * @return the role set name from the result set
	 */
	public String getRoleSetName()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get role set head
	 *
	 * @return role set head
	 */
	public String getRoleSetHead()
	{
		return this.cursor.getString(2);
	}

	/**
	 * Get role set description
	 *
	 * @return role set description
	 */
	public String getRoleSetDescr()
	{
		return this.cursor.getString(3);
	}
}
