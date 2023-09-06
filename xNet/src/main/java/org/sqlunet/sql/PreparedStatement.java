/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Prepared statement
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PreparedStatement
{
	static private final String TAG = "PreparedStatement";

	/**
	 * Whether to output SQL statements
	 */
	static public boolean logSql = false;

	/**
	 * Database
	 */
	private final SQLiteDatabase db;

	/**
	 * Sql
	 */
	private final String sql;

	/**
	 * Selection arguments
	 */
	private final SparseArray<String> selectionArgs = new SparseArray<>();

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param sql        sql
	 */
	public PreparedStatement(final SQLiteDatabase connection, final String sql)
	{
		this.db = connection;
		this.sql = sql;
	}

	/**
	 * Set string argument
	 *
	 * @param i      ith argument
	 * @param string string
	 */
	public void setString(final int i, final String string)
	{
		this.selectionArgs.put(i, string);
	}

	/**
	 * Set long argument
	 *
	 * @param i ith argument
	 * @param l long
	 */
	public void setLong(final int i, final long l)
	{
		this.selectionArgs.put(i, Long.toString(l));
	}

	/**
	 * Set int argument
	 *
	 * @param i ith argument
	 * @param n int
	 */
	public void setInt(final int i, final int n)
	{
		this.selectionArgs.put(i, Integer.toString(n));
	}

	/**
	 * Set null parameter
	 *
	 * @param i    ith parameter
	 * @param type type
	 */
	public void setNull(@SuppressWarnings("SameParameterValue") final int i, @SuppressWarnings({"UnusedParameters", "SameParameterValue"}) final int type)
	{
		this.selectionArgs.put(i, null);
	}

	/**
	 * Execute statement
	 *
	 * @return cursor
	 */
	@Nullable
	public Cursor executeQuery()
	{
		final String[] args = toSelectionArgs();
		if (PreparedStatement.logSql)
		{
			Log.d(PreparedStatement.TAG + "SQL", SqlFormatter.format(this.sql).toString());
			Log.d(PreparedStatement.TAG + "ARGS", TextUtils.join(",", args));
		}
		try
		{
			return this.db.rawQuery(this.sql, args);
		}
		catch (@NonNull final Exception e)
		{
			Log.e(PreparedStatement.TAG, this.sql + ' ' + Utils.argsToString(args), e);
			return null;
		}
	}

	/**
	 * Close
	 */
	@SuppressWarnings("EmptyMethod")
	public void close()
	{
		// do not close database
		// db.close();
	}

	/**
	 * Arguments to string array
	 *
	 * @return selection arguments as string array
	 */
	@NonNull
	private String[] toSelectionArgs()
	{
		final int n = this.selectionArgs.size();
		final String[] args = new String[n];
		for (int i = 0; i < n; i++)
		{
			args[i] = this.selectionArgs.get(i);
		}
		return args;
	}
}
