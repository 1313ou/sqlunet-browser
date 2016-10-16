/*
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 * Created on 31 dec. 2004
 * Filename : TypedLinksQueryCommand.java
 * Class encapsulating query for synsets linked through a given relation type
 */
package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * Query command for synsets linked through a given relation type
 *
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 */
class TypedLinksQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.TypedLinksQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection is the database connection
	 */
	public TypedLinksQueryCommand(final SQLiteDatabase thisConnection)
	{
		super(thisConnection, TypedLinksQueryCommand.theQuery);
	}

	/**
	 * Set source synset parameter in prepared statement
	 *
	 * @param thisSynset is the source synset id
	 */
	public void setFromSynset(final long thisSynset)
	{
		this.statement.setLong(0, thisSynset);
		this.statement.setLong(2, thisSynset);
	}

	/**
	 * Set source word parameter in prepared statement
	 *
	 * @param thisWordId is the source word id (for lexical links) or -1 if word is any in which case the query returns all lexical links whatever the word
	 */
	public void setFromWord(final long thisWordId)
	{
		this.statement.setLong(4, thisWordId);
		this.statement.setLong(5, thisWordId);
	}

	/**
	 * Set source type parameter in prepared statement
	 *
	 * @param thisType is the target synset type
	 */
	public void setLinkType(final int thisType)
	{
		this.statement.setInt(1, thisType);
		this.statement.setInt(3, thisType);
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