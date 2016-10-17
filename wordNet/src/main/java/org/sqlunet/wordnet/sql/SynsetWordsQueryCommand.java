/*
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 * Created on 31 dec. 2004
 * Filename : SynsetWordsQueryCommand.java
 * Class encapsulating query for words in synset
 */
package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * Query command for words in synset
 *
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 */
class SynsetWordsQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.SynsetWordsQuery;

	/**
	 * Constructor
	 *
	 * @param connection is the database connection
	 * @param synsetId   is the target synset id
	 */
	@SuppressWarnings("boxing")
	public SynsetWordsQueryCommand(final SQLiteDatabase connection, final long synsetId)
	{
		super(connection, SynsetWordsQueryCommand.QUERY);
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