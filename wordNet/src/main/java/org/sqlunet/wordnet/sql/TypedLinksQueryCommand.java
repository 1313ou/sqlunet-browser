package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * Query command for synsets linked through a given relation type
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class TypedLinksQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.TypedLinksQuery;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 */
	public TypedLinksQueryCommand(final SQLiteDatabase connection)
	{
		super(connection, TypedLinksQueryCommand.QUERY);
	}

	/**
	 * Set source synset parameter in prepared statement
	 *
	 * @param synsetId is the source synset id
	 */
	public void setFromSynset(final long synsetId)
	{
		this.statement.setLong(0, synsetId);
		this.statement.setLong(2, synsetId);
	}

	/**
	 * Set source word parameter in prepared statement
	 *
	 * @param wordId is the source word id (for lexical links) or -1 if word is any in which case the query returns all lexical links whatever the word
	 */
	public void setFromWord(final long wordId)
	{
		this.statement.setLong(4, wordId);
		this.statement.setLong(5, wordId);
	}

	/**
	 * Set source type parameter in prepared statement
	 *
	 * @param type target synset type
	 */
	public void setLinkType(final int type)
	{
		this.statement.setInt(1, type);
		this.statement.setInt(3, type);
	}

	// linkid, synsetid, definition, lexdomainid, sampleset, word2id, lemma, synset1id, word1id

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
	 * Get synset id
	 *
	 * @return synset id
	 */
	public long getSynsetId()
	{
		return this.cursor.getLong(1);
	}

	/**
	 * Get synset definition
	 *
	 * @return definition
	 */
	public String getDefinition()
	{
		return this.cursor.getString(2);
	}

	/**
	 * Get synset lexdomain id
	 *
	 * @return synset lexdomain id
	 */
	public int getLexDomainId()
	{
		return this.cursor.getInt(3);
	}

	/**
	 * Get sample data
	 *
	 * @return samples in a bar-separated string
	 */
	public String getSamples()
	{
		return this.cursor.getString(4);
	}

	/**
	 * Get target word id
	 *
	 * @return source synset id
	 */
	public long getWordId()
	{
		return this.cursor.getLong(5);
	}

	/**
	 * Get target word
	 *
	 * @return source synset id
	 */
	public String getWord()
	{
		return this.cursor.getString(6);
	}

	/**
	 * Get source synset id
	 *
	 * @return source synset id
	 */
	public long getFromSynset()
	{
		return this.cursor.getLong(7);
	}

	/**
	 * Get source synset id
	 *
	 * @return source synset id
	 */
	public long getFromWord()
	{
		return this.cursor.getLong(8);
	}
}