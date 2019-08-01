/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class Settings
{
	private static final String PREF_DOWNLOADER = "pref_downloader";

	static public final String PREF_DB_NAME = "pref_db_name";
	static public final String PREF_DB_DATE = "pref_db_date";
	static public final String PREF_DB_SIZE = "pref_db_size";

	public static final String STORAGE_DB_DIR = "sqlunet";
	public static final String HINT_DB_ZIP = "sqlunet.zip";

	@Nullable
	public static String getDownloaderPref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getString(Settings.PREF_DOWNLOADER, null);
	}

	/**
	 * Get database name
	 *
	 * @param context context
	 * @return name
	 */
	@Nullable
	static public String getDbName(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getString(Settings.PREF_DB_NAME, null);
	}

	/**
	 * Set database name
	 *
	 * @param context context
	 * @param name    name
	 */
	static public void setDbName(final Context context, final String name)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPref.edit().putString(Settings.PREF_DB_NAME, name).apply();
	}

	/**
	 * Get database date
	 *
	 * @param context context
	 * @return timestamp
	 */
	static public long getDbDate(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getLong(Settings.PREF_DB_DATE, -1);
	}

	/**
	 * Set database date
	 *
	 * @param context   context
	 * @param timestamp timestamp
	 */
	static public void setDbDate(final Context context, final long timestamp)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPref.edit().putLong(Settings.PREF_DB_DATE, timestamp).apply();
	}

	/**
	 * Get database size
	 *
	 * @param context context
	 * @return size
	 */
	static public long getDbSize(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getLong(Settings.PREF_DB_SIZE, -1);
	}

	/**
	 * Set database size
	 *
	 * @param context context
	 * @param size    size
	 */
	static public void setDbSize(final Context context, final long size)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPref.edit().putLong(Settings.PREF_DB_SIZE, size).apply();
	}


	/**
	 * Clear database info
	 *
	 * @param context context
	 */
	static public void unrecordDb(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPref.edit() //
				.remove(Settings.PREF_DB_NAME) //
				.remove(Settings.PREF_DB_DATE) //
				.remove(Settings.PREF_DB_SIZE) //
				.apply();
	}
}
