/*
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 * Created on 31 dec. 2004
 * Filename : SynsetsQueryCommand.java
 * Class encapsulating query for synsets containing a given word
 */
package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * Query command for synsets containing a given word
 *
 * @author Bernard
 */
class SynsetQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.SynsetQuery; // ;

	/**
	 * Constructor
	 *
	 * @param connection database connection
	 * @param synsetId   target synset id
	 */
	@SuppressWarnings("boxing")
	public SynsetQueryCommand(final SQLiteDatabase connection, final long synsetId)
	{
		super(connection, SynsetQueryCommand.QUERY);
		setParams(synsetId);
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
	 * @return samples as a semicolon-separated string
	 */
	public String getSample()
	{
		return this.cursor.getString(3);
	}
}