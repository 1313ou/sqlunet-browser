package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * Query for words in synset
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class SynsetWordsQuery extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.SynsetWordsQuery;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param synsetId   target synset id
	 */
	@SuppressWarnings("boxing")
	public SynsetWordsQuery(final SQLiteDatabase connection, final long synsetId)
	{
		super(connection, SynsetWordsQuery.QUERY);
		setParams(synsetId);
	}

	/**
	 * Get word in result set
	 *
	 * @return word in the result set
	 */
	public String getLemma()
	{
		return this.cursor.getString(0);
	}

	/**
	 * Get database id
	 *
	 * @return database id in the result set
	 */
	public long getId()
	{
		return this.cursor.getLong(1);
	}
}