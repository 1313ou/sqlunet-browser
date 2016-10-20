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
	 * @param connection database connection
	 * @param wordId     target word id
	 */
	@SuppressWarnings("boxing")
	public PbRoleSetQueryFromWordIdCommand(final SQLiteDatabase connection, final long wordId)
	{
		super(connection, PbRoleSetQueryFromWordIdCommand.QUERY);
		setParams(wordId);
	}

	/**
	 * Get the roleSet id from the result set
	 *
	 * @return the roleSet id from the result set
	 */
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
