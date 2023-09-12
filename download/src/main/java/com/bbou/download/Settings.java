/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

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
		DOWNLOAD, //

		DOWNLOAD_ZIP;

		@NonNull
		public static String zipDownloaderSource(@NonNull final Context context, @NonNull final String source)
		{
			if (Settings.Downloader.isZipDownloaderPref(context) && !source.endsWith(".zip"))
			{
				return source + ".zip";
			}
			return source;
		}

		public static boolean isZipDownloaderPref(@NonNull final Context context)
		{
			Downloader dl = Downloader.getDownloaderFromPref(context);
			return dl.equals(DOWNLOAD_ZIP);
		}

		@Nullable
		public static String getDownloaderPrefString(@NonNull final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			return sharedPref.getString(Settings.PREF_DOWNLOADER, null);
		}

		public static Downloader getDownloaderFromPref(@NonNull final Context context)
		{
			final String preferredDownloader = getDownloaderPrefString(context);
			if (preferredDownloader == null)
			{
				return DOWNLOAD;
			}
			return Downloader.valueOf(preferredDownloader);
		}
	}

	// downloader preference key
	static public final String PREF_DOWNLOADER = "pref_downloader";

	// preference names
	static public final String PREFERENCES_DEVICE = "preferences_device";
	static public final String PREFERENCES_DATAPACK = "preferences_datapack";

	// general prefs
	static public final String PREF_REPO = "pref_repo";

	// device prefs
	static public final String PREF_CACHE = "pref_cache";
	static public final String PREF_DATAPACK_DIR = "pref_datapack_dir";

	// datapack preference keys
	static public final String PREF_DATAPACK_NAME = "pref_datapack_name";
	static public final String PREF_DATAPACK_DATE = "pref_datapack_date";
	static public final String PREF_DATAPACK_SIZE = "pref_datapack_size";

	// datapack source preference keys
	static public final String PREF_DATAPACK_SOURCE = "pref_datapack_source";
	static public final String PREF_DATAPACK_SOURCE_DATE = "pref_datapack_source_date";
	static public final String PREF_DATAPACK_SOURCE_SIZE = "pref_datapack_source_size";
	static public final String PREF_DATAPACK_SOURCE_ETAG = "pref_datapack_source_etag";
	static public final String PREF_DATAPACK_SOURCE_VERSION = "pref_datapack_source_version";
	static public final String PREF_DATAPACK_SOURCE_STATIC_VERSION = "pref_datapack_source_static_version";

	// clear button preference key
	static public final String PREF_DATAPACK_CLEAR_BUTTON = "pref_datapack_clear";

	// R E P O

	@Nullable
	public static String getRepoPref(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getString(Settings.PREF_REPO, null);
	}

	public static void setRepoPref(@NonNull final Context context, @Nullable final String repo)
	{
		final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		if (repo == null)
		{
			editor.remove(Settings.PREF_REPO);
		}
		else
		{
			editor.putString(Settings.PREF_REPO, repo);
		}
		editor.apply();
	}

	/**
	 * Downloader type
	 *
	 * @param context context
	 * @return downloader type
	 */
	@Nullable
	public static String getDownloaderPref(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getString(Settings.PREF_DOWNLOADER, null);
	}

	// D E V I C E

	@Nullable
	public static String getDatapackDir(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DEVICE, Context.MODE_PRIVATE);
		return sharedPref.getString(Settings.PREF_DATAPACK_DIR, null);
	}

	public static void setDatapackDir(@NonNull final Context context, final String dest)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DEVICE, Context.MODE_PRIVATE);
		sharedPref.edit().putString(Settings.PREF_DATAPACK_DIR, dest).apply();
	}

	@Nullable
	public static String getCachePref(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DEVICE, Context.MODE_PRIVATE);
		return sharedPref.getString(Settings.PREF_CACHE, null);
	}

	public static void setCachePref(@NonNull final Context context, final String cache)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DEVICE, Context.MODE_PRIVATE);
		sharedPref.edit().putString(Settings.PREF_CACHE, cache).apply();
	}

	// M O D E L

	/**
	 * Get datapack name
	 *
	 * @param context context
	 * @return name
	 */
	@Nullable
	static public String getDatapackName(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		return sharedPref.getString(Settings.PREF_DATAPACK_NAME, null);
	}

	/**
	 * Set datapack name
	 *
	 * @param context context
	 * @param name    name
	 */
	static public void setDatapackName(@NonNull final Context context, final String name)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		sharedPref.edit().putString(Settings.PREF_DATAPACK_NAME, name).apply();
	}

	/**
	 * Get datapack date
	 *
	 * @param context context
	 * @return timestamp
	 */
	static public long getDatapackDate(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		return sharedPref.getLong(Settings.PREF_DATAPACK_DATE, -1);
	}

	/**
	 * Set datapack date
	 *
	 * @param context   context
	 * @param timestamp timestamp
	 */
	static public void setDatapackDate(@NonNull final Context context, final long timestamp)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		sharedPref.edit().putLong(Settings.PREF_DATAPACK_DATE, timestamp).apply();
	}

	/**
	 * Get datapack size
	 *
	 * @param context context
	 * @return size
	 */
	static public long getDatapackSize(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		return sharedPref.getLong(Settings.PREF_DATAPACK_SIZE, -1);
	}

	/**
	 * Set datapack size
	 *
	 * @param context context
	 * @param size    size
	 */
	static public void setDatapackSize(@NonNull final Context context, final long size)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		sharedPref.edit().putLong(Settings.PREF_DATAPACK_SIZE, size).apply();
	}

	/**
	 * Get datapack source
	 *
	 * @param context context
	 * @return datapack source (repo)
	 */
	@Nullable
	public static String getDatapackSource(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		return sharedPref.getString(Settings.PREF_DATAPACK_SOURCE, null);
	}

	/**
	 * Get datapack source date
	 *
	 * @param context context
	 * @return timestamp
	 */
	static public long getDatapackSourceDate(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		return sharedPref.getLong(Settings.PREF_DATAPACK_SOURCE_DATE, -1);
	}

	/**
	 * Get datapack source size
	 *
	 * @param context context
	 * @return size
	 */
	static public long getDatapackSourceSize(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		return sharedPref.getLong(Settings.PREF_DATAPACK_SOURCE_SIZE, -1);
	}

	/**
	 * Get datapack source etag
	 *
	 * @param context context
	 * @return etag
	 */
	@Nullable
	public static String getDatapackSourceEtag(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		return sharedPref.getString(Settings.PREF_DATAPACK_SOURCE_ETAG, null);
	}

	/**
	 * Get datapack source version
	 *
	 * @param context context
	 * @return version
	 */
	@Nullable
	public static String getDatapackSourceVersion(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		return sharedPref.getString(Settings.PREF_DATAPACK_SOURCE_VERSION, null);
	}

	/**
	 * Get datapack source static version
	 *
	 * @param context context
	 * @return static version
	 */
	@Nullable
	public static String getDatapackSourceStaticVersion(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		return sharedPref.getString(Settings.PREF_DATAPACK_SOURCE_STATIC_VERSION, null);
	}

	/**
	 * Record datapack info
	 *
	 * @param context      context
	 * @param datapackFile datapack file
	 */
	public static void recordDatapack(@NonNull final Context context, final File datapackFile)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		final SharedPreferences.Editor edit = sharedPref.edit(); //
		final FileData fileData = FileData.makeFileDataFrom(datapackFile);
		if (fileData != null)
		{
			if (fileData.name != null)
			{
				edit.putString(PREF_DATAPACK_NAME, fileData.name);
			}
			else
			{
				edit.remove(PREF_DATAPACK_NAME);
			}
			if (fileData.date != -1)
			{
				edit.putLong(PREF_DATAPACK_DATE, fileData.date);
			}
			else
			{
				edit.remove(PREF_DATAPACK_DATE);
			}
			if (fileData.size != -1)
			{
				edit.putLong(PREF_DATAPACK_SIZE, fileData.size);
			}
			else
			{
				edit.remove(PREF_DATAPACK_SIZE);
			}
			edit.apply();
		}
	}

	/**
	 * Record datapack info
	 *
	 * @param context     context
	 * @param datapackUri datapack uri
	 */
	public static void recordDatapack(@NonNull final Context context, final String datapackUri)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		final SharedPreferences.Editor edit = sharedPref.edit(); //

		if (datapackUri != null)
		{
			edit.putString(PREF_DATAPACK_NAME, datapackUri);
		}
		else
		{
			edit.remove(PREF_DATAPACK_NAME);
		}
		edit.remove(PREF_DATAPACK_DATE);
		edit.remove(PREF_DATAPACK_SIZE);
		edit.apply();
	}

	/**
	 * Unrecord datapack info
	 *
	 * @param context context
	 */
	public static void unrecordDatapack(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		final SharedPreferences.Editor edit = sharedPref.edit(); //
		edit.remove(PREF_DATAPACK_NAME) //
				.remove(PREF_DATAPACK_DATE) //
				.remove(PREF_DATAPACK_SIZE) //
				.apply();
	}

	/**
	 * Record datapack source info
	 *
	 * @param context       context
	 * @param source        source
	 * @param date          data
	 * @param size          size
	 * @param etag          etag
	 * @param version       version
	 * @param staticVersion staticVersion
	 */
	public static void recordDatapackSource(@NonNull final Context context, @Nullable final String source, final long date, final long size, @Nullable final String etag, @Nullable final String version, @Nullable final String staticVersion)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		final SharedPreferences.Editor edit = sharedPref.edit(); //
		if (source != null)
		{
			edit.putString(PREF_DATAPACK_SOURCE, source);
		}
		else
		{
			edit.remove(PREF_DATAPACK_SOURCE);
		}
		if (date != -1)
		{
			edit.putLong(PREF_DATAPACK_SOURCE_DATE, date);
		}
		else
		{
			edit.remove(PREF_DATAPACK_SOURCE_DATE);
		}
		if (size != -1)
		{
			edit.putLong(PREF_DATAPACK_SOURCE_SIZE, size);
		}
		else
		{
			edit.remove(PREF_DATAPACK_SOURCE_SIZE);
		}
		if (etag != null)
		{
			edit.putString(PREF_DATAPACK_SOURCE_ETAG, etag);
		}
		else
		{
			edit.remove(PREF_DATAPACK_SOURCE_ETAG);
		}
		if (version != null)
		{
			edit.putString(PREF_DATAPACK_SOURCE_VERSION, version);
		}
		else
		{
			edit.remove(PREF_DATAPACK_SOURCE_VERSION);
		}
		if (staticVersion != null)
		{
			edit.putString(PREF_DATAPACK_SOURCE_STATIC_VERSION, staticVersion);
		}
		else
		{
			edit.remove(PREF_DATAPACK_SOURCE_STATIC_VERSION);
		}
		edit.apply();
	}

	/**
	 * Clear datapack source info
	 *
	 * @param context context
	 */
	static public void unrecordDatapackSource(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE);
		sharedPref.edit() //
				.remove(Settings.PREF_DATAPACK_SOURCE) //
				.remove(Settings.PREF_DATAPACK_SOURCE_DATE) //
				.remove(Settings.PREF_DATAPACK_SOURCE_SIZE) //
				.remove(Settings.PREF_DATAPACK_SOURCE_ETAG) //
				.remove(Settings.PREF_DATAPACK_SOURCE_VERSION) //
				.remove(Settings.PREF_DATAPACK_SOURCE_STATIC_VERSION) //
				.apply();
	}
}
