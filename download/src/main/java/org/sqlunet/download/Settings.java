/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;


@SuppressWarnings("WeakerAccess")
public class Settings
{
	/**
	 * Downloaders
	 */
	public enum Downloader
	{
		SIMPLE_SERVICE, //
		SIMPLE_ZIP_SERVICE, //
		DOWNLOAD_MANAGER;

		static Downloader getFromPref(@NonNull final Context context)
		{
			final String preferredDownloader = getDownloaderPref(context);
			if (preferredDownloader == null)
			{
				return SIMPLE_ZIP_SERVICE;
			}
			return Downloader.valueOf(preferredDownloader);
		}

		@Nullable
		public static String getDownloaderPref(@NonNull final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			return sharedPref.getString(Settings.PREF_DOWNLOADER, null);
		}

		@NonNull
		public static String zipDownloaderSource(@NonNull final Context context, @NonNull final String source)
		{
			return Settings.Downloader.isZipDownloaderPref(context) || source.endsWith(".zip") ? source + ".zip" : source;
		}

		public static boolean isZipDownloaderPref(@NonNull final Context context)
		{
			Downloader dl = Downloader.getFromPref(context);
			return dl.equals(SIMPLE_ZIP_SERVICE);
		}
	}

	static public final String PREF_DOWNLOADER = "pref_downloader";

	static public final String PREF_DB_SOURCE = "pref_db_source";
	static public final String PREF_DB_SOURCE_DATE = "pref_db_zip_date";
	static public final String PREF_DB_SOURCE_SIZE = "pref_db_zip_size";

	static public final String PREF_DB_NAME = "pref_db_name";
	static public final String PREF_DB_DATE = "pref_db_date";
	static public final String PREF_DB_SIZE = "pref_db_size";

	public static final String STORAGE_DB_DIR = "sqlunet";
	public static final String HINT_DB_ZIP = "sqlunet.zip";

	/**
	 * Get database name
	 *
	 * @param context context
	 * @return name
	 */
	@Nullable
	static public String getDbName(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getString(Settings.PREF_DB_NAME, null);
	}

	/**
	 * Get database date
	 *
	 * @param context context
	 * @return timestamp
	 */
	static public long getDbDate(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getLong(Settings.PREF_DB_DATE, -1);
	}

	/**
	 * Get database size
	 *
	 * @param context context
	 * @return size
	 */
	static public long getDbSize(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getLong(Settings.PREF_DB_SIZE, -1);
	}

	/**
	 * Get database source
	 *
	 * @param context context
	 * @return source
	 */
	@Nullable
	static public String getDbSource(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getString(Settings.PREF_DB_SOURCE, null);
	}

	/**
	 * Get database source date
	 *
	 * @param context context
	 * @return timestamp
	 */
	static public long getDbSourceDate(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getLong(Settings.PREF_DB_SOURCE_DATE, -1);
	}

	/**
	 * Get database source size
	 *
	 * @param context context
	 * @return timestamp
	 */
	static public long getDbSourceSize(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getLong(Settings.PREF_DB_SOURCE_SIZE, -1);
	}

	/**
	 * Record database info
	 *
	 * @param context      context
	 * @param databaseFile database file
	 */
	public static void recordDb(@NonNull final Context context, final File databaseFile)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		final SharedPreferences.Editor edit = sharedPref.edit(); //
		final FileData fileData = FileData.makeFileDataFrom(databaseFile);
		if (fileData != null)
		{
			if (fileData.name != null)
			{
				edit.putString(PREF_DB_NAME, fileData.name);
			}
			else
			{
				edit.remove(PREF_DB_NAME);
			}
			if (fileData.date != -1)
			{
				edit.putLong(PREF_DB_DATE, fileData.date);
			}
			else
			{
				edit.remove(PREF_DB_DATE);
			}
			if (fileData.size != -1)
			{
				edit.putLong(PREF_DB_SIZE, fileData.size);
			}
			else
			{
				edit.remove(PREF_DB_SIZE);
			}
		edit.apply();
		}
	}

	/**
	 * Clear database info
	 *
	 * @param context context
	 */
	static public void unrecordDb(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPref.edit() //
				.remove(Settings.PREF_DB_NAME) //
				.remove(Settings.PREF_DB_DATE) //
				.remove(Settings.PREF_DB_SIZE) //
				.apply();
	}

	/**
	 * Record db source info
	 *
	 * @param context context
	 * @param source  source
	 * @param date    data
	 * @param size    size
	 */
	public static void recordDbSource(@NonNull final Context context, @Nullable final String source, final long date, final long size)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		final SharedPreferences.Editor edit = sharedPref.edit(); //
		if (source != null)
		{
			edit.putString(PREF_DB_SOURCE, source);
		}
		else
		{
			edit.remove(PREF_DB_SOURCE);
		}
		if (date != -1)
		{
			edit.putLong(PREF_DB_SOURCE_DATE, date);
		}
		else
		{
			edit.remove(PREF_DB_SOURCE_DATE);
		}
		if (size != -1)
		{
			edit.putLong(PREF_DB_SOURCE_SIZE, size);
		}
		else
		{
			edit.remove(PREF_DB_SOURCE_SIZE);
		}
		edit.apply();
	}

	/**
	 * Clear database source info
	 *
	 * @param context context
	 */
	static public void unrecordDbSource(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPref.edit() //
				.remove(Settings.PREF_DB_SOURCE) //
				.remove(Settings.PREF_DB_SOURCE_DATE) //
				.remove(Settings.PREF_DB_SOURCE_SIZE) //
				.apply();
	}
}
