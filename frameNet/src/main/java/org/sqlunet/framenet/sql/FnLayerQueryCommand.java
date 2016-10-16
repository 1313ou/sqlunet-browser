package org.sqlunet.framenet.sql;

import java.util.List;

import org.sqlunet.framenet.Utils;
import org.sqlunet.sql.DBQueryCommand;

import android.database.sqlite.SQLiteDatabase;

class FnLayerQueryCommand extends DBQueryCommand
{
	/**
	 * Constructor
	 *
	 * @param thisConnection
	 *            is the database connection
	 * @param thisTargetId
	 *            is the target id
	 */
	@SuppressWarnings("boxing")
	FnLayerQueryCommand(final SQLiteDatabase thisConnection, final long thisTargetId, final String thisQuery)
	{
		super(thisConnection, thisQuery);
		setParams(thisTargetId);
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
