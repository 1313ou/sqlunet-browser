package org.sqlunet.framenet.sql;

import org.sqlunet.sql.DBQueryCommand;

import android.database.sqlite.SQLiteDatabase;

class FnSentenceQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.FrameNetSentenceQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection
	 *            is the database connection
	 * @param thisSentenceId
	 *            is the target sentence id
	 */
	@SuppressWarnings("boxing")
	public FnSentenceQueryCommand(final SQLiteDatabase thisConnection, final long thisSentenceId)
	{
		super(thisConnection, FnSentenceQueryCommand.theQuery);
		setParams(thisSentenceId);
	}

	/**
	 * Get the governor id from the result set
	 *
	 * @return the governor id from the result set
	 */
	public long getSentenceId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the text from the result set
	 *
	 * @return the text from the result set
	 */
	public String getText()
	{
		return this.cursor.getString(1);
	}
}