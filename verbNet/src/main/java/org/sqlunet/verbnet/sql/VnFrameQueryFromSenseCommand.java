package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

import java.sql.Types;

/**
 * VerbNet FrameQuery query command
 *
 * @author Bernard Bou
 */
class VnFrameQueryFromSenseCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.VerbNetFramesFromClassAndSenseQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection is the database connection
	 * @param thisClassId    is the target classid
	 * @param thisWordId     is the target wordid
	 * @param thisSynsetId   is the target synsetid
	 */
	@SuppressWarnings("boxing")
	public VnFrameQueryFromSenseCommand(final SQLiteDatabase thisConnection, final long thisClassId, final long thisWordId, final Long thisSynsetId)
	{
		super(thisConnection, VnFrameQueryFromSenseCommand.theQuery);
		setParams(thisClassId, thisWordId);
		if (thisSynsetId != null)
		{
			this.statement.setLong(2, thisSynsetId);
		} else
		{
			this.statement.setNull(2, Types.DECIMAL);
		}
	}

	/**
	 * Get the frame id from the result set
	 *
	 * @return the frame id from the result set
	 */
	@SuppressWarnings("unused")
	public long getFrameId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the frame number from the result set
	 *
	 * @return the frame number from the result set
	 */
	public String getNumber()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get the frame xtag from the result set
	 *
	 * @return the frame xtag from the result set
	 */
	public String getXTag()
	{
		return this.cursor.getString(2);
	}

	/**
	 * Get the frame major description from the result set
	 *
	 * @return the frame major description from the result set
	 */
	public String getDescription1()
	{
		return this.cursor.getString(3);
	}

	/**
	 * Get the frame minor description from the result set
	 *
	 * @return the frame minor description from the result set
	 */
	public String getDescription2()
	{
		return this.cursor.getString(4);
	}

	/**
	 * Get the frame syntax from the result set
	 *
	 * @return the frame syntax from the result set
	 */
	public String getSyntax()
	{
		return this.cursor.getString(5);
	}

	/**
	 * Get the frame semantics from the result set
	 *
	 * @return the frame semantics from the result set
	 */
	public String getSemantics()
	{
		return this.cursor.getString(6);
	}

	/**
	 * Get the frame example from the result set
	 *
	 * @return the frame example from the result set
	 */
	public String getExample()
	{
		return this.cursor.getString(7);
	}

	/**
	 * Get the frame quality from the result set
	 *
	 * @return the frame quality from the result set
	 */
	@SuppressWarnings("unused")
	public int getQuality()
	{
		return this.cursor.getInt(8);
	}

	/**
	 * Get synset-specific flag from the result set
	 *
	 * @return the synset-specific flag from the result set
	 */
	@SuppressWarnings("unused")
	public boolean getSynsetSpecific()
	{
		final int thisResult = this.cursor.getInt(9);
		return thisResult == 0;
	}
}
