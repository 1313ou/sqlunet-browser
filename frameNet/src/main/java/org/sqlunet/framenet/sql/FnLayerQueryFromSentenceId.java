/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * FrameNet layer query from sentence
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnLayerQueryFromSentenceId extends FnLayerQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.FrameNetLayerQueryFromSentenceId;

	/**
	 * Constructor
	 *
	 * @param connection       connection
	 * @param targetSentenceId target sentence id
	 */
	public FnLayerQueryFromSentenceId(final SQLiteDatabase connection, final long targetSentenceId)
	{
		super(connection, targetSentenceId, FnLayerQueryFromSentenceId.QUERY);
	}
}
