package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * VerbNet class membership
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class VnClassMembership
{
	/**
	 * Class name
	 */
	public final String className;

	/**
	 * Class id
	 */
	public final long classId;

	/**
	 * Synset id
	 */
	public final Long synsetId;

	/**
	 * Word id
	 */
	@SuppressWarnings("unused")
	public final Long wordId;

	/**
	 * Sense num
	 */
	public final Integer senseNum;

	/**
	 * Sense key
	 */
	public final String senseKey;

	/**
	 * Quality
	 */
	public final int quality;

	/**
	 * Groupings
	 */
	public final String groupings;

	/**
	 * Constructor
	 *
	 * @param className class name
	 * @param classId   class id
	 * @param wordId    word id
	 * @param synsetId  synset id
	 * @param senseNum  sense num
	 * @param senseKey  senseKey
	 * @param quality   quality
	 * @param groupings groupings
	 */
	private VnClassMembership(final String className, final long classId, final Long wordId, final Long synsetId, final Integer senseNum, final String senseKey, final int quality, final String groupings)
	{
		super();
		this.className = className;
		this.classId = classId;
		this.synsetId = synsetId;
		this.wordId = wordId;
		this.senseNum = senseNum;
		this.senseKey = senseKey;
		this.quality = quality;
		this.groupings = groupings;
	}

	/**
	 * Make sets of VerbNet memberships from query built from word id and synset id
	 *
	 * @param connection connection
	 * @param wordId     word id to build query from
	 * @param synsetId   synset id to build the query from (null if any)
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

				result.add(new VnClassMembership(className, classId, wordId, synsetSpecificFlag ? synsetId : null, sensenum, sensekey, quality, groupings));
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
