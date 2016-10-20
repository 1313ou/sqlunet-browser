package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * FrameNet layer command from sentence
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
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
	 * @param connection       database connection
	 * @param targetSentenceId target sentence id
	 */
	public FnLayerQueryFromSentenceCommand(final SQLiteDatabase connection, final long targetSentenceId)
	{
		super(connection, targetSentenceId, FnLayerQueryFromSentenceCommand.QUERY);
	}
}
