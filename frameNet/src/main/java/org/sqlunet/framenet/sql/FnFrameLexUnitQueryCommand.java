package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * FrameNet lex unit query command
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnFrameLexUnitQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.FrameNetFrameLexUnitQuery;

	/**
	 * Constructor
	 *
	 * @param connection    is the database connection
	 * @param targetFrameId target annosetid
	 */
	@SuppressWarnings("boxing")
	public FnFrameLexUnitQueryCommand(final SQLiteDatabase connection, final long targetFrameId)
	{
		super(connection, FnFrameLexUnitQueryCommand.QUERY);
		setParams(targetFrameId);
	}

	/**
	 * Get the frame id from the result set
	 *
	 * @return the frame id from the result set
	 */
	public long getLuId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the frame from the result set
	 *
	 * @return the frame from the result set
	 */
	public String getLexUnit()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get the pos from the result set
	 *
	 * @return the pos from the result set
	 */
	public String getPos()
	{
		return this.cursor.getString(2);
	}

	/**
	 * Get the definition from the result set
	 *
	 * @return the definition from the result set
	 */
	public String getDefinition()
	{
		return this.cursor.getString(3);
	}

	/**
	 * Get the definition dictionary from the result set
	 *
	 * @return the definition dictionary from the result set
	 */
	public String getDictionary()
	{
		return this.cursor.getString(4);
	}

	/**
	 * Get the incorporated FE from the result set
	 *
	 * @return the incorporated FE from the result set or null if none
	 */
	public String getIncorporatedFe()
	{
		return this.cursor.getString(5);
	}

	/**
	 * Get the annosetid from the result set
	 *
	 * @return the annosetid from the result set
	 */
	@SuppressWarnings("unused")
	public long getFrameId()
	{
		return this.cursor.getInt(6);
	}
}
