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
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.VerbNetClassQuery;

	/**
	 * Constructor
	 *
	 * @param connection is the database connection
	 * @param classId    is the target classid (null corresponds to no value)
	 */
	@SuppressWarnings("boxing")
	public VnClassQueryCommand(final SQLiteDatabase connection, final long classId)
	{
		super(connection, VnClassQueryCommand.QUERY);
		setParams(classId);
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
