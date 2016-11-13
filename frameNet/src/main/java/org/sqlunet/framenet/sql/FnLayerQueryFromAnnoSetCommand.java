package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * FrameNet layer command from annoSet
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnLayerQueryFromAnnoSetCommand extends FnLayerQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.FrameNetLayerQueryFromAnnoSet;

	/**
	 * Constructor
	 *
	 * @param connection      connection
	 * @param targetAnnoSetId target annoSet id
	 */
	public FnLayerQueryFromAnnoSetCommand(final SQLiteDatabase connection, final long targetAnnoSetId)
	{
		super(connection, targetAnnoSetId, FnLayerQueryFromAnnoSetCommand.QUERY);
	}
}
