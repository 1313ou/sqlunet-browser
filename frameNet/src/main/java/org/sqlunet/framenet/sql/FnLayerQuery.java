package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

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
		return this.cursor.getLong(0);
	}

	/**
	 * Get the layer type from the result set
	 *
	 * @return the layer type from the result set
	 */
	public String getLayerType()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get the layer annoSet id from the result set
	 *
	 * @return the layer annoSet id from the result set
	 */
	public long getAnnoSetId()
	{
		return this.cursor.getLong(2);
	}

	/**
	 * Get the layer rank from the result set
	 *
	 * @return the layer rank from the result set
	 */
	public int getRank()
	{
		return this.cursor.getInt(3);
	}

	/**
	 * Get the labels from the result set
	 *
	 * @return the labels from the result set
	 */
	public List<FnLabel> getLabels()
	{
		return Utils.parseLabels(this.cursor.getString(4));
	}
}
