/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import org.sqlunet.settings.StorageUtils.StorageDirectory;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Storage
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Storage
{
	static private final String TAG = "Storage";

	/**
	 * SqlUNet DB basename
	 */
	static private final String DBNAME = "sqlunet";

	/**
	 * SqlUNet DB filename
	 */
	static public final String DBFILE = DBNAME + ".db";

	/**
	 * SqlUNet DB zipped filename
	 */
	static public final String DBFILEZIP = DBFILE + ".zip";

	/**
	 * SqlUNet DB sql filename
	 */
	static private final String DBSQL = DBNAME + ".sql";

	/**
	 * SqlUNet DB zipped sql filename
	 */
	static public final String DBSQLZIP = DBSQL + ".zip";

	/**
	 * SqlUNet sub directory when external public
	 */
	static final String SQLUNETDIR = "sqlunet" + '/';

	/**
	 * SqlUNet storage preference name
	 */
	static public final String PREF_SQLUNET_STORAGE = "pref_storage";

	/**
	 * SqlUNet cache preference name
	 */
	static public final String PREF_SQLUNET_CACHE = "pref_cache";

	// D A T A B A S E

	/**
	 * Get database storage
	 *
	 * @param context context
	 * @return database storage directory
	 */
	@NonNull
	@SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
	static public File getSqlUNetStorage(@NonNull final Context context)
	{
		// test if set in preference
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		final String prefValue = sharedPref.getString(Storage.PREF_SQLUNET_STORAGE, null);
		if (prefValue != null && !prefValue.isEmpty()) //
		{
			// pref defined
			if (!StorageUtils.DirType.AUTO.toString().equals(prefValue))
			{
				final File prefStorage = new File(prefValue);
				if (Storage.build(prefStorage))
				{
					Log.d(TAG, "Using pref " + prefStorage.getAbsolutePath());
					return prefStorage;
				}
				//  pref defined as invalid value
			}
			//  pref defined as auto
		}
		//  pref not defined
		//  pref not defined || defined as auto || defined as invalid value

		// auto (as of marshmallow which allows for adopted storage)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && StorageUtils.DirType.AUTO.toString().equals(prefValue)) //
		{
			final File autoStorage = context.getFilesDir();
			Log.d(TAG, StorageUtils.DirType.AUTO.toDisplay() + ' ' + autoStorage.getAbsolutePath());
			return autoStorage; // context.getDatabasePath(DBFILE).getParentFile();
		}

		// discover pref not defined || defined as auto but not marshmallow || defined as invalid value
		final File discoveredStorage = Storage.discover(context);

		// record as discovered
		sharedPref.edit().putString(Storage.PREF_SQLUNET_STORAGE, discoveredStorage.getAbsolutePath()).commit();
		Log.d(TAG, "Saving " + discoveredStorage.getAbsolutePath());
		return discoveredStorage;
	}

	/**
	 * Discover SqlUNet storage
	 *
	 * @param context context
	 * @return SqlUNet storage
	 */
	static private File discover(@NonNull final Context context)
	{
		final List<StorageDirectory> dirs = StorageUtils.getSortedStorageDirectories(context);
		for (StorageDirectory dir : dirs)
		{
			if (dir.status == 0)
			{
				Log.d(TAG, "Select " + dir.toString());
				return dir.dir.getFile();
			}
		}
		Log.e(TAG, "Error while looking for storage directories. External storage is " + StorageReports.reportExternalStorage());
		throw new RuntimeException("Cannot find suitable storage directory " + StorageReports.reportStorageDirectories(context) + ' ' + StorageReports.reportExternalStorage());
	}

	/**
	 * Build the dir and tests
	 *
	 * @param dir directory
	 * @return true if it qualifies
	 */
	static private boolean build(@Nullable final File dir)
	{
		if (dir == null)
		{
			return false;
		}

		// make sure that path can be created and it is a directory
		return dir.mkdirs() || dir.isDirectory();
	}

	// C A C H E (cache is used to download SQL zip file)

	/**
	 * Get data cache
	 *
	 * @param context context
	 * @return data cache
	 */
	@NonNull
	@SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
	static public String getCacheDir(@NonNull final Context context)
	{
		// test if set in preference
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String prefValue = sharedPref.getString(Storage.PREF_SQLUNET_CACHE, null);
		if (prefValue == null || prefValue.isEmpty())
		{
			File cache = context.getExternalCacheDir();
			if (cache == null)
			{
				cache = context.getCacheDir();
			}
			prefValue = cache.getAbsolutePath();

			// record as discovered
			sharedPref.edit().putString(Storage.PREF_SQLUNET_CACHE, prefValue).commit();
		}

		return prefValue;
	}
}
