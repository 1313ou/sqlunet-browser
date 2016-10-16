package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

class FnLayerQueryFromAnnoSetCommand extends FnLayerQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.FrameNetLayerQueryFromAnnoSet;

	/**
	 * Constructor
	 *
	 * @param thisConnection      is the database connection
	 * @param thisTargetAnnoSetId is the target annosetid
	 */
	public FnLayerQueryFromAnnoSetCommand(final SQLiteDatabase thisConnection, final long thisTargetAnnoSetId)
	{
		super(thisConnection, thisTargetAnnoSetId, FnLayerQueryFromAnnoSetCommand.theQuery);
	}
}
