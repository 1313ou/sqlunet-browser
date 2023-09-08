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
			return dl.equals(DOWNLOAD_ZIP) ;
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
	static public final String PREFERENCES_DEVICE = "org.grammarscope_preferences_device";
	static public final String PREFERENCES_MODEL = "org.grammarscope_preferences_model";

	// general prefs
	static public final String PREF_REPO = "pref_repo";

	// device prefs
	static public final String PREF_CACHE = "pref_cache";
	static public final String PREF_MODEL_DIR = "pref_model_dir";

	// model preference keys
	static public final String PREF_MODEL_NAME = "pref_model_name";
	static public final String PREF_MODEL_DATE = "pref_model_date";
	static public final String PREF_MODEL_SIZE = "pref_model_size";

	// model source preference keys
	static public final String PREF_MODEL_SOURCE = "pref_model_source";
	static public final String PREF_MODEL_SOURCE_DATE = "pref_model_source_date";
	static public final String PREF_MODEL_SOURCE_SIZE = "pref_model_source_size";
	static public final String PREF_MODEL_SOURCE_ETAG = "pref_model_source_etag";
	static public final String PREF_MODEL_SOURCE_VERSION = "pref_model_source_version";
	static public final String PREF_MODEL_SOURCE_STATIC_VERSION = "pref_model_source_static_version";

	// clear button preference key
	static public final String PREF_MODEL_CLEAR_BUTTON = "pref_model_clear";

	// others
	public static final String HINT_DB_ZIP = "sqlunet.zip";
	public static final String HINT_DB_ZIP_ENTRY = "sqlunet.db";

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
	public static String getModelDir(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DEVICE, Context.MODE_PRIVATE);
		return sharedPref.getString(Settings.PREF_MODEL_DIR, null);
	}

	public static void setModelDir(@NonNull final Context context, final String dest)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_DEVICE, Context.MODE_PRIVATE);
		sharedPref.edit().putString(Settings.PREF_MODEL_DIR, dest).apply();
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
	 * Get model name
	 *
	 * @param context context
	 * @return name
	 */
	@Nullable
	static public String getModelName(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		return sharedPref.getString(Settings.PREF_MODEL_NAME, null);
	}

	/**
	 * Set model name
	 *
	 * @param context context
	 * @param name    name
	 */
	static public void setModelName(@NonNull final Context context, final String name)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		sharedPref.edit().putString(Settings.PREF_MODEL_NAME, name).apply();
	}

	/**
	 * Get model date
	 *
	 * @param context context
	 * @return timestamp
	 */
	static public long getModelDate(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		return sharedPref.getLong(Settings.PREF_MODEL_DATE, -1);
	}

	/**
	 * Set model date
	 *
	 * @param context   context
	 * @param timestamp timestamp
	 */
	static public void setModelDate(@NonNull final Context context, final long timestamp)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		sharedPref.edit().putLong(Settings.PREF_MODEL_DATE, timestamp).apply();
	}

	/**
	 * Get model size
	 *
	 * @param context context
	 * @return size
	 */
	static public long getModelSize(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		return sharedPref.getLong(Settings.PREF_MODEL_SIZE, -1);
	}

	/**
	 * Set model size
	 *
	 * @param context context
	 * @param size    size
	 */
	static public void setModelSize(@NonNull final Context context, final long size)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		sharedPref.edit().putLong(Settings.PREF_MODEL_SIZE, size).apply();
	}

	/**
	 * Get model source
	 *
	 * @param context context
	 * @return model source (repo)
	 */
	@Nullable
	public static String getModelSource(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		return sharedPref.getString(Settings.PREF_MODEL_SOURCE, null);
	}

	/**
	 * Get model source date
	 *
	 * @param context context
	 * @return timestamp
	 */
	static public long getModelSourceDate(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		return sharedPref.getLong(Settings.PREF_MODEL_SOURCE_DATE, -1);
	}

	/**
	 * Get model source size
	 *
	 * @param context context
	 * @return size
	 */
	static public long getModelSourceSize(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		return sharedPref.getLong(Settings.PREF_MODEL_SOURCE_SIZE, -1);
	}

	/**
	 * Get model source etag
	 *
	 * @param context context
	 * @return etag
	 */
	@Nullable
	public static String getModelSourceEtag(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		return sharedPref.getString(Settings.PREF_MODEL_SOURCE_ETAG, null);
	}

	/**
	 * Get model source version
	 *
	 * @param context context
	 * @return version
	 */
	@Nullable
	public static String getModelSourceVersion(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		return sharedPref.getString(Settings.PREF_MODEL_SOURCE_VERSION, null);
	}

	/**
	 * Get model source static version
	 *
	 * @param context context
	 * @return static version
	 */
	@Nullable
	public static String getModelSourceStaticVersion(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		return sharedPref.getString(Settings.PREF_MODEL_SOURCE_STATIC_VERSION, null);
	}

	/**
	 * Record model info
	 *
	 * @param context   context
	 * @param modelFile model file
	 */
	public static void recordModel(@NonNull final Context context, final File modelFile)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		final SharedPreferences.Editor edit = sharedPref.edit(); //
		final FileData fileData = FileData.makeFileDataFrom(modelFile);
		if (fileData != null)
		{
			if (fileData.name != null)
			{
				edit.putString(PREF_MODEL_NAME, fileData.name);
			}
			else
			{
				edit.remove(PREF_MODEL_NAME);
			}
			if (fileData.date != -1)
			{
				edit.putLong(PREF_MODEL_DATE, fileData.date);
			}
			else
			{
				edit.remove(PREF_MODEL_DATE);
			}
			if (fileData.size != -1)
			{
				edit.putLong(PREF_MODEL_SIZE, fileData.size);
			}
			else
			{
				edit.remove(PREF_MODEL_SIZE);
			}
			edit.apply();
		}
	}

	/**
	 * Record model info
	 *
	 * @param context  context
	 * @param modelUri model uri
	 */
	public static void recordModel(@NonNull final Context context, final String modelUri)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		final SharedPreferences.Editor edit = sharedPref.edit(); //

		if (modelUri != null)
		{
			edit.putString(PREF_MODEL_NAME, modelUri);
		}
		else
		{
			edit.remove(PREF_MODEL_NAME);
		}
		edit.remove(PREF_MODEL_DATE);
		edit.remove(PREF_MODEL_SIZE);
		edit.apply();
	}

	/**
	 * Unrecord model info
	 *
	 * @param context context
	 */
	public static void unrecordModel(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		final SharedPreferences.Editor edit = sharedPref.edit(); //
		edit.remove(PREF_MODEL_NAME) //
				.remove(PREF_MODEL_DATE) //
				.remove(PREF_MODEL_SIZE) //
				.apply();
	}

	/**
	 * Record model source info
	 *
	 * @param context       context
	 * @param source        source
	 * @param date          data
	 * @param size          size
	 * @param etag          etag
	 * @param version       version
	 * @param staticVersion staticVersion
	 */
	public static void recordModelSource(@NonNull final Context context, @Nullable final String source, final long date, final long size, @Nullable final String etag, @Nullable final String version, @Nullable final String staticVersion)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		final SharedPreferences.Editor edit = sharedPref.edit(); //
		if (source != null)
		{
			edit.putString(PREF_MODEL_SOURCE, source);
		}
		else
		{
			edit.remove(PREF_MODEL_SOURCE);
		}
		if (date != -1)
		{
			edit.putLong(PREF_MODEL_SOURCE_DATE, date);
		}
		else
		{
			edit.remove(PREF_MODEL_SOURCE_DATE);
		}
		if (size != -1)
		{
			edit.putLong(PREF_MODEL_SOURCE_SIZE, size);
		}
		else
		{
			edit.remove(PREF_MODEL_SOURCE_SIZE);
		}
		if (etag != null)
		{
			edit.putString(PREF_MODEL_SOURCE_ETAG, etag);
		}
		else
		{
			edit.remove(PREF_MODEL_SOURCE_ETAG);
		}
		if (version != null)
		{
			edit.putString(PREF_MODEL_SOURCE_VERSION, version);
		}
		else
		{
			edit.remove(PREF_MODEL_SOURCE_VERSION);
		}
		if (staticVersion != null)
		{
			edit.putString(PREF_MODEL_SOURCE_STATIC_VERSION, staticVersion);
		}
		else
		{
			edit.remove(PREF_MODEL_SOURCE_STATIC_VERSION);
		}
		edit.apply();
	}

	/**
	 * Clear model source info
	 *
	 * @param context context
	 */
	static public void unrecordModelSource(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_MODEL, Context.MODE_PRIVATE);
		sharedPref.edit() //
				.remove(Settings.PREF_MODEL_SOURCE) //
				.remove(Settings.PREF_MODEL_SOURCE_DATE) //
				.remove(Settings.PREF_MODEL_SOURCE_SIZE) //
				.remove(Settings.PREF_MODEL_SOURCE_ETAG) //
				.remove(Settings.PREF_MODEL_SOURCE_VERSION) //
				.remove(Settings.PREF_MODEL_SOURCE_STATIC_VERSION) //
				.apply();
	}
}
