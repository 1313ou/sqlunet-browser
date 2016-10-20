package org.sqlunet.sql;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Database query command
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

public class DBQueryCommand
{
	/**
	 * <code>statement</code> is the SQL statement
	 */
	protected final PreparedStatement statement;

	/**
	 * <code>cursor</code> is the result set/cursor
	 */
	protected Cursor cursor;

	/**
	 * Constructor
	 *
	 * @param connection database connection
	 * @param statement  is the SQL statement
	 */
	protected DBQueryCommand(final SQLiteDatabase connection, final String statement)
	{
		this.statement = new PreparedStatement(connection, statement);
	}

	/**
	 * Execute query
	 */
	public void execute()
	{
		this.cursor = this.statement.executeQuery();
	}

	/**
	 * Iterate over cursor/result set
	 *
	 * @return true if more data is available
	 */
	public boolean next()
	{
		return this.cursor != null && this.cursor.moveToNext();
	}

	/**
	 * Release resources
	 */
	public void release()
	{
		try
		{
			if (this.cursor != null)
			{
				this.cursor.close();
			}
			this.statement.close();
		}
		catch (final SQLException e)
		{
			// nothing
		}
	}

	/**
	 * Set parameters in prepared SQL statement
	 *
	 * @param params the parameters
	 */
	protected void setParams(final Object... params)
	{
		int i = 0;
		for (final Object param : params)
		{
			if (param == null)
			{
				continue;
			}

			if (param instanceof String)
			{
				this.statement.setString(i, (String) param);
			}
			else if (param instanceof Long)
			{
				this.statement.setLong(i, (Long) param);
			}
			else if (param instanceof Integer)
			{
				this.statement.setInt(i, (Integer) param);
			}
			else if (param instanceof Character)
			{
				this.statement.setString(i, param.toString());
			}
			i++;
		}
	}
}