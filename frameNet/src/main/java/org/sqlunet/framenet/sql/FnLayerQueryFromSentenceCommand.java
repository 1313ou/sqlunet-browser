package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

class FnLayerQueryFromSentenceCommand extends FnLayerQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.FrameNetLayerQueryFromSentence;

	/**
	 * Constructor
	 *
	 * @param thisConnection       is the database connection
	 * @param thisTargetSentenceId is the target sentenceid
	 */
	public FnLayerQueryFromSentenceCommand(final SQLiteDatabase thisConnection, final long thisTargetSentenceId)
	{
		super(thisConnection, thisTargetSentenceId, FnLayerQueryFromSentenceCommand.theQuery);
	}
}
