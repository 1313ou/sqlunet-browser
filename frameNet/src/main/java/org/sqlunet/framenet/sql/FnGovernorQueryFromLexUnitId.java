package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * FrameNet governor query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnGovernorQueryFromLexUnitId extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.FrameNetGovernorQueryFromLexUnitId;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param luId       target lex unit id
	 */
	@SuppressWarnings("boxing")
	public FnGovernorQueryFromLexUnitId(final SQLiteDatabase connection, final long luId)
	{
		super(connection, FnGovernorQueryFromLexUnitId.QUERY);
		setParams(luId);
	}

	/**
	 * Get the governor id from the result set
	 *
	 * @return the governor id from the result set
	 */
	public long getGovernorId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the word id from the result set
	 *
	 * @return the word id from the result set
	 */
	public long getWordId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(1);
	}

	/**
	 * Get the governor from the result set
	 *
	 * @return the governor from the result set
	 */
	public String getGovernor()
	{
		assert this.cursor != null;
		return this.cursor.getString(2);
	}
}
