/*
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
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
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 */
class LinkTypesQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.LinkTypesQuery; // ;

	/**
	 * Constructor
	 *
	 * @param thisConnection is the database connection
	 */
	public LinkTypesQueryCommand(final SQLiteDatabase thisConnection)
	{
		super(thisConnection, LinkTypesQueryCommand.theQuery);
	}

	/**
	 * Set word parameter in prepared SQL statement
	 *
	 * @param thisWord is the target word
	 */
	public void setWord(final String thisWord)
	{
		this.statement.setString(0, thisWord);
		this.statement.setString(1, thisWord);
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