package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * QueryData for a link enumeration
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class LinkEnumQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.LinkEnumQuery;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 */
	public LinkEnumQueryCommand(final SQLiteDatabase connection)
	{
		super(connection, LinkEnumQueryCommand.QUERY);
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