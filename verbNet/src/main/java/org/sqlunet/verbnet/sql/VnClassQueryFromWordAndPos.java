package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * Query for VerbNet selector
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class VnClassQueryFromWordAndPos extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.VerbNetClassQueryFromWordAndPos;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param word       target word
	 */
	public VnClassQueryFromWordAndPos(final SQLiteDatabase connection, final String word)
	{
		super(connection, VnClassQueryFromWordAndPos.QUERY);
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
	 * Get the synset-specific tag from the result set
	 *
	 * @return the synset-specific tag from the result set
	 */
	public boolean getSynsetSpecific()
	{
		final int result = this.cursor.getInt(1);
		return result != 0;
	}

	/**
	 * Get the synset id from the result set
	 *
	 * @return the synset id from the result set
	 */
	public long getSynsetId()
	{
		return this.cursor.getLong(2);
	}

	/**
	 * Get synset definition
	 *
	 * @return definition
	 */
	public String getDefinition()
	{
		return this.cursor.getString(3);
	}

	/**
	 * Get synset lexdomain id
	 *
	 * @return synset lexdomain id
	 */
	public int getLexDomainId()
	{
		return this.cursor.getInt(4);
	}
}
