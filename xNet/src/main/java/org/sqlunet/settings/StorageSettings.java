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

	static public final String PREF_DOWNLOAD_MODE = "pref_download_mode";
	static public final String PREF_DOWNLOAD_SITE = "pref_download_site";
	static public final String PREF_DOWNLOAD_DBFILE = "pref_download_dbfile";

	// L O C A L   D A T A B A S E

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
	 * Get database name
	 *
	 * @return database name
	 */
	@NonNull
	@SuppressWarnings("SameReturnValue")
	static public String getDatabaseName()
	{
		return Storage.DBFILE;
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
		return getDataDir(context) + File.separatorChar + getDatabaseName();
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

	/**
	 * Get download db zipped target
	 *
	 * @param context context
	 * @return cached target
	 */
	@NonNull
	static public String getCachedZippedPath(@NonNull final Context context)
	{
		return getCacheDir(context) + File.separatorChar + getDbDownloadZipName(context);
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
		String value = sharedPref.getString(PREF_DOWNLOAD_SITE, null);
		if (value != null)
		{
			return value;
		}

		// set to default value read from resources
		value = context.getResources().getString(R.string.default_download_site_url);

		// store value in preferences
		sharedPref.edit().putString(PREF_DOWNLOAD_SITE, value).apply();

		return value;
	}

	/**
	 * Get download db file
	 *
	 * @param context context
	 * @return download db file
	 */
	@NonNull
	static public String getDbDownloadName(@NonNull final Context context)
	{
		// test if already already in preferences
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String value = sharedPref.getString(PREF_DOWNLOAD_DBFILE, null);
		if (value != null)
		{
			return value;
		}

		// set to default value read from resources
		value = context.getResources().getString(R.string.default_download_datapack_file);

		// store value in preferences
		sharedPref.edit().putString(PREF_DOWNLOAD_DBFILE, value).apply();

		return value;
	}

	/**
	 * Get download db file
	 *
	 * @param context context
	 * @return download db zip file
	 */
	@NonNull
	static public String getDbDownloadZipName(@NonNull final Context context)
	{
		return zipped(getDbDownloadName(context));
	}

	/**
	 * Get download db source file
	 *
	 * @param context context
	 * @return download db source
	 */
	@NonNull
	static public String getDbDownloadSourcePath(@NonNull final Context context)
	{
		return getDownloadSite(context) + '/' + getDbDownloadName(context);
	}

	/**
	 * Get download db zipped source
	 *
	 * @param context context
	 * @return download db zip source
	 */
	@NonNull
	static public String getDbDownloadZippedSourcePath(@NonNull final Context context)
	{
		return zipped(getDbDownloadSourcePath(context));
	}

	/**
	 * Get download db source as per downloader
	 *
	 * @param context context
	 * @param zipped  whether downloader needs zipped file or stream
	 * @return download db zip source
	 */
	@NonNull
	static public String getDbDownloadSourcePath(@NonNull final Context context, boolean zipped)
	{
		return zipped ? getDbDownloadZippedSourcePath(context) : getDbDownloadSourcePath(context);
	}

	static private String zipped(@NonNull final String unzipped)
	{
		return unzipped + ".zip";
	}
}
