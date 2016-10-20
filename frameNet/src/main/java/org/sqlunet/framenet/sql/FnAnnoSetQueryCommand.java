package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;
import org.sqlunet.sql.Utils;

/**
 * FrameNet annoset query command
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnAnnoSetQueryCommand extends DBQueryCommand
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	private static final String QUERY = SqLiteDialect.FrameNetAnnoSetQuery;

	/**
	 * Constructor
	 *
	 * @param connection      is the database connection
	 * @param targetAnnoSetId is the target annoSetId
	 */
	@SuppressWarnings("boxing")
	public FnAnnoSetQueryCommand(final SQLiteDatabase connection, final long targetAnnoSetId)
	{
		super(connection, FnAnnoSetQueryCommand.QUERY);
		setParams(targetAnnoSetId);
	}

	/**
	 * Get the sentence id from the result set
	 *
	 * @return the sentence id from the result set
	 */
	public long getSentenceId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the sentence text from the result set
	 *
	 * @return the sentence text from the result set
	 */
	public String getSentenceText()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get the annoset ids from the result set
	 *
	 * @return the annoset ids from the result set
	 */
	@SuppressWarnings("unused")
	public long[] getAnnoSetIds()
	{
		return Utils.toIds(this.cursor.getString(2));
	}
}
