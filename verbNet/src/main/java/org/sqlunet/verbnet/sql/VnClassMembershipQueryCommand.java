package org.sqlunet.verbnet.sql;

import org.sqlunet.sql.DBQueryCommand;

import android.database.sqlite.SQLiteDatabase;

/**
 * VerbNet Class Membership query command
 *
 * @author Bernard Bou
 */
class VnClassMembershipQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.VerbNetClassMembershipQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection
	 *            is the database connection
	 * @param thisWordId
	 *            is the target wordid
	 * @param thisSynsetId
	 *            is the target synsetid (null corresponds to no value)
	 */
	@SuppressWarnings("boxing")
	public VnClassMembershipQueryCommand(final SQLiteDatabase thisConnection, final long thisWordId, final Long thisSynsetId)
	{
		super(thisConnection, VnClassMembershipQueryCommand.theQuery);
		setParams(thisWordId, thisSynsetId);
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
	public int getQuality()
	{
		return this.cursor.getInt(5);
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
