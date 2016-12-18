package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * Query for link types
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class LinkTypesQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.LinkTypesQuery; // ;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 */
	public LinkTypesQueryCommand(final SQLiteDatabase connection)
	{
		super(connection, LinkTypesQueryCommand.QUERY);
	}

	/**
	 * Set word parameter in prepared SQL statement
	 *
	 * @param word target word
	 */
	public void setWord(final String word)
	{
		this.statement.setString(0, word);
		this.statement.setString(1, word);
	}

	/**
	 * Get link type
	 *
	 * @return link type
	 */
	public int getLinkType()
	{
		return this.cursor.getInt(0);
	}

	/**
	 * Get lexdomain id
	 *
	 * @return lexdomain id
	 */
	public int getLexDomainId()
	{
		return this.cursor.getInt(1);
	}
}