package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

import java.sql.Types;

/**
 * Query command for VerbNet roles
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class VnRoleQueryFromSenseCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.VerbNetThematicRolesFromClassAndSenseQuery;

	/**
	 * Constructor
	 *
	 * @param connection is the database connection
	 * @param classId    is the target class id
	 * @param wordId     is the target word id
	 * @param synsetId   is the target synset id
	 */
	@SuppressWarnings("boxing")
	public VnRoleQueryFromSenseCommand(final SQLiteDatabase connection, final long classId, final long wordId, final Long synsetId)
	{
		super(connection, VnRoleQueryFromSenseCommand.QUERY);
		setParams(classId, wordId);
		if (synsetId != null)
		{
			this.statement.setLong(2, synsetId);
		}
		else
		{
			this.statement.setNull(2, Types.DECIMAL);
		}
	}

	/**
	 * Get the role id from the result set
	 *
	 * @return the role id from the result set
	 */
	@SuppressWarnings("unused")
	public long getRoleId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the role type id from the result set
	 *
	 * @return the role type id from the result set
	 */
	@SuppressWarnings("unused")
	public long getRoleTypeId()
	{
		return this.cursor.getLong(1);
	}

	/**
	 * Get the role type from the result set
	 *
	 * @return the role type from the result set
	 */
	public String getRoleType()
	{
		return this.cursor.getString(2);
	}

	/**
	 * Get the role selectional restriction from the result set
	 *
	 * @return the role selectional restriction from the result set
	 */
	public String getSelectionRestriction()
	{
		return this.cursor.getString(3);
	}

	/**
	 * Get the quality from the result set
	 *
	 * @return the quality from the result set
	 */
	@SuppressWarnings("unused")
	public int getQuality()
	{
		return this.cursor.getInt(4);
	}

	/**
	 * Get synset-specific flag from the result set
	 *
	 * @return the synset-specific flag from the result set
	 */
	@SuppressWarnings("unused")
	public boolean getSynsetSpecific()
	{
		final int result = this.cursor.getInt(5);
		return result == 0;
	}
}
