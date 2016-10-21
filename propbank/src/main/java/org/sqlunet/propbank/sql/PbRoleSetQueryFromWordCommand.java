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
	 * @param connection connection
	 * @param word       target word
	 */
	public PbRoleSetQueryFromWordCommand(final SQLiteDatabase connection, final String word)
	{
		super(connection, PbRoleSetQueryFromWordCommand.QUERY);
		setParams(word);
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
	 * Get the role set id from the result set
	 *
	 * @return the role set id from the result set
	 */
	public long getRoleSetId()
	{
		return this.cursor.getLong(1);
	}

	/**
	 * Get the role set name from the result set
	 *
	 * @return the role set name from the result set
	 */
	public String getRoleSetName()
	{
		return this.cursor.getString(2);
	}

	/**
	 * Get role set head
	 *
	 * @return role set head
	 */
	public String getRoleSetHead()
	{
		return this.cursor.getString(3);
	}

	/**
	 * Get role set description
	 *
	 * @return role set description
	 */
	public String getRoleSetDescr()
	{
		return this.cursor.getString(4);
	}
}
