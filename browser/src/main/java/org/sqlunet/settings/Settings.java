package org.sqlunet.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

/**
 * Settings
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Settings
{
	// preferences

	static public final String PREF_SELECTOR_MODE = "pref_viewweb_mode"; //$NON-NLS-1$
	static private final String PREF_SELECTOR = "pref_selector_mode"; //$NON-NLS-1$
	static public final String PREF_DETAIL_MODE = "pref_detail_mode"; //$NON-NLS-1$
	static private final String PREF_XML = "pref_xml"; //$NON-NLS-1$
	static private final String PREF_ENABLE_LINKS = "pref_enable_links"; //$NON-NLS-1$
	static private final String PREF_ENABLE_WORDNET = "pref_enable_wordnet"; //$NON-NLS-1$
	static private final String PREF_ENABLE_VERBNET = "pref_enable_verbnet"; //$NON-NLS-1$
	static private final String PREF_ENABLE_PROPBANK = "pref_enable_propbank"; //$NON-NLS-1$
	static private final String PREF_ENABLE_FRAMENET = "pref_enable_framenet"; //$NON-NLS-1$
	static private final String PREF_ENABLE_BNC = "pref_enable_bnc"; //$NON-NLS-1$
	static private final String PREF_SEARCH_MODE = "pref_search_mode"; //$NON-NLS-1$
	static public final String PREF_STORAGE = StorageSettings.PREF_STORAGE;
	static public final String PREF_DOWNLOAD_SITE = StorageSettings.PREF_DOWNLOAD_SITE;
	static public final String PREF_DOWNLOAD_DBFILE = StorageSettings.PREF_DOWNLOAD_DBFILE;
	static public final String PREF_DOWNLOAD_SQLFILE = StorageSettings.PREF_DOWNLOAD_SQLFILE;
	static public final String PREF_ENTRY_IMPORT = StorageSettings.PREF_ENTRY_IMPORT;
	static public final String PREF_ENTRY_PM = StorageSettings.PREF_ENTRY_PM;
	static public final String PREF_ENTRY_INDEX = StorageSettings.PREF_ENTRY_INDEX;

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
		 * @return result
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
		public static Selector getPref(final Context context)
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
	static public String getSearchModePref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getString(Settings.PREF_SEARCH_MODE, null);
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
	}

	/**
	 * Application settings
	 *
	 * @param context context
	 * @param pkgName package name
	 */
	static public void applicationSettings(final Context context, @SuppressWarnings("SameParameterValue") final String pkgName)
	{
		final int apiLevel = Build.VERSION.SDK_INT;
		final Intent intent = new Intent();

		if (apiLevel >= 9)
		{
			intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:" + pkgName)); //$NON-NLS-1$
		}
		else
		{
			final String appPkgName = apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName"; //$NON-NLS-1$ //$NON-NLS-2$

			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails"); //$NON-NLS-1$ //$NON-NLS-2$
			intent.putExtra(appPkgName, pkgName);
		}

		// start activity
		context.startActivity(intent);
	}
}
