/*
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 * Created on 31 dec. 2004
 * Filename : LexDomainEnumQueryCommand.java
 * Class encapsulating query for lexdomain enumeration
 */
package org.sqlunet.wordnet.sql;

import org.sqlunet.sql.DBQueryCommand;

import android.database.sqlite.SQLiteDatabase;

/**
 * Query for lexdomain enumeration
 *
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 */
class LexDomainEnumQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.LexDomainEnumQuery; // ;

	/**
	 * Constructor
	 *
	 * @param thisConnection
	 *            is the database connection
	 */
	public LexDomainEnumQueryCommand(final SQLiteDatabase thisConnection)
	{
		super(thisConnection, LexDomainEnumQueryCommand.theQuery);
	}

	/**
	 * Get the lexdomain id from the result set
	 *
	 * @return the lexdomain id value from the result set
	 */
	public int getId()
	{
		return this.cursor.getInt(0);
	}

	/**
	 * Get the lexdomain name (with pos prefix) from the result set
	 *
	 * @return the lexdomain name (with pos prefix) from the result set
	 */
	public String getPosLexDomainName()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get the part-of-speech code value from the result set
	 *
	 * @return the part-of-speech value from the result set (in the range n,v,a,r)
	 */
	public int getPos()
	{
		return this.cursor.getString(2).charAt(0);
	}
}