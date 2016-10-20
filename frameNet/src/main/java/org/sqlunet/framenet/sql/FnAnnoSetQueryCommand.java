package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;
import org.sqlunet.sql.Utils;

/**
 * FrameNet annoSet query command
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
	 * @param connection database connection
	 * @param annoSetId  target annoSet id
	 */
	@SuppressWarnings("boxing")
	public FnAnnoSetQueryCommand(final SQLiteDatabase connection, final long annoSetId)
	{
		super(connection, FnAnnoSetQueryCommand.QUERY);
		setParams(annoSetId);
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
	 * Get the annoSet ids from the result set
	 *
	 * @return the annoSet ids from the result set
	 */
	@SuppressWarnings("unused")
	public long[] getAnnoSetIds()
	{
		return Utils.toIds(this.cursor.getString(2));
	}
}
