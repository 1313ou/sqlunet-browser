package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * PropBank role sets query command from word
 *
 * @author Bernard Bou
 */
class PbRoleSetQueryFromWordCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.PropBankRolesetFromWord;

	/**
	 * Constructor
	 *
	 * @param connection is the database connection
	 * @param wordId     is the target wordid
	 */
	public PbRoleSetQueryFromWordCommand(final SQLiteDatabase connection, final String wordId)
	{
		super(connection, PbRoleSetQueryFromWordCommand.QUERY);
		setParams(wordId);
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
