package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * FrameNet governor query command
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnGovernorQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.FrameNetGovernorQuery;

	/**
	 * Constructor
	 *
	 * @param connection is the database connection
	 * @param luId       is the target frameid
	 */
	@SuppressWarnings("boxing")
	public FnGovernorQueryCommand(final SQLiteDatabase connection, final long luId)
	{
		super(connection, FnGovernorQueryCommand.QUERY);
		setParams(luId);
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
