package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * VerbNet class membership query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class VnClassQueryFromSense extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.VerbNetClassQueryFromSense;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @param synsetId   target synset id (null corresponds to no value)
	 */
	@SuppressWarnings("boxing")
	public VnClassQueryFromSense(final SQLiteDatabase connection, final long wordId, final Long synsetId)
	{
		super(connection, VnClassQueryFromSense.QUERY);
		setParams(wordId, synsetId);
	}

	/**
	 * Get the class id from the result set
	 *
	 * @return the class id from the result set
	 */
	public long getClassId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the class name from the result set
	 *
	 * @return the class name from the result set
	 */
	public String getClassName()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get the frame xtag from the result set
	 *
	 * @return the frame xtag from the result set
	 */
	public boolean getSynsetSpecific()
	{
		return this.cursor.getInt(2) == 0;
	}

	/**
	 * Get the sensenum from the result set
	 *
	 * @return the sensenum from the result set
	 */
	public int getSenseNum()
	{
		return this.cursor.getInt(3);
	}

	/**
	 * Get the sensekey from the result set
	 *
	 * @return the sensekey from the result set
	 */
	public String getSenseKey()
	{
		return this.cursor.getString(4);
	}

	/**
	 * Get the quality from the result set
	 *
	 * @return the quality from the result set
	 */
	public float getQuality()
	{
		return this.cursor.getFloat(5);
	}

	/**
	 * Get the groupings from the result set
	 *
	 * @return the (|-separated) groupings from the result set
	 */
	public String getGroupings()
	{
		return this.cursor.getString(6);
	}
}
