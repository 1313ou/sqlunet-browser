package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * FrameNet frame element query command
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnFrameElementQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.FrameNetFrameFEQuery;

	/**
	 * Constructor
	 *
	 * @param connection    is the database connection
	 * @param targetFrameId is the target frameId
	 */
	@SuppressWarnings("boxing")
	public FnFrameElementQueryCommand(final SQLiteDatabase connection, final long targetFrameId)
	{
		super(connection, FnFrameElementQueryCommand.QUERY);
		setParams(targetFrameId);
	}

	/**
	 * Get the fe type id from the result set
	 *
	 * @return the fe type id from the result set
	 */
	public long getFETypeId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the fe type from the result set
	 *
	 * @return the fe type from the result set
	 */
	public String getFEType()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get the fe id from the result set
	 *
	 * @return the fe id from the result set
	 */
	public long getFEId()
	{
		return this.cursor.getLong(2);
	}

	/**
	 * Get the fe definition from the result set
	 *
	 * @return the fe definition from the result set
	 */
	public String getFEDefinition()
	{
		return this.cursor.getString(3);
	}

	/**
	 * Get the fe abbrev from the result set
	 *
	 * @return the fe abbrev from the result set
	 */
	public String getFEAbbrev()
	{
		return this.cursor.getString(4);
	}

	/**
	 * Get the fe core type from the result set
	 *
	 * @return the fe core type from the result set
	 */
	public String getFECoreType()
	{
		return this.cursor.getString(5);
	}

	/**
	 * Get the fe sem types from the result set
	 *
	 * @return the fe sem types from the result set
	 */
	public String getSemTypes()
	{
		return this.cursor.getString(6);
	}

	/**
	 * Get the fe core membership type from the result set
	 *
	 * @return the fe core membership from the result set
	 */
	public boolean getIsCore()
	{
		return this.cursor.getInt(7) != 0;
	}

	/**
	 * Get the fe core set from the result set
	 *
	 * @return the fe coreset from the result set or 0 if node
	 */
	public int getCoreSet()
	{
		return this.cursor.getInt(8);
	}
}
