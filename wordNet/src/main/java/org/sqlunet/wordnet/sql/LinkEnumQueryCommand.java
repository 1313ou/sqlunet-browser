/*
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 * Created on 31 dec. 2004
 * Filename : LinkEnumQueryCommand.java
 * Class encapsulating query for link enumeration
 */
package org.sqlunet.wordnet.sql;

import org.sqlunet.sql.DBQueryCommand;

import android.database.sqlite.SQLiteDatabase;

/**
 * Query for a link enumeration
 *
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 */
class LinkEnumQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.LinkEnumQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection
	 *            is the database connection
	 */
	public LinkEnumQueryCommand(final SQLiteDatabase thisConnection)
	{
		super(thisConnection, LinkEnumQueryCommand.theQuery);
	}

	/**
	 * Get the id from the result set
	 *
	 * @return the link id value from the result set
	 */
	public int getId()
	{
		return this.cursor.getInt(0);
	}

	/**
	 * Get the name from the result set
	 *
	 * @return the link name value from the result set
	 */
	public String getName()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get the recurse capability from the result set
	 *
	 * @return the link recurse capability value from the result set
	 */
	public boolean getRecurse()
	{
		return this.cursor.getInt(2) != 0;
	}
}