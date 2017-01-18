package org.sqlunet.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import org.sqlunet.settings.StorageUtils.CandidateStorage;

import java.io.File;
import java.util.List;

/**
 * Storage
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Storage
{
	static private final String TAG = "Storage";
	/**
	 * SqlUnet DB filename
	 */
	public static final String DBFILE = "sqlunet.db";
	/**
	 * SqlUnet sub directory when external public
	 */
	static final String SQLUNETDIR = "sqlunet" + '/';
	/**
	 * SqlUnet storage preference name
	 */
	public static final String PREF_SQLUNET_STORAGE = "pref_storage";
	// D A T A B A S E

	/**
	 * Get database storage
	 *
	 * @param context context
	 * @return database storage directory
	 */
	@SuppressLint("CommitPrefEdits")
	static public File getSqlUNetStorage(final Context context)
	{
		// test if set in preference
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		final String pref = sharedPref.getString(Storage.PREF_SQLUNET_STORAGE, null);
		if (pref != null && !pref.isEmpty() && !"internal_or_adopted".equals(pref)) //
		{
			final File prefStorage = new File(pref);
			if (Storage.build(prefStorage))
			{
				Log.d(TAG, "Using pref " + prefStorage.getAbsolutePath());
				return prefStorage;
			}
		}

		// internal or adopted
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || "internal_or_adopted".equals(pref)) //
		{
			final File internalOrAdoptedStorage = context.getFilesDir();
			Log.d(TAG, "Internal or adopted " + internalOrAdoptedStorage.getAbsolutePath());
			return internalOrAdoptedStorage; // context.getDatabasePath(DBFILE).getParentFile();
		}

		// discover if (pref==null ||pref.isEmpty())
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
	static private File discover(final Context context)
	{
		final List<CandidateStorage> candidates = StorageUtils.getSortedCandidateStorages(context);
		for (CandidateStorage candidate : candidates)
		{
			if (candidate.status == 0)
			{
				Log.d(TAG, "Select " + candidate.toString());
				return candidate.dir.file;
			}
		}
		Log.e(TAG, "Error while looking for candidate storage. External storage is " + StorageUtils.reportExternalStorage());
		throw new RuntimeException("Cannot find suitable storage " + StorageReports.reportStyledCandidateStorage(context) + ' ' + StorageUtils.reportExternalStorage());
	}

	/**
	 * Build the dir and tests if it qualifies as sqlunet storage
	 *
	 * @param dir candidate dir
	 * @return true if it qualifies
	 */
	static private boolean build(final File dir)
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
	static public String getCacheDir(final Context context)
	{
		final File cache = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ? //
				context.getExternalCacheDir() : //
				context.getCacheDir();
		assert cache != null;
		return cache.getAbsolutePath();
	}
}
