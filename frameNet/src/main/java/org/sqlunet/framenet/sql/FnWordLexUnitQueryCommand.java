package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * VerbNet Class Membership query command
 *
 * @author Bernard Bou
 */
class FnWordLexUnitQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.FrameNetWordLexUnitQueryFromWordId;

	/**
	 * <code>theQuery2</code> is the SQL statement with Pos input
	 */
	private static final String theQuery2 = SqLiteDialect.FrameNetWordLexUnitWithPosQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection   is the database connection
	 * @param thisTargetWordId target wordid
	 * @param thisTargetPos    target pos or null
	 */
	@SuppressWarnings("boxing")
	public FnWordLexUnitQueryCommand(final SQLiteDatabase thisConnection, final long thisTargetWordId, final Character thisTargetPos)
	{
		super(thisConnection, thisTargetPos != null ?
				FnWordLexUnitQueryCommand.theQuery2 :
				FnWordLexUnitQueryCommand.theQuery);
		setParams(thisTargetWordId, FnWordLexUnitQueryCommand.mapPos(thisTargetPos));
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

	/**
	 * Map character to pos
	 *
	 * @param thatTargetPos character
	 * @return pos code
	 */
	@SuppressWarnings("boxing")
	private static Integer mapPos(final Character thatTargetPos)
	{
		if (thatTargetPos == null)
			return null;
		switch (thatTargetPos)
		{
			case 'n':
				return 1;
			case 'v':
				return 2;
			case 'a':
				return 3;
			case 'r':
				return 4;
		}
		return null;
	}
}
