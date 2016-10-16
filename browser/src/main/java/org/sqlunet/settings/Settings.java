package org.sqlunet.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

public class Settings
{
	static public final String PREF_SELECTOR_MODE = "pref_viewweb_mode"; //$NON-NLS-1$
	private static final String PREF_SELECTOR = "pref_selector_mode"; //$NON-NLS-1$
	static public final String PREF_DETAIL_MODE = "pref_detail_mode"; //$NON-NLS-1$
	private static final String PREF_XML = "pref_xml"; //$NON-NLS-1$
	private static final String PREF_ENABLE_LINKS = "pref_enable_links"; //$NON-NLS-1$
	private static final String PREF_ENABLE_WORDNET = "pref_enable_wordnet"; //$NON-NLS-1$
	private static final String PREF_ENABLE_VERBNET = "pref_enable_verbnet"; //$NON-NLS-1$
	private static final String PREF_ENABLE_PROPBANK = "pref_enable_propbank"; //$NON-NLS-1$
	private static final String PREF_ENABLE_FRAMENET = "pref_enable_framenet"; //$NON-NLS-1$
	private static final String PREF_ENABLE_BNC = "pref_enable_bnc"; //$NON-NLS-1$
	private static final String PREF_SEARCH_MODE = "pref_search_mode"; //$NON-NLS-1$
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

		final private int mask;

		Source(final int mask)
		{
			this.mask = mask;
		}

		public int set(final int sources)
		{
			return sources | this.mask;
		}

		public boolean test(final int sources)
		{
			return (sources & this.mask) != 0;
		}
	}

	// D I S P L A Y

	/**
	 * Modes
	 */
	public enum SelectorMode
	{
		VIEW, WEB;

		static SelectorMode getPref(final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			final String mode_string = sharedPref.getString(Settings.PREF_SELECTOR_MODE, Settings.SelectorMode.VIEW.name());
			Settings.SelectorMode mode;
			try
			{
				mode = Settings.SelectorMode.valueOf(mode_string);
			}
			catch (final Exception e)
			{
				mode = Settings.SelectorMode.VIEW;
				sharedPref.edit().putString(Settings.PREF_SELECTOR_MODE, mode.name()).apply();
			}
			return mode;
		}
	}

	public enum DetailMode
	{
		VIEW, WEB;

		static DetailMode getPref(final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			final String mode_string = sharedPref.getString(Settings.PREF_DETAIL_MODE, Settings.DetailMode.VIEW.name());
			Settings.DetailMode mode;
			try
			{
				mode = Settings.DetailMode.valueOf(mode_string);
			}
			catch (final Exception e)
			{
				mode = Settings.DetailMode.VIEW;
				sharedPref.edit().putString(Settings.PREF_DETAIL_MODE, mode.name()).apply();
			}
			return mode;
		}
	}

	public enum Selector
	{
		SELECTOR, XSELECTOR;

		public void setPref(final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			sharedPref.edit().putString(Settings.PREF_SELECTOR, this.name()).apply();
		}

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

	static public SelectorMode getSelectorModePref(final Context context)
	{
		return SelectorMode.getPref(context);
	}

	static public DetailMode getDetailModePref(final Context context)
	{
		return DetailMode.getPref(context);
	}

	static public Selector getXnModePref(final Context context)
	{
		return Selector.getPref(context);
	}

	static public boolean getRecursePref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(Settings.PREF_ENABLE_LINKS, false);
	}

	static public boolean getWordNetPref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(Settings.PREF_ENABLE_WORDNET, true);
	}

	static public boolean getVerbNetPref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(Settings.PREF_ENABLE_VERBNET, true);
	}

	static public boolean getPropBankPref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(Settings.PREF_ENABLE_PROPBANK, true);
	}

	static public boolean getFrameNetPref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(Settings.PREF_ENABLE_FRAMENET, true);
	}

	static public boolean getBncPref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(Settings.PREF_ENABLE_BNC, true);
	}

	static public boolean getXmlPref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(Settings.PREF_XML, true);
	}

	static public String getSearchModePref(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getString(Settings.PREF_SEARCH_MODE, null);
	}

	static public void initialize(final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor = sharedPref.edit();
		
		final String selector_string = sharedPref.getString(Settings.PREF_SELECTOR, null);
		if (selector_string == null)
			editor.putString(Settings.PREF_SELECTOR, Settings.Selector.XSELECTOR.name());

		final String viewwebMode_string = sharedPref.getString(Settings.PREF_SELECTOR_MODE, null);
		if (viewwebMode_string == null)
			editor.putString(Settings.PREF_SELECTOR_MODE, Settings.SelectorMode.VIEW.name());

		final String detailMode_string = sharedPref.getString(Settings.PREF_DETAIL_MODE, null);
		if (detailMode_string == null)
			editor.putString(Settings.PREF_DETAIL_MODE, Settings.DetailMode.VIEW.name());
		
		editor.commit();
	}

	/**
	 * Application settings
	 *
	 * @param context
	 *            context
	 * @param pkgName
	 *            package name
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
