package org.sqlunet.framenet.sql;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

/**
 * FE
 *
 * @author Bernard Bou
 */
class FnGovernor
{
	/**
	 * Governor id
	 */
	public final long theGovernorId;

	/**
	 * Word id
	 */
	public final long theWordId;

	/**
	 * Governor
	 */
	public final String theGovernor;

	/**
	 * Constructor
	 *
	 * @param thisGovernorId
	 *            governor id
	 * @param thisWordId
	 *            word id
	 * @param thisGovernor
	 *            governor
	 */
	private FnGovernor(final long thisGovernorId, final long thisWordId, final String thisGovernor)
	{
		this.theGovernorId = thisGovernorId;
		this.theWordId = thisWordId;
		this.theGovernor = thisGovernor;
	}

	/**
	 * Make set of governors from query built from frameid
	 *
	 * @param thisConnection
	 *            is the database connection
	 * @param theLuId
	 *            is the frameid to build query from
	 * @return list of governors
	 */
	public static List<FnGovernor> make(final SQLiteDatabase thisConnection, final long theLuId)
	{
		final List<FnGovernor> thisResult = new ArrayList<>();
		FnGovernorQueryCommand thisQuery = null;
		try
		{
			thisQuery = new FnGovernorQueryCommand(thisConnection, theLuId);
			thisQuery.execute();

			while (thisQuery.next())
			{
				final long thisGovernorId = thisQuery.getGovernorId();
				final long thisWordId = thisQuery.getWordId();
				final String thisGovernor = thisQuery.getGovernor();

				thisResult.add(new FnGovernor(thisGovernorId, thisWordId, thisGovernor));
			}
		}
		finally
		{
			if (thisQuery != null)
			{
				thisQuery.release();
			}
		}
		return thisResult;
	}
}
