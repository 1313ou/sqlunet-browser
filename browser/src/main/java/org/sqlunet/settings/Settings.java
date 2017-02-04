package org.sqlunet.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

import org.sqlunet.provider.BaseProvider;
import org.sqlunet.sql.PreparedStatement;

/**
 * Settings
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Settings
{
	// preferences

	static public final String PREF_SELECTOR_MODE = "pref_viewweb_mode";
	static private final String PREF_SELECTOR = "pref_selector_mode";
	static public final String PREF_DETAIL_MODE = "pref_detail_mode";
	static private final String PREF_XML = "pref_xml";
	static private final String PREF_SQL_LOG = "pref_sql_log";
	static private final String PREF_ENABLE_LINKS = "pref_enable_links";
	static private final String PREF_ENABLE_WORDNET = "pref_enable_wordnet";
	static private final String PREF_ENABLE_VERBNET = "pref_enable_verbnet";
	static private final String PREF_ENABLE_PROPBANK = "pref_enable_propbank";
	static private final String PREF_ENABLE_FRAMENET = "pref_enable_framenet";
	static private final String PREF_ENABLE_BNC = "pref_enable_bnc";
	static private final String PREF_TEXTSEARCH_MODE = "pref_textsearch_mode";
	static public final String PREF_STORAGE = StorageSettings.PREF_STORAGE;
	static public final String PREF_DOWNLOAD_SITE = StorageSettings.PREF_DOWNLOAD_SITE;
	static public final String PREF_DOWNLOADER = StorageSettings.PREF_DOWNLOADER;
	static public final String PREF_DOWNLOAD_DBFILE = StorageSettings.PREF_DOWNLOAD_DBFILE;
	static public final String PREF_DOWNLOAD_SQLFILE = StorageSettings.PREF_DOWNLOAD_SQLFILE;
	static public final String PREF_ENTRY_IMPORT = StorageSettings.PREF_ENTRY_IMPORT;
	static public final String PREF_ENTRY_PM = StorageSettings.PREF_ENTRY_PM;
	static public final String PREF_ENTRY_INDEX = StorageSettings.PREF_ENTRY_INDEX;
	static public final String PREF_CACHE = StorageSettings.PREF_CACHE;

	static public final int ENABLE_WORDNET = 0x1;
	static public final int ENABLE_VERBNET = 0x10;
	static public final int ENABLE_PROPBANK = 0x20;
	static public final int ENABLE_FRAMENET = 0x40;
	static public final int ENABLE_BNC = 0x100;

	// D A T A

	/**
	 * Source
	 */
	public enum Source
	{
		WORDNET(0x1), BNC(0x2), VERBNET(0x10), PROPBANK(0x20), FRAMENET(0x40);

		/**
		 * Source mask
		 */
		final private int mask;

		/**
		 * Constructor
		 *
		 * @param mask mask
		 */
		Source(final int mask)
		{
			this.mask = mask;
		}

		/**
		 * Set this source in sources
		 *
		 * @param sources sources to set
		 * @return progressMessage
		 */
		public int set(final int sources)
		{
			return sources | this.mask;
		}

		/**
		 * Test
		 *
		 * @param sources sources to test
		 * @return true if this source is set
		 */
		public boolean test(final int sources)
		{
			return (sources & this.mask) != 0;
		}
	}

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
		static SelectorViewMode getPref(final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			final String mode_string = sharedPref.getString(Settings.PREF_SELECTOR_MODE, SelectorViewMode.VIEW.name());
			SelectorViewMode mode;
			try
			{
				mode = SelectorViewMode.valueOf(mode_string);
			}
			catch (final Exception e)
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
		static DetailViewMode getPref(final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			final String mode_string = sharedPref.getString(Settings.PREF_DETAIL_MODE, DetailViewMode.VIEW.name());
			DetailViewMode mode;
			try
			{
				mode = DetailViewMode.valueOf(mode_string);
			}
			catch (final Exception e)
			{
				mode = DetailViewMode.VIEW;
				sharedPref.edit().putString(Settings.PREF_DETAIL_MODE, mode.name()).apply();
			}
			return mode;
		}
	}

	// S E L E C T O R   T Y P E

	/**
	 * Selectors
	 */
	public enum Selector
	{
		SELECTOR, XSELECTOR;

		/**
		 * Set this selector as preferred selector
		 *
		 * @param context context
		 */
		public void setPref(final Context context)
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
		static public Selector getPref(final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			final String name = sharedPref.getString(Settings.PREF_SELECTOR, Settings.Selector.XSELECTOR.name());
			Settings.Selector mode;
			try
			{
				mode = Settings.Selector.valueOf(name);
			}
			catch (final Exception e)
			{
				mode = Settings.Selector.XSELECTOR;
				sharedPref.edit().putString(Settings.PREF_SELECTOR, mode.name()).apply();
			}
			return mode;
		}
	}

	// P R E F E R E N C E S H O R T C U T S

	/**
	 * Get selector preferred view mode
	 *
	 * @param context context
	 * @return preferred selector view mode
	 */
	static public SelectorViewMode getSelectorViewModePref(final Context context)
	{
		return SelectorViewMode.getPref(context);
	}

	/**
	 * Get detail preferred view mode
	 *
	 * @param context context
	 * @return preferred detail view mode
	 */
	static public DetailViewMode getDetailViewModePref(final Context context)
	{
		return DetailViewMode.getPref(context);
	}

	/**
	 * Get preferred selector type
	 *
	 * @param context context
	 * @return preferred selector type
	 */
	static public Selector getSelectorPref(final Context context)
	{
		return Selector.getPref(context);
	}

	/**
	 * Get preferred recurse flag
	 *
	 * @param context context
	 * @return preferred recurse flag
	 */
	static public boolean getRecursePref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(Settings.PREF_ENABLE_LINKS, false);
	}

	/**
	 * Get preferred enable aggregated flag
	 *
	 * @param context context
	 * @return preferred enable WordNet flag
	 */
	static public int getAllPref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		int result = 0;
		if (sharedPref.getBoolean(Settings.PREF_ENABLE_WORDNET, true))
		{
			result |= ENABLE_WORDNET;
		}
		if (sharedPref.getBoolean(Settings.PREF_ENABLE_VERBNET, true))
		{
			result |= ENABLE_VERBNET;
		}
		if (sharedPref.getBoolean(Settings.PREF_ENABLE_PROPBANK, true))
		{
			result |= ENABLE_PROPBANK;
		}
		if (sharedPref.getBoolean(Settings.PREF_ENABLE_FRAMENET, true))
		{
			result |= ENABLE_FRAMENET;
		}
		if (sharedPref.getBoolean(Settings.PREF_ENABLE_BNC, true))
		{
			result |= ENABLE_BNC;
		}
		return result;
	}

	/**
	 * Get preferred enable WordNet flag
	 *
	 * @param context context
	 * @return preferred enable WordNet flag
	 */
	static public boolean getWordNetPref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(Settings.PREF_ENABLE_WORDNET, true);
	}

	/**
	 * Get preferred enable VerbNet flag
	 *
	 * @param context context
	 * @return preferred enable VerbNet flag
	 */
	static public boolean getVerbNetPref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(Settings.PREF_ENABLE_VERBNET, true);
	}

	/**
	 * Get preferred enable PropBank flag
	 *
	 * @param context context
	 * @return preferred enable PropBank flag
	 */
	static public boolean getPropBankPref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(Settings.PREF_ENABLE_PROPBANK, true);
	}

	/**
	 * Get preferred enable FrameNet flag
	 *
	 * @param context context
	 * @return preferred enable FrameNet flag
	 */
	static public boolean getFrameNetPref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(Settings.PREF_ENABLE_FRAMENET, true);
	}

	/**
	 * Get preferred enable BNC flag
	 *
	 * @param context context
	 * @return preferred enable BNC flag
	 */
	static public boolean getBncPref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(Settings.PREF_ENABLE_BNC, true);
	}

	/**
	 * Get preferred XML output flag when view mode is WEB
	 *
	 * @param context context
	 * @return preferred XML output flag when view mode is WEB
	 */
	static public boolean getXmlPref(final Context context)
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
	static public int getSearchModePref(final Context context)
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
	static public void setSearchModePref(final Context context, final int value)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPref.edit().putInt(Settings.PREF_TEXTSEARCH_MODE, value).apply();
	}

	/**
	 * Get preferred XML output flag when view mode is WEB
	 *
	 * @param context context
	 * @return preferred XML output flag when view mode is WEB
	 */
	@SuppressWarnings("unused")
	static public boolean getSqlLogPref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(Settings.PREF_SQL_LOG, false);
	}

	/**
	 * Get preferred downloader
	 *
	 * @param context context
	 * @return preferred downloader
	 */
	static public String getDownloaderPref(final Context context)
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
	static public String getCachePref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getString(Settings.PREF_CACHE, null);
	}

	/**
	 * Initialize preferences
	 *
	 * @param context context
	 */
	@SuppressLint("CommitPrefEdits")
	static public void initialize(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor = sharedPref.edit();

		final String selector_string = sharedPref.getString(Settings.PREF_SELECTOR, null);
		if (selector_string == null)
		{
			editor.putString(Settings.PREF_SELECTOR, Settings.Selector.XSELECTOR.name());
		}

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

		// globals

		final boolean logSql = sharedPref.getBoolean(Settings.PREF_SQL_LOG, false);
		PreparedStatement.logSql = logSql;
		BaseProvider.logSql = logSql;
	}

	/**
	 * Application settings
	 *
	 * @param context context
	 * @param pkgName package name
	 */
	static public void applicationSettings(final Context context, final String pkgName)
	{
		final int apiLevel = Build.VERSION.SDK_INT;
		final Intent intent = new Intent();

		if (apiLevel >= 9)
		{
			intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:" + pkgName));
		}
		else
		{
			final String appPkgName = apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName";
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
			intent.putExtra(appPkgName, pkgName);
		}

		// start activity
		context.startActivity(intent);
	}
}
