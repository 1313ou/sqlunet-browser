/*
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 * Created on 31 dec. 2004
 * Filename : LinksQueryCommand.java
 * Class encapsulating query for all linked synsets
 */
package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * Query command for linked synsets
 *
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 */
class LinksQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.LinksQuery; // ;

	/**
	 * Constructor
	 *
	 * @param thisConnection is the database connection
	 */
	public LinksQueryCommand(final SQLiteDatabase thisConnection)
	{
		super(thisConnection, LinksQueryCommand.theQuery);
	}

	/**
	 * Set source synset parameter in prepared statement
	 *
	 * @param thisSynsetId is the synset id
	 */
	public void setFromSynset(final long thisSynsetId)
	{
		this.statement.setLong(0, thisSynsetId);
		this.statement.setLong(1, thisSynsetId);
	}

	/**
	 * Set source word parameter in prepared statement
	 *
	 * @param thisWordId is the word id or -1 if word is any in which case the query returns all lexical links whatever the word
	 */
	public void setFromWord(final long thisWordId)
	{
		this.statement.setLong(2, thisWordId);
		this.statement.setLong(3, thisWordId);
	}

	/**
	 * Get synset id
	 *
	 * @return synset id
	 */
	public long getSynsetId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get synset definition
	 *
	 * @return definition
	 */
	public String getDefinition()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get synset lexdomain id
	 *
	 * @return synset lexdomain id
	 */
	public int getLexDomainId()
	{
		return this.cursor.getInt(2);
	}

	/**
	 * Get sample data
	 *
	 * @return samples in a bar-separated string
	 */
	public String getSample()
	{
		return this.cursor.getString(3);
	}

	/**
	 * Get link type
	 *
	 * @return link type
	 */
	public int getLinkType()
	{
		return this.cursor.getInt(4);
	}

	/**
	 * Get source synset id
	 *
	 * @return source synset id
	 */
	public long getFromSynset()
	{
		return this.cursor.getLong(5);
	}
}