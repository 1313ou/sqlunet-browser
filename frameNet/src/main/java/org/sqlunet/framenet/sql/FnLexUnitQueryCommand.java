package org.sqlunet.framenet.sql;

import org.sqlunet.sql.DBQueryCommand;

import android.database.sqlite.SQLiteDatabase;

/**
 * VerbNet Class Membership query command
 *
 * @author Bernard Bou
 */
class FnLexUnitQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.FrameNetLexUnitQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection
	 *            is the database connection
	 * @param thisTargetLuId
	 *            target luid
	 */
	@SuppressWarnings("boxing")
	public FnLexUnitQueryCommand(final SQLiteDatabase thisConnection, final long thisTargetLuId)
	{
		super(thisConnection, FnLexUnitQueryCommand.theQuery);
		setParams(thisTargetLuId);
	}

	/**
	 * Get the frame id from the result set
	 *
	 * @return the frame id from the result set
	 */
	public long getLuId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the frame from the result set
	 *
	 * @return the frame from the result set
	 */
	public String getLexUnit()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get the pos from the result set
	 *
	 * @return the pos from the result set
	 */
	public String getPos()
	{
		return this.cursor.getString(2);
	}

	/**
	 * Get the definition from the result set
	 *
	 * @return the definition from the result set
	 */
	public String getDefinition()
	{
		return this.cursor.getString(3);
	}

	/**
	 * Get the definition dictionary from the result set
	 *
	 * @return the definition dictionary from the result set
	 */
	public String getDictionary()
	{
		return this.cursor.getString(4);
	}

	/**
	 * Get the incorporated FE from the result set
	 *
	 * @return the incorporated FE from the result set or null if none
	 */
	public String getIncorporatedFe()
	{
		return this.cursor.getString(5);
	}

	/**
	 * Get the annosetid from the result set
	 *
	 * @return the annosetid from the result set
	 */
	public long getFrameId()
	{
		return this.cursor.getInt(6);
	}

	/**
	 * Get the frame from the result set
	 *
	 * @return the frame from the result set or null if none
	 */
	public String getFrame()
	{
		return this.cursor.getString(7);
	}

	/**
	 * Get the frame description from the result set
	 *
	 * @return the frame description from the result set or null if none
	 */
	public String getFrameDescription()
	{
		return this.cursor.getString(8);
	}
}
