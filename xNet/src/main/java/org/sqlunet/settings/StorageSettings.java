package org.sqlunet.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.sqlunet.xnet.R;

import java.io.File;

/**
 * Storage settings
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StorageSettings
{
	// database

	/**
	 * DB file
	 */
	static private final String DBFILE = Storage.DBFILE;

	// storage preferences

	static public final String PREF_STORAGE = Storage.PREF_SQLUNET_STORAGE;

	// download preferences

	static public final String PREF_DOWNLOAD_SITE = "pref_download_site";
	static public final String PREF_DOWNLOAD_DBFILE = "pref_download_dbfile";
	static public final String PREF_DOWNLOAD_SQLFILE = "pref_download_sqlfile";
	static public final String PREF_ENTRY_IMPORT = "pref_entry_import";
	static public final String PREF_ENTRY_PM = "pref_entry_pm";
	static public final String PREF_ENTRY_INDEX = "pref_entry_index";
	// D A T A B A S E

	/**
	 * Get database directory
	 *
	 * @param context context
	 * @return database directory
	 */
	static public String getDataDir(final Context context)
	{
		final File dir = Storage.getSqlUNetStorage(context);
		if (!dir.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			dir.mkdirs();
		}
		return dir.getAbsolutePath();
	}

	/**
	 * Get database path
	 *
	 * @param context context
	 * @return database path
	 */
	static public String getDatabasePath(final Context context)
	{
		return getDataDir(context) + File.separator + StorageSettings.DBFILE;
	}

	// C A C H E

	/**
	 * Get data cache
	 *
	 * @param context context
	 * @return data cache
	 */
	static public String getCacheDir(final Context context)
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
	private static String getDownloadSite(final Context context)
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
	private static String getDbDownloadFile(final Context context)
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
	static public String getDbDownloadSource(final Context context)
	{
		return StorageSettings.getDownloadSite(context) + '/' + StorageSettings.getDbDownloadFile(context);
	}

	/**
	 * Get download db target
	 *
	 * @param context context
	 * @return download db target
	 */
	static public String getDbDownloadTarget(final Context context)
	{
		return StorageSettings.getDataDir(context) + File.separator + StorageSettings.DBFILE;
	}

	// S Q L

	/**
	 * Get download sql file
	 *
	 * @param context context
	 * @return download sql file
	 */
	private static String getSqlDownloadFile(final Context context)
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
	static public String getSqlDownloadSource(final Context context)
	{

		return StorageSettings.getDownloadSite(context) + '/' + StorageSettings.getSqlDownloadFile(context);
	}

	/**
	 * Get download sql target
	 *
	 * @param context context
	 * @return download sql target
	 */
	static public String getSqlDownloadTarget(final Context context)
	{
		return StorageSettings.getCacheDir(context) + File.separator + StorageSettings.getSqlDownloadFile(context);
	}

	/**
	 * Get SQL source for execution
	 *
	 * @param context context
	 * @return SQL source
	 */
	static public String getSqlSource(final Context context)
	{
		return StorageSettings.getSqlDownloadTarget(context);
	}

	/**
	 * Get import archive entry
	 *
	 * @param context context
	 * @return import archive entry
	 */
	public static String getImportEntry(final Context context)
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
	 * Get pm archive entry
	 *
	 * @param context context
	 * @return pm archive entry
	 */
	public static String getPmEntry(final Context context)
	{
		// test if already in preferences
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String value = sharedPref.getString(StorageSettings.PREF_ENTRY_PM, null);
		if (value != null)
		{
			return value;
		}

		// set to default value
		value = context.getResources().getString(R.string.pref_default_entry_pm);

		// store value in preferences
		sharedPref.edit().putString(StorageSettings.PREF_ENTRY_PM, value).apply();

		return value;
	}

	/**
	 * Get index archive entry
	 *
	 * @param context context
	 * @return index archive entry
	 */
	public static String getIndexEntry(final Context context)
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
