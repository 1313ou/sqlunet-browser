/*
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
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
class SynsetsQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.SynsetsQuery; // ;

	/**
	 * Constructor
	 *
	 * @param thisConnection is the database connection
	 * @param thisWordId     is the target word id
	 */
	@SuppressWarnings("boxing")
	public SynsetsQueryCommand(final SQLiteDatabase thisConnection, final long thisWordId)
	{
		super(thisConnection, SynsetsQueryCommand.theQuery);
		setParams(thisWordId);
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