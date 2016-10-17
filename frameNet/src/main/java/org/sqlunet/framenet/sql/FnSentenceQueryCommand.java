package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * FrameNet sentence query command
 *
 * @author Bernard Bou
 */
class FnSentenceQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.FrameNetSentenceQuery;

	/**
	 * Constructor
	 *
	 * @param connection is the database connection
	 * @param sentenceId is the target sentence id
	 */
	@SuppressWarnings("boxing")
	public FnSentenceQueryCommand(final SQLiteDatabase connection, final long sentenceId)
	{
		super(connection, FnSentenceQueryCommand.QUERY);
		setParams(sentenceId);
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