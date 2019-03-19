package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * VerbNet frame query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class VnFrameQueryFromClassId extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.VerbNetFramesQueryFromClassId;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param classId    target classId
	 */
	@SuppressWarnings("boxing")
	public VnFrameQueryFromClassId(final SQLiteDatabase connection, final long classId)
	{
		super(connection, VnFrameQueryFromClassId.QUERY);
		setParams(classId);
	}

	/**
	 * Get the frame id from the result set
	 *
	 * @return the frame id from the result set
	 */
	@SuppressWarnings("unused")
	public long getFrameId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the frame number from the result set
	 *
	 * @return the frame number from the result set
	 */
	public String getNumber()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get the frame xtag from the result set
	 *
	 * @return the frame xtag from the result set
	 */
	public String getXTag()
	{
		assert this.cursor != null;
		return this.cursor.getString(2);
	}

	/**
	 * Get the frame major description from the result set
	 *
	 * @return the frame major description from the result set
	 */
	public String getDescription1()
	{
		assert this.cursor != null;
		return this.cursor.getString(3);
	}

	/**
	 * Get the frame minor description from the result set
	 *
	 * @return the frame minor description from the result set
	 */
	public String getDescription2()
	{
		assert this.cursor != null;
		return this.cursor.getString(4);
	}

	/**
	 * Get the frame syntax from the result set
	 *
	 * @return the frame syntax from the result set
	 */
	public String getSyntax()
	{
		assert this.cursor != null;
		return this.cursor.getString(5);
	}

	/**
	 * Get the frame semantics from the result set
	 *
	 * @return the frame semantics from the result set
	 */
	public String getSemantics()
	{
		assert this.cursor != null;
		return this.cursor.getString(6);
	}

	/**
	 * Get the frame example from the result set
	 *
	 * @return the frame example from the result set
	 */
	public String getExamples()
	{
		assert this.cursor != null;
		return this.cursor.getString(7);
	}
}
