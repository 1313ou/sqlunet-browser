package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * FrameNet lex unit query command
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnLexUnitQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.FrameNetLexUnitQuery;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param targetLuId target luId
	 */
	@SuppressWarnings("boxing")
	public FnLexUnitQueryCommand(final SQLiteDatabase connection, final long targetLuId)
	{
		super(connection, FnLexUnitQueryCommand.QUERY);
		setParams(targetLuId);
	}

	/**
	 * Get the frame id from the result set
	 *
	 * @return the frame id from the result set
	 */
	@SuppressWarnings("unused")
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
	 * Get the annoSetId from the result set
	 *
	 * @return the annoSetId from the result set
	 */
	public long getFrameId()
	{
		return this.cursor.getInt(6);
	}

	/**
	 * Get the frame from the result set
	 *
	 * @return the frame from the result set or null if none
	 */
	public String getFrame()
	{
		return this.cursor.getString(7);
	}

	/**
	 * Get the frame description from the result set
	 *
	 * @return the frame description from the result set or null if none
	 */
	public String getFrameDescription()
	{
		return this.cursor.getString(8);
	}
}
