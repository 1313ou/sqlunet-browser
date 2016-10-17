package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.framenet.Utils;
import org.sqlunet.sql.DBQueryCommand;

import java.util.List;

/**
 * FrameNet layer query command
 *
 * @author Bernard Bou
 */
class FnLayerQueryCommand extends DBQueryCommand
{
	/**
	 * Constructor
	 *
	 * @param connection is the database connection
	 * @param targetId   is the target id
	 */
	@SuppressWarnings("boxing")
	FnLayerQueryCommand(final SQLiteDatabase connection, final long targetId, final String query)
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
	 * Get the layer rank from the result set
	 *
	 * @return the layer rank from the result set
	 */
	public int getRank()
	{
		return this.cursor.getInt(2);
	}

	/**
	 * Get the labels from the result set
	 *
	 * @return the labels from the result set
	 */
	public List<FnLabel> getLabels()
	{
		return Utils.parseLabels(this.cursor.getString(3));
	}
}
