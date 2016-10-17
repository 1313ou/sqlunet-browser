package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * FrameNet layer command from sentence
 *
 * @author Bernard Bou
 */
class FnLayerQueryFromSentenceCommand extends FnLayerQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.FrameNetLayerQueryFromSentence;

	/**
	 * Constructor
	 *
	 * @param connection       is the database connection
	 * @param targetSentenceId is the target sentenceid
	 */
	public FnLayerQueryFromSentenceCommand(final SQLiteDatabase connection, final long targetSentenceId)
	{
		super(connection, targetSentenceId, FnLayerQueryFromSentenceCommand.QUERY);
	}
}
