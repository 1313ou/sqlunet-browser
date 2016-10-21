package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * FrameNet frame query command
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnFrameQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.FrameNetFrameQuery;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param frameId    target frame id
	 */
	@SuppressWarnings("boxing")
	public FnFrameQueryCommand(final SQLiteDatabase connection, final long frameId)
	{
		super(connection, FnFrameQueryCommand.QUERY);
		setParams(frameId);
	}

	/**
	 * Get the annoSetId from the result set
	 *
	 * @return the annoSetId from the result set
	 */
	@SuppressWarnings("unused")
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
