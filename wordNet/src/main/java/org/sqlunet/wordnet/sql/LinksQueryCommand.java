package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * Query command for linked synsets
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class LinksQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> SQL statement
	 */
	private static final String QUERY = SqLiteDialect.LinksQuery; // ;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 */
	public LinksQueryCommand(final SQLiteDatabase connection)
	{
		super(connection, LinksQueryCommand.QUERY);
	}

	/**
	 * Set source synset parameter in prepared statement
	 *
	 * @param synsetId synset id
	 */
	public void setFromSynset(final long synsetId)
	{
		this.statement.setLong(0, synsetId);
		this.statement.setLong(1, synsetId);
	}

	/**
	 * Set source word parameter in prepared statement
	 *
	 * @param wordId word id or 0 if word is any in which case the query returns all lexical links whatever the word
	 */
	public void setFromWord(final long wordId)
	{
		this.statement.setLong(2, wordId);
		this.statement.setLong(3, wordId);
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