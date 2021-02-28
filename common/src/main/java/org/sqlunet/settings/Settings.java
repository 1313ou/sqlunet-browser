/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import org.sqlunet.browser.common.R;
import org.sqlunet.browser.config.SetupAsset;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.sql.PreparedStatement;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

/**
 * Settings
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("WeakerAccess")
public class Settings
{
	// preferences

	static public final String PREF_LAUNCH = "pref_launch";
	static public final String PREF_SELECTOR = "pref_selector_mode"; // selector or xselector or ...
	static public final String PREF_SELECTOR_MODE = "pref_selector_view_mode"; // view or web for selector fragment
	static public final String PREF_DETAIL_MODE = "pref_detail_view_mode"; // view wor web for detail fragment
	static public final String PREF_XML = "pref_xml";
	static public final String PREF_TEXTSEARCH_MODE = "pref_searchtext_mode";
	static public final String PREF_ASSET_PRIMARY_DEFAULT = SetupAsset.PREF_ASSET_PRIMARY_DEFAULT;
	static public final String PREF_ASSET_AUTO_CLEANUP = SetupAsset.PREF_ASSET_AUTO_CLEANUP;
	static public final String PREF_DB_FILE = "pref_db_file";
	static public final String PREF_STORAGE = StorageSettings.PREF_STORAGE;
	static public final String PREF_DOWNLOADER = StorageSettings.PREF_DOWNLOADER;
	static public final String PREF_DOWNLOAD_SITE = StorageSettings.PREF_DOWNLOAD_SITE;
	static public final String PREF_DOWNLOAD_DBFILE = StorageSettings.PREF_DOWNLOAD_DBFILE;
	static public final String PREF_CACHE = StorageSettings.PREF_CACHE;
	static public final String PREF_DB_DATE = "pref_db_date";
	static public final String PREF_DB_SIZE = "pref_db_size";
	static public final String PREF_TWO_PANES = "pref_two_panes";
	static private final String PREF_VERSION = "org.sqlunet.browser.version";

	// D I S P L A Y

	/**
	 * Selector view modes
	 */
	public enum SelectorViewMode
	{
		VIEW, WEB;

		/**
		 * Get selector preferred view mode
		 *
		 * @param context context
		 * @return preferred selector view mode
		 */
		static SelectorViewMode getPref(@NonNull final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			final String mode_string = sharedPref.getString(Settings.PREF_SELECTOR_MODE, SelectorViewMode.VIEW.name());
			SelectorViewMode mode;
			try
			{
				mode = SelectorViewMode.valueOf(mode_string);
			}
			catch (@NonNull final Exception e)
			{
				mode = SelectorViewMode.VIEW;
				sharedPref.edit().putString(Settings.PREF_SELECTOR_MODE, mode.name()).apply();
			}
			return mode;
		}
	}

	/**
	 * Detail detail view modes
	 */
	public enum DetailViewMode
	{
		VIEW, WEB;

		/**
		 * Get preferred view mode
		 *
		 * @param context context
		 * @return preferred selector mode
		 */
		static DetailViewMode getPref(@NonNull final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			final String mode_string = sharedPref.getString(Settings.PREF_DETAIL_MODE, DetailViewMode.VIEW.name());
			DetailViewMode mode;
			try
			{
				mode = DetailViewMode.valueOf(mode_string);
			}
			catch (@NonNull final Exception e)
			{
				mode = DetailViewMode.VIEW;
				sharedPref.edit().putString(Settings.PREF_DETAIL_MODE, mode.name()).apply();
			}
			return mode;
		}
	}

	// P A N E  L A Y O U T   H O O K

	public static int paneMode = 0;

	@LayoutRes
	public static int getPaneLayout(@LayoutRes int defaultPaneLayout, @LayoutRes int onePaneLayout, @LayoutRes int twoPanesLayout)
	{
		switch (paneMode)
		{
			default:
			case 0:
				return defaultPaneLayout;
			case 1:
				return onePaneLayout;
			case 2:
				return twoPanesLayout;
		}
	}

	// S E L E C T O R   T Y P E

	/**
	 * Selectors
	 */
	public enum Selector
	{
		SELECTOR;

		/**
		 * Set this selector as preferred selector
		 *
		 * @param context context
		 */
		public void setPref(@NonNull final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			sharedPref.edit().putString(Settings.PREF_SELECTOR, this.name()).apply();
		}

		/**
		 * Get preferred mode
		 *
		 * @param context context
		 * @return preferred selector mode
		 */
		public static Selector getPref(@NonNull final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			final String name = sharedPref.getString(Settings.PREF_SELECTOR, Settings.Selector.SELECTOR.name());
			Settings.Selector mode;
			try
			{
				mode = Settings.Selector.valueOf(name);
			}
			catch (@NonNull final Exception e)
			{
				mode = Settings.Selector.SELECTOR;
				sharedPref.edit().putString(Settings.PREF_SELECTOR, mode.name()).apply();
			}
			return mode;
		}
	}

	// P R E F E R E N C E   S H O R T C U T S

	/**
	 * Get launch activity class
	 *
	 * @param context context
	 * @return preferred launch activity class
	 */
	@NonNull
	static public String getLaunchPref(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String result = sharedPref.getString(Settings.PREF_LAUNCH, null);
		if (result == null)
		{
			result = context.getString(R.string.pref_default_launch);
		}
		return result;
	}

	/**
	 * Get selector preferred view mode
	 *
	 * @param context context
	 * @return preferred selector view mode
	 */
	static public SelectorViewMode getSelectorViewModePref(@NonNull final Context context)
	{
		return SelectorViewMode.getPref(context);
	}

	/**
	 * Get detail preferred view mode
	 *
	 * @param context context
	 * @return preferred detail view mode
	 */
	static public DetailViewMode getDetailViewModePref(@NonNull final Context context)
	{
		return DetailViewMode.getPref(context);
	}

	/**
	 * Get preferred selector type
	 *
	 * @param context context
	 * @return preferred selector type
	 */
	static public Selector getSelectorPref(@NonNull final Context context)
	{
		return Selector.getPref(context);
	}

	/**
	 * Get preferred XML output flag when view mode is WEB
	 *
	 * @param context context
	 * @return preferred XML output flag when view mode is WEB
	 */
	static public boolean getXmlPref(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(Settings.PREF_XML, true);
	}

	/**
	 * Get preferred search mode
	 *
	 * @param context context
	 * @return preferred search mode
	 */
	static public int getSearchModePref(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getInt(Settings.PREF_TEXTSEARCH_MODE, 0);
	}

	/**
	 * Set preferred search mode
	 *
	 * @param context context
	 * @param value   preferred search mode
	 */
	static public void setSearchModePref(@NonNull final Context context, final int value)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPref.edit().putInt(Settings.PREF_TEXTSEARCH_MODE, value).apply();
	}

	/**
	 * Get preferred asset pack
	 *
	 * @param context context
	 * @return asset pack preference
	 */
	@NonNull
	public static String getAssetPack(@NonNull final Context context)
	{
		final String primary = context.getString(R.string.asset_primary);
		final String alt = context.getString(R.string.asset_alt);
		if (alt.isEmpty())
		{
			return primary;
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		final boolean isPrimaryDefault = prefs.getBoolean(PREF_ASSET_PRIMARY_DEFAULT, true);
		return isPrimaryDefault ? primary : alt;
	}

	/**
	 * Get preferred asset pack dir
	 *
	 * @param context context
	 * @return asset pack dir preference
	 */
	@NonNull
	public static String getAssetPackDir(@NonNull final Context context)
	{
		final String primary = context.getString(R.string.asset_dir_primary);
		final String alt = context.getString(R.string.asset_dir_alt);
		if (alt.isEmpty())
		{
			return primary;
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean isPrimaryDefault = prefs.getBoolean(PREF_ASSET_PRIMARY_DEFAULT, true);
		return isPrimaryDefault ? primary : alt;
	}

	/**
	 * Get preferred asset pack zip
	 *
	 * @param context context
	 * @return asset pack zip preference
	 */
	@NonNull
	public static String getAssetPackZip(@NonNull final Context context)
	{
		final String primary = context.getString(R.string.asset_zip_primary);
		final String alt = context.getString(R.string.asset_zip_alt);
		if (alt.isEmpty())
		{
			return primary;
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean isPrimaryDefault = prefs.getBoolean(PREF_ASSET_PRIMARY_DEFAULT, true);
		return isPrimaryDefault ? primary : alt;
	}

	/**
	 * Get preferred asset pack auto cleanup flag
	 *
	 * @param context context
	 * @return asset asset pack auto cleanup preference
	 */
	public static boolean getAssetAutoCleanup(@NonNull final Context context)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_ASSET_AUTO_CLEANUP, true);
	}

	/**
	 * Get preferred downloader
	 *
	 * @param context context
	 * @return preferred downloader
	 */
	@Nullable
	static public String getDownloaderPref(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getString(Settings.PREF_DOWNLOADER, null);
	}


	/**
	 * Get preferred cache
	 *
	 * @param context context
	 * @return preferred cache
	 */
	@Nullable
	static public String getCachePref(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getString(Settings.PREF_CACHE, null);
	}

	/**
	 * Get db date
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
	 * Set db date
	 *
	 * @param context   context
	 * @param timestamp timestamp
	 */
	static public void setDbDate(@NonNull final Context context, final long timestamp)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPref.edit().putLong(Settings.PREF_DB_DATE, timestamp).apply();
	}

	/**
	 * Get db size
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
	 * Set db size
	 *
	 * @param context context
	 * @param size    size
	 */
	static public void setDbSize(@NonNull final Context context, final long size)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPref.edit().putLong(Settings.PREF_DB_SIZE, size).apply();
	}

	/**
	 * Initialize display mode preferences
	 *
	 * @param context context
	 */
	@SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
	static public void initializeDisplayPrefs(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor = sharedPref.edit();

		final String viewwebMode_string = sharedPref.getString(Settings.PREF_SELECTOR_MODE, null);
		if (viewwebMode_string == null)
		{
			editor.putString(Settings.PREF_SELECTOR_MODE, SelectorViewMode.VIEW.name());
		}

		final String detailMode_string = sharedPref.getString(Settings.PREF_DETAIL_MODE, null);
		if (detailMode_string == null)
		{
			editor.putString(Settings.PREF_DETAIL_MODE, DetailViewMode.VIEW.name());
		}

		editor.commit();
	}

	/**
	 * Update globals
	 *
	 * @param context context
	 */
	static public void updateGlobals(@NonNull final Context context)
	{
		// globals
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

		final boolean logSql = sharedPref.getBoolean(BaseProvider.CircularBuffer.PREF_SQL_LOG, false);
		PreparedStatement.logSql = logSql;
		BaseProvider.logSql = logSql;

		final String sqlBufferCapacity = sharedPref.getString(BaseProvider.CircularBuffer.PREF_SQL_BUFFER_CAPACITY, null);
		if (sqlBufferCapacity != null)
		{
			try
			{
				int capacity = Integer.parseInt(sqlBufferCapacity);
				if (capacity >= 1 && capacity <= 64)
				{
					BaseProvider.resizeSql(capacity);
				}
				else
				{
					sharedPref.edit().remove(BaseProvider.CircularBuffer.PREF_SQL_BUFFER_CAPACITY).apply();
				}
			}
			catch (Exception e)
			{
				//
			}
		}

		final boolean twoPanes = sharedPref.getBoolean(Settings.PREF_TWO_PANES, false);
		Settings.paneMode = twoPanes ? 2 : 0;
	}

	/**
	 * Clear settings on upgrade
	 *
	 * @param context context
	 * @return build version
	 */
	@SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
	@SuppressWarnings({"UnusedReturnValue"})
	public static long clearSettingsOnUpgrade(@NonNull final Context context)
	{
		// recorded version
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		long version;
		try
		{
			version = prefs.getLong(Settings.PREF_VERSION, -1);
		}
		catch (ClassCastException e)
		{
			version = prefs.getInt(Settings.PREF_VERSION, -1);
		}

		// build version
		long build = 0; //BuildConfig.VERSION_CODE;
		try
		{
			final PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
			{
				build = packageInfo.getLongVersionCode();
			}
			else
			{
				build = packageInfo.versionCode;
			}
		}
		catch (PackageManager.NameNotFoundException ignored)
		{
			//
		}

		// upgrade test
		if (version < build)
		{
			prefs.edit() //
					// clear all settings
					// .clear()
					// clear some settings
					.remove(PREF_DOWNLOADER) //
					.remove(PREF_DOWNLOAD_SITE) //
					.remove(PREF_DOWNLOAD_DBFILE) //
					// flag as 'has run'
					.putLong(Settings.PREF_VERSION, build).apply();
		}
		return build;
	}

	/**
	 * Application settings
	 *
	 * @param context context
	 * @param pkgName package name
	 */
	static public void applicationSettings(@NonNull final Context context, final String pkgName)
	{
		final Intent intent = new Intent();

		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
		// {
		intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.parse("package:" + pkgName));
		// }
		// else
		// {
		//	final String appPkgName = Build.VERSION.SDK_INT == Build.VERSION_CODES.FROYO ? "pkg" : "com.android.settings.ApplicationPkgName";
		//	intent.setAction(Intent.ACTION_VIEW);
		//	intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
		//	intent.putExtra(appPkgName, pkgName);
		// }

		// start activity
		context.startActivity(intent);
	}
}
