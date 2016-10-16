package org.sqlunet.propbank.sql;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

class PbRoleSet
{
	public final String theRoleSetName;

	public final String theRoleSetHead;

	public final String theRoleSetDescr;

	public final long theRoleSetId;

	private PbRoleSet(final String thisRoleSetName, final String thisRoleSetHead, final String thisRoleSetDescr, final long thisRoleSetId)
	{
		super();
		this.theRoleSetName = thisRoleSetName;
		this.theRoleSetHead = thisRoleSetHead;
		this.theRoleSetDescr = thisRoleSetDescr;
		this.theRoleSetId = thisRoleSetId;
	}

	/**
	 * Make sets of PropBank rolesets from query built from wordid
	 *
	 * @param thisConnection
	 *        is the database connection
	 * @param thisWord
	 *        is the word to build query from
	 * @return list of PropBank rolesets
	 */
	static public Pair<Long, List<PbRoleSet>> makeFromWord(final SQLiteDatabase thisConnection, final String thisWord)
	{
		final List<PbRoleSet> thisResult = new ArrayList<>();
		PbRoleSetQueryFromWordCommand thisQuery = null;
		try
		{
			thisQuery = new PbRoleSetQueryFromWordCommand(thisConnection, thisWord);
			thisQuery.execute();

			long thisWordId = 0;
			while (thisQuery.next())
			{
				thisWordId = thisQuery.getWordId();
				final String thisRoleSetName = thisQuery.getRoleSetName();
				final String thisRoleSetHead = thisQuery.getRoleSetHead();
				final String thisRoleSetDescr = thisQuery.getRoleSetDescr();
				final long thisRoleSetId = thisQuery.getRoleSetId();
				thisResult.add(new PbRoleSet(thisRoleSetName, thisRoleSetHead, thisRoleSetDescr, thisRoleSetId));
			}
			return new Pair<>(thisWordId, thisResult);
		}
		finally
		{
			if (thisQuery != null)
			{
				thisQuery.release();
			}
		}
	}

	/**
	 * Make sets of PropBank rolesets from query built from wordid
	 *
	 * @param thisConnection
	 *        is the database connection
	 * @param thisWordId
	 *        is the word id to build query from
	 * @return list of PropBank rolesets
	 */
	static public List<PbRoleSet> makeFromWordId(final SQLiteDatabase thisConnection, final long thisWordId)
	{
		final List<PbRoleSet> thisResult = new ArrayList<>();
		PbRoleSetQueryFromWordIdCommand thisQuery = null;
		try
		{
			thisQuery = new PbRoleSetQueryFromWordIdCommand(thisConnection, thisWordId);
			thisQuery.execute();

			while (thisQuery.next())
			{
				final String thisRoleSetName = thisQuery.getRoleSetName();
				final String thisRoleSetHead = thisQuery.getRoleSetHead();
				final String thisRoleSetDescr = thisQuery.getRoleSetDescr();
				final long thisRoleSetId = thisQuery.getRoleSetId();
				thisResult.add(new PbRoleSet(thisRoleSetName, thisRoleSetHead, thisRoleSetDescr, thisRoleSetId));
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

	/**
	 * Make sets of PropBank rolesets from query built from wordid
	 *
	 * @param thisConnection
	 *        is the database connection
	 * @param thisRoleSetId
	 *        is the roleset id to build query from
	 * @return list of PropBank rolesets
	 */
	static public List<PbRoleSet> make(final SQLiteDatabase thisConnection, final long thisRoleSetId)
	{
		final List<PbRoleSet> thisResult = new ArrayList<>();
		PbRoleSetQueryCommand thisQuery = null;
		try
		{
			thisQuery = new PbRoleSetQueryCommand(thisConnection, thisRoleSetId);
			thisQuery.execute();

			while (thisQuery.next())
			{
				final String thisRoleSetName = thisQuery.getRoleSetName();
				final String thisRoleSetHead = thisQuery.getRoleSetHead();
				final String thisRoleSetDescr = thisQuery.getRoleSetDescr();
				thisResult.add(new PbRoleSet(thisRoleSetName, thisRoleSetHead, thisRoleSetDescr, thisRoleSetId));
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
