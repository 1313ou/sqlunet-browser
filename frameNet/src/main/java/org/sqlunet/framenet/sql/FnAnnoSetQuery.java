/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;
import org.sqlunet.sql.Utils;

import androidx.annotation.NonNull;

/**
 * FrameNet annoSet query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnAnnoSetQuery extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.FrameNetAnnoSetQuery;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param annoSetId  target annoSet id
	 */
	@SuppressWarnings("boxing")
	public FnAnnoSetQuery(final SQLiteDatabase connection, final long annoSetId)
	{
		super(connection, FnAnnoSetQuery.QUERY);
		setParams(annoSetId);
	}

	/**
	 * Get the sentence id from the result set
	 *
	 * @return the sentence id from the result set
	 */
	public long getSentenceId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the sentence text from the result set
	 *
	 * @return the sentence text from the result set
	 */
	public String getSentenceText()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get the annoSet ids from the result set
	 *
	 * @return the annoSet ids from the result set
	 */
	@NonNull
	@SuppressWarnings("unused")
	public long[] getAnnoSetIds()
	{
		assert this.cursor != null;
		return Utils.toIds(this.cursor.getString(2));
	}
}
