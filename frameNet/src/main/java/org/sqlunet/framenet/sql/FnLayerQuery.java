package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import org.sqlunet.framenet.Utils;
import org.sqlunet.sql.DBQuery;

import java.util.List;

/**
 * FrameNet layer query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnLayerQuery extends DBQuery
{
	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param targetId   target id
	 */
	@SuppressWarnings("boxing")
	FnLayerQuery(final SQLiteDatabase connection, final long targetId, final String query)
	{
		super(connection, query);
		setParams(targetId);
	}

	/**
	 * Get the layer id from the result set
	 *
	 * @return the layer id from the result set
	 */
	public long getLayerId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the layer type from the result set
	 *
	 * @return the layer type from the result set
	 */
	public String getLayerType()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get the layer annoSet id from the result set
	 *
	 * @return the layer annoSet id from the result set
	 */
	public long getAnnoSetId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(2);
	}

	/**
	 * Get the layer rank from the result set
	 *
	 * @return the layer rank from the result set
	 */
	public int getRank()
	{
		assert this.cursor != null;
		return this.cursor.getInt(3);
	}

	/**
	 * Get the labels from the result set
	 *
	 * @return the labels from the result set
	 */
	@Nullable
	public List<FnLabel> getLabels()
	{
		assert this.cursor != null;
		return Utils.parseLabels(this.cursor.getString(4));
	}
}
