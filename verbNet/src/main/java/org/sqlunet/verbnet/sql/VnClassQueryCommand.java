package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * VerbNet Class query command
 *
 * @author Bernard Bou
 */
class VnClassQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.VerbNetClassQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection is the database connection
	 * @param thisClassId    is the target classid (null corresponds to no value)
	 */
	@SuppressWarnings("boxing")
	public VnClassQueryCommand(final SQLiteDatabase thisConnection, final long thisClassId)
	{
		super(thisConnection, VnClassQueryCommand.theQuery);
		setParams(thisClassId);
	}

	/**
	 * Get the class id from the result set
	 *
	 * @return the class id from the result set
	 */
	@SuppressWarnings("unused")
	public long getClassId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the class name from the result set
	 *
	 * @return the class name from the result set
	 */
	public String getClassName()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get the groupings from the result set
	 *
	 * @return the (|-separated) groupings from the result set
	 */
	public String getGroupings()
	{
		return this.cursor.getString(2);
	}
}
