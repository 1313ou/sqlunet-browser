package org.sqlunet.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

import org.sqlunet.xnet.R;

import java.io.File;

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

	static public final String PREF_DOWNLOAD_SITE = "pref_download_site";
	static public final String PREF_DOWNLOADER = "pref_downloader";
	static public final String PREF_DOWNLOAD_DBFILE = "pref_download_dbfile";
	static public final String PREF_DOWNLOAD_SQLFILE = "pref_download_sqlfile";
	static public final String PREF_ENTRY_IMPORT = "pref_entry_import";
	static public final String PREF_ENTRY_INDEX = "pref_entry_index";

	// D A T A B A S E

	/**
	 * Get database directory
	 *
	 * @param context context
	 * @return database directory
	 */
	private static String getDataDir(@NonNull final Context context)
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
	static public String getDatabasePath(@NonNull final Context context)
	{
		return getDataDir(context) + File.separatorChar + Storage.DBFILE;
	}

	// C A C H E

	/**
	 * Get data cache
	 *
	 * @param context context
	 * @return data cache
	 */
	@NonNull
	static private String getCacheDir(@NonNull final Context context)
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
		value = context.getResources().getString(R.string.pref_default_download_site);

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
	static private String getDbDownloadFile(@NonNull final Context context)
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
	static public String getDbDownloadSource(@NonNull final Context context)
	{
		return StorageSettings.getDownloadSite(context) + '/' + StorageSettings.getDbDownloadFile(context);
	}

	/**
	 * Get download db target
	 *
	 * @param context context
	 * @return download db target
	 */
	static public String getDbDownloadTarget(@NonNull final Context context)
	{
		return StorageSettings.getDataDir(context) + File.separatorChar + Storage.DBFILE;
	}

	/**
	 * Get download db zipped source
	 *
	 * @param context context
	 * @return download db source
	 */
	static public String getDbDownloadZippedSource(@NonNull final Context context)
	{
		return StorageSettings.getDownloadSite(context) + '/' + StorageSettings.getDbDownloadFile(context) + ".zip";
	}

	/**
	 * Get download db zipped target
	 *
	 * @param context context
	 * @return download db target
	 */
	static public String getDbDownloadZippedTarget(@NonNull final Context context)
	{
		return StorageSettings.getCacheDir(context) + File.separatorChar + Storage.DBFILEZIP;
	}

	// S Q L

	/**
	 * Get download sql file
	 *
	 * @param context context
	 * @return download sql file
	 */
	@NonNull
	static private String getSqlDownloadFile(@NonNull final Context context)
	{
		// test if already already in preferences
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String value = sharedPref.getString(StorageSettings.PREF_DOWNLOAD_SQLFILE, null);
		if (value != null)
		{
			return value;
		}

		// set to default value
		value = context.getResources().getString(R.string.pref_default_download_sqlfile);

		// store value in preferences
		sharedPref.edit().putString(StorageSettings.PREF_DOWNLOAD_SQLFILE, value).apply();

		return value;
	}

	/**
	 * Get download sql source
	 *
	 * @param context context
	 * @return download sql source
	 */
	static public String getSqlDownloadSource(@NonNull final Context context)
	{

		return StorageSettings.getDownloadSite(context) + '/' + StorageSettings.getSqlDownloadFile(context);
	}

	/**
	 * Get download sql target
	 *
	 * @param context context
	 * @return download sql target
	 */
	static public String getSqlDownloadTarget(@NonNull final Context context)
	{
		return StorageSettings.getCacheDir(context) + File.separator + StorageSettings.getSqlDownloadFile(context);
	}

	/**
	 * Get SQL source for execution
	 *
	 * @param context context
	 * @return SQL source
	 */
	static public String getSqlSource(@NonNull final Context context)
	{
		return StorageSettings.getSqlDownloadTarget(context);
	}

	/**
	 * Get import archive entry
	 *
	 * @param context context
	 * @return import archive entry
	 */
	@NonNull
	static public String getImportEntry(@NonNull final Context context)
	{
		// test if already in preferences
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String value = sharedPref.getString(StorageSettings.PREF_ENTRY_IMPORT, null);
		if (value != null)
		{
			return value;
		}

		// set to default value
		value = context.getResources().getString(R.string.pref_default_entry_import);

		// store value in preferences
		sharedPref.edit().putString(StorageSettings.PREF_ENTRY_IMPORT, value).apply();

		return value;
	}

	/**
	 * Get index archive entry
	 *
	 * @param context context
	 * @return index archive entry
	 */
	@NonNull
	static public String getIndexEntry(@NonNull final Context context)
	{
		// test if already in preferences
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String value = sharedPref.getString(StorageSettings.PREF_ENTRY_INDEX, null);
		if (value != null)
		{
			return value;
		}

		// set to default value
		value = context.getResources().getString(R.string.pref_default_entry_index);

		// store value in preferences
		sharedPref.edit().putString(StorageSettings.PREF_ENTRY_INDEX, value).apply();

		return value;
	}
}
