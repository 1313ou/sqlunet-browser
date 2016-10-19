/*
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 * Created on 31 dec. 2004
 * Filename : LinksQueryCommand.java
 * Class encapsulating query for link types of a given word
 */
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
	private static final String QUERY = SqLiteDialect.LinkTypesQuery; // ;

	/**
	 * Constructor
	 *
	 * @param connection is the database connection
	 */
	public LinkTypesQueryCommand(final SQLiteDatabase connection)
	{
		super(connection, LinkTypesQueryCommand.QUERY);
	}

	/**
	 * Set word parameter in prepared SQL statement
	 *
	 * @param word is the target word
	 */
	public void setWord(final String word)
	{
		this.statement.setString(0, word);
		this.statement.setString(1, word);
	}

	/**
	 * Get lexdomain id
	 *
	 * @return lexdomain id
	 */
	public int getLexDomainId()
	{
		return this.cursor.getInt(0);
	}

	/**
	 * Get link type
	 *
	 * @return link type
	 */
	public int getLinkType()
	{
		return this.cursor.getInt(1);
	}
}