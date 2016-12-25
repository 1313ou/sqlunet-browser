package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * VerbNet class with sense
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class VnClassWithSense
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
	 * Definition
	 */
	public final String definition;

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
	public final float quality;

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
	 * @param definition  sense num
	 * @param senseNum  sense num
	 * @param senseKey  senseKey
	 * @param quality   quality
	 * @param groupings groupings
	 */
	private VnClassWithSense(final String className, final long classId, final Long wordId, final Long synsetId, final String definition, final Integer senseNum, final String senseKey, final float quality, final String groupings)
	{
		super();
		this.className = className;
		this.classId = classId;
		this.synsetId = synsetId;
		this.wordId = wordId;
		this.definition = definition;
		this.senseNum = senseNum;
		this.senseKey = senseKey;
		this.quality = quality;
		this.groupings = groupings;
	}

	/**
	 * Make sets of VerbNet classes with senses from query built from word id and synset id
	 *
	 * @param connection connection
	 * @param wordId     word id to build query from
	 * @param synsetId   synset id to build the query from (null if any)
	 * @return list of VerbNet classes
	 */
	static public List<VnClassWithSense> make(final SQLiteDatabase connection, final long wordId, final Long synsetId)
	{
		final List<VnClassWithSense> result = new ArrayList<>();
		VnClassQueryFromSense query = null;
		try
		{
			query = new VnClassQueryFromSense(connection, wordId, synsetId);
			query.execute();

			while (query.next())
			{
				final String className = query.getClassName();
				final long classId = query.getClassId();
				final boolean synsetSpecificFlag = query.getSynsetSpecific();
				final String definition = query.getDefinition();
				final String sensekey = query.getSenseKey();
				final int sensenum = query.getSenseNum();
				final float quality = query.getQuality();
				final String groupings = query.getGroupings();

				result.add(new VnClassWithSense(className, classId, wordId, synsetSpecificFlag ? synsetId : null, definition, sensenum, sensekey, quality, groupings));
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
