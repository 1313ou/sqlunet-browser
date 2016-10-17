package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Governor
 *
 * @author Bernard Bou
 */
class FnGovernor
{
	/**
	 * Governor id
	 */
	public final long governorId;

	/**
	 * Word id
	 */
	public final long wordId;

	/**
	 * Governor
	 */
	public final String governor;

	/**
	 * Constructor
	 *
	 * @param governorId governor id
	 * @param wordId     word id
	 * @param governor   governor
	 */
	private FnGovernor(final long governorId, final long wordId, final String governor)
	{
		this.governorId = governorId;
		this.wordId = wordId;
		this.governor = governor;
	}

	/**
	 * Make set of governors from query built from frameid
	 *
	 * @param connection is the database connection
	 * @param luId       is the frameid to build query from
	 * @return list of governors
	 */
	public static List<FnGovernor> make(final SQLiteDatabase connection, final long luId)
	{
		final List<FnGovernor> result = new ArrayList<>();
		FnGovernorQueryCommand query = null;
		try
		{
			query = new FnGovernorQueryCommand(connection, luId);
			query.execute();

			while (query.next())
			{
				final long governorId = query.getGovernorId();
				final long wordId = query.getWordId();
				final String governor = query.getGovernor();

				result.add(new FnGovernor(governorId, wordId, governor));
			}
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return result;
	}
}
