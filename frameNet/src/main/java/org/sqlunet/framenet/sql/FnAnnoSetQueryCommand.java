package org.sqlunet.framenet.sql;

import org.sqlunet.sql.DBQueryCommand;

import android.database.sqlite.SQLiteDatabase;

class FnAnnoSetQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.FrameNetAnnoSetQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection
	 *            is the database connection
	 * @param thisTargetAnnoSetId
	 *            is the target annosetid
	 */
	@SuppressWarnings("boxing")
	public FnAnnoSetQueryCommand(final SQLiteDatabase thisConnection, final long thisTargetAnnoSetId)
	{
		super(thisConnection, FnAnnoSetQueryCommand.theQuery);
		setParams(thisTargetAnnoSetId);
	}

	/**
	 * Get the sentence id from the result set
	 *
	 * @return the sentence id from the result set
	 */
	public long getSentenceId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the sentence text from the result set
	 *
	 * @return the sentence text from the result set
	 */
	public String getSentenceText()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get the annoset ids from the result set
	 *
	 * @return the annoset ids from the result set
	 */
	@SuppressWarnings("unused")
	public long[] getAnnoSetIds()
	{
		return DBQueryCommand.toIds(this.cursor.getString(2));
	}
}
