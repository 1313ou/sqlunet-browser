package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

class VnClassMembership
{
	public final String className;

	public final long classId;

	public final long synsetId;

	@SuppressWarnings("unused")
	public final long wordId;

	public final int senseNum;

	public final String senseKey;

	public final int quality;

	public final String groupings;

	private VnClassMembership(final String className, final long classId, final long wordId, final long synsetId, final int senseNum, final String sensekey, final int quality, final String groupings)
	{
		super();
		this.className = className;
		this.classId = classId;
		this.synsetId = synsetId;
		this.wordId = wordId;
		this.senseNum = senseNum;
		this.senseKey = sensekey;
		this.quality = quality;
		this.groupings = groupings;
	}

	/**
	 * Make sets of VerbNet memberships from query built from wordid and synsetid
	 *
	 * @param connection is the database connection
	 * @param wordId     is the word id to build query from
	 * @param synsetId   is the synset id to build the query from (-1 if any)
	 * @return list of VerbNet memberships
	 */
	static public List<VnClassMembership> make(final SQLiteDatabase connection, final long wordId, final Long synsetId)
	{
		final List<VnClassMembership> result = new ArrayList<>();
		VnClassMembershipQueryCommand query = null;
		try
		{
			query = new VnClassMembershipQueryCommand(connection, wordId, synsetId);
			query.execute();

			while (query.next())
			{
				final String className = query.getClassName();
				final long classId = query.getClassId();
				final boolean synsetSpecificFlag = query.getSynsetSpecific();
				final String sensekey = query.getSenseKey();
				final int sensenum = query.getSenseNum();
				final int quality = query.getQuality();
				final String groupings = query.getGroupings();

				result.add(new VnClassMembership(className, classId, wordId, synsetSpecificFlag ?
						synsetId :
						-1, sensenum, sensekey, quality, groupings));
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
