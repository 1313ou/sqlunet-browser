package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * PropBank role sets query command from word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
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
	 * @param connection database connection
	 * @param wordId     target word id
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
	 * Get the roleSet id from the result set
	 *
	 * @return the roleSet id from the result set
	 */
	public long getRoleSetId()
	{
		return this.cursor.getLong(1);
	}

	/**
	 * Get the roleSet name from the result set
	 *
	 * @return the roleSet name from the result set
	 */
	public String getRoleSetName()
	{
		return this.cursor.getString(2);
	}

	/**
	 * Get roleSet head
	 *
	 * @return roleSet head
	 */
	public String getRoleSetHead()
	{
		return this.cursor.getString(3);
	}

	/**
	 * Get roleSet description
	 *
	 * @return roleSet description
	 */
	public String getRoleSetDescr()
	{
		return this.cursor.getString(4);
	}
}
