/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.settings;

import android.content.Context;
import android.content.SharedPreferences;

import org.sqlunet.xnet.R;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

/**
 * Storage settings
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StorageSettings
{
	// storage preferences

	static public final String PREF_STORAGE = Storage.PREF_SQLUNET_STORAGE;
	static public final String PREF_CACHE = Storage.PREF_SQLUNET_CACHE;

	// download preferences

	static public final String PREF_DOWNLOADER = "pref_downloader";
	static public final String PREF_DOWNLOAD_SITE = "pref_download_site";
	static public final String PREF_DOWNLOAD_DBFILE = "pref_download_dbfile";

	// D A T A B A S E

	/**
	 * Get database directory
	 *
	 * @param context context
	 * @return database directory
	 */
	@NonNull
	static public String getDataDir(@NonNull final Context context)
	{
		final File dir = Storage.getSqlUNetStorage(context);
		if (!dir.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			dir.mkdirs();
			if (!dir.exists())
			{
				throw new IllegalStateException("Can't make directory " + dir.getAbsolutePath());
			}
		}
		return dir.getAbsolutePath();
	}

	/**
	 * Get database path
	 *
	 * @param context context
	 * @return database path
	 */
	@NonNull
	static public String getDatabasePath(@NonNull final Context context)
	{
		return getDataDir(context) + File.separatorChar + Storage.DBFILE;
	}

	/**
	 * Get database name
	 *
	 * @param context context
	 * @return database name
	 */
	@NonNull
	@SuppressWarnings("SameReturnValue")
	static public String getDatabaseName(@NonNull final Context context)
	{
		return Storage.DBFILE;
	}

	// C A C H E

	/**
	 * Get data cache
	 *
	 * @param context context
	 * @return data cache
	 */
	@NonNull
	static public String getCacheDir(@NonNull final Context context)
	{
		return Storage.getCacheDir(context);
	}

	// D O W N L O A D

	/**
	 * Get download site
	 *
	 * @param context context
	 * @return download site
	 */
	@NonNull
	static private String getDownloadSite(@NonNull final Context context)
	{
		// test if already in preferences
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String value = sharedPref.getString(StorageSettings.PREF_DOWNLOAD_SITE, null);
		if (value != null)
		{
			return value;
		}

		// set to default value
		value = context.getResources().getString(R.string.pref_default_download_site_url);

		// store value in preferences
		sharedPref.edit().putString(StorageSettings.PREF_DOWNLOAD_SITE, value).apply();

		return value;
	}

	// D A T A B A S E

	/**
	 * Get download db file
	 *
	 * @param context context
	 * @return download db file
	 */
	@NonNull
	static public String getDbDownloadFile(@NonNull final Context context)
	{
		// test if already already in preferences
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String value = sharedPref.getString(StorageSettings.PREF_DOWNLOAD_DBFILE, null);
		if (value != null)
		{
			return value;
		}

		// set to default value
		value = context.getResources().getString(R.string.pref_default_download_dbfile);

		// store value in preferences
		sharedPref.edit().putString(StorageSettings.PREF_DOWNLOAD_DBFILE, value).apply();

		return value;
	}

	/**
	 * Get download db source
	 *
	 * @param context context
	 * @return download db source
	 */
	@NonNull
	static public String getDbDownloadSource(@NonNull final Context context)
	{
		return StorageSettings.getDownloadSite(context) + '/' + StorageSettings.getDbDownloadFile(context);
	}

	/**
	 * Get download db zipped source
	 *
	 * @param context context
	 * @return download db source
	 */
	@NonNull
	static public String getDbDownloadZippedSource(@NonNull final Context context)
	{
		return StorageSettings.getDbDownloadSource(context) + ".zip";
	}

	/**
	 * Get download db source as per downloader
	 *
	 * @param context       context
	 * @param zipDownloader whether downloader is zip downloader
	 * @return download db source
	 */
	@NonNull
	static public String getDbDownloadSource(@NonNull final Context context, boolean zipDownloader)
	{
		return zipDownloader ? StorageSettings.getDbDownloadZippedSource(context) : StorageSettings.getDbDownloadSource(context);
	}

	/**
	 * Get download db target
	 *
	 * @param context context
	 * @return download db target
	 */
	@NonNull
	static public String getDbDownloadTarget(@NonNull final Context context)
	{
		return getDataDir(context) + File.separatorChar + Storage.DBFILE;
	}

	/**
	 * Get download db zipped target
	 *
	 * @param context context
	 * @return download db target
	 */
	@NonNull
	static public String getDbDownloadZippedTarget(@NonNull final Context context)
	{
		return StorageSettings.getCacheDir(context) + File.separatorChar + Storage.DBFILEZIP;
	}
}
