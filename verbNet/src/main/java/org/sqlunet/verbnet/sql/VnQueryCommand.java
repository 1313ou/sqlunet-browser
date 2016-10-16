package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * Query command for VerbNet selector
 *
 * @author Bernard Bou
 */
class VnQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.VerbNetQueryFromWord;

	/**
	 * Constructor
	 *
	 * @param thisConnection is the database connection
	 * @param thisLemma      is the target word
	 */
	public VnQueryCommand(final SQLiteDatabase thisConnection, final String thisLemma)
	{
		super(thisConnection, VnQueryCommand.theQuery);
		setParams(thisLemma);
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
		final int thisResult = this.cursor.getInt(1);
		return thisResult != 0;
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
