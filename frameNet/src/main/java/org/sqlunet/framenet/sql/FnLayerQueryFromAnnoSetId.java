/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * FrameNet layer query from annoSet
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnLayerQueryFromAnnoSetId extends FnLayerQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.FrameNetLayerQueryFromAnnoSetId;

	/**
	 * Constructor
	 *
	 * @param connection      connection
	 * @param targetAnnoSetId target annoSet id
	 */
	public FnLayerQueryFromAnnoSetId(final SQLiteDatabase connection, final long targetAnnoSetId)
	{
		super(connection, targetAnnoSetId, FnLayerQueryFromAnnoSetId.QUERY);
	}
}
