package org.sqlunet.framenet.sql;

import org.sqlunet.sql.DBQueryCommand;

import android.database.sqlite.SQLiteDatabase;

class FnSentenceQueryFromLexicalUnitCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.FrameNetSentencesFromLexicalUnitQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection
	 *            is the database connection
	 * @param thisLuId
	 *            is the target frameid
	 */
	@SuppressWarnings("boxing")
	public FnSentenceQueryFromLexicalUnitCommand(final SQLiteDatabase thisConnection, final long thisLuId)
	{
		super(thisConnection, FnSentenceQueryFromLexicalUnitCommand.theQuery);
		setParams(thisLuId);
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