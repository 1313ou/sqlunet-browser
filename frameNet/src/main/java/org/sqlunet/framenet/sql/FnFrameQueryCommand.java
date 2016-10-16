package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * VerbNet Class Membership query command
 *
 * @author Bernard Bou
 */
class FnFrameQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.FrameNetFrameQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection    is the database connection
	 * @param thisTargetFrameId target annosetid
	 */
	@SuppressWarnings("boxing")
	public FnFrameQueryCommand(final SQLiteDatabase thisConnection, final long thisTargetFrameId)
	{
		super(thisConnection, FnFrameQueryCommand.theQuery);
		setParams(thisTargetFrameId);
	}

	/**
	 * Get the annosetid from the result set
	 *
	 * @return the annosetid from the result set
	 */
	public long getFrameId()
	{
		return this.cursor.getInt(0);
	}

	/**
	 * Get the frame from the result set
	 *
	 * @return the frame from the result set or null if none
	 */
	public String getFrame()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get the frame description from the result set
	 *
	 * @return the frame description from the result set or null if none
	 */
	public String getFrameDescription()
	{
		return this.cursor.getString(2);
	}

	/**
	 * Get the semtypes from the result set
	 *
	 * @return the the semtypes from the result set or null if none
	 */
	public String getSemTypes()
	{
		return this.cursor.getString(3);
	}

	/**
	 * Get the related frames from the result set
	 *
	 * @return the related frames from the result set or null if none
	 */
	public String getRelatedFrames()
	{
		return this.cursor.getString(4);
	}
}
