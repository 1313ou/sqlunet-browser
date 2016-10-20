package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * FrameNet layer command from annoset
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnLayerQueryFromAnnoSetCommand extends FnLayerQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.FrameNetLayerQueryFromAnnoSet;

	/**
	 * Constructor
	 *
	 * @param connection      is the database connection
	 * @param targetAnnoSetId is the target annoSetId
	 */
	public FnLayerQueryFromAnnoSetCommand(final SQLiteDatabase connection, final long targetAnnoSetId)
	{
		super(connection, targetAnnoSetId, FnLayerQueryFromAnnoSetCommand.QUERY);
	}
}
