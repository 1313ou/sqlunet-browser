package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * VerbNet FrameQuery query command
 *
 * @author Bernard Bou
 */
class FnGovernorQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.FrameNetGovernorQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection is the database connection
	 * @param thisLuId       is the target frameid
	 */
	@SuppressWarnings("boxing")
	public FnGovernorQueryCommand(final SQLiteDatabase thisConnection, final long thisLuId)
	{
		super(thisConnection, FnGovernorQueryCommand.theQuery);
		setParams(thisLuId);
	}

	/**
	 * Get the governor id from the result set
	 *
	 * @return the governor id from the result set
	 */
	public long getGovernorId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the word id from the result set
	 *
	 * @return the word id from the result set
	 */
	public long getWordId()
	{
		return this.cursor.getLong(1);
	}

	/**
	 * Get the governor from the result set
	 *
	 * @return the governor from the result set
	 */
	public String getGovernor()
	{
		return this.cursor.getString(2);
	}
}
