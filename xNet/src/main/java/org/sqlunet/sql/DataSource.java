/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.sql;

import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

/**
 * Data source wrapping to use JDBC style
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DataSource implements AutoCloseable
{
	/**
	 * Database
	 */
	private final SQLiteDatabase db;

	/**
	 * Constructor
	 *
	 * @param path path to database
	 */
	public DataSource(@NonNull final String path)
	{
		this.db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
	}

	/**
	 * Get connection
	 *
	 * @return database
	 */
	public SQLiteDatabase getConnection()
	{
		return this.db;
	}

	/**
	 * Close
	 */
	public void close()
	{
		this.db.close();
	}
}
