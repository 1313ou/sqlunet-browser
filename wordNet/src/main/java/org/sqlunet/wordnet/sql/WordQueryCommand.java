/*
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 * Created on 26 mars 2005
 * Filename : WordQueryCommand.java
 */
package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * Word query command
 *
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 */
class WordQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.WordQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection is the database connection
	 * @param thisLemma      is the word lemma
	 */
	public WordQueryCommand(final SQLiteDatabase thisConnection, final String thisLemma)
	{
		super(thisConnection, WordQueryCommand.theQuery);
		setParams(thisLemma);
	}

	/**
	 * Get the word id from the result set
	 *
	 * @return the word id value from the result set
	 */
	public int getId()
	{
		return this.cursor.getInt(0);
	}

	/**
	 * Get the word lemma from the result set
	 *
	 * @return the lemma string value from the result set
	 */
	public String getLemma()
	{
		return this.cursor.getString(1);
	}
}
