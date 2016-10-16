package org.sqlunet.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * Data source
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DataSource
{
	private final SQLiteDatabase db;

	public DataSource(final String path)
	{
		this.db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
	}

	public SQLiteDatabase getConnection()
	{
		return this.db;
	}

	public void close()
	{
		this.db.close();
	}
}
