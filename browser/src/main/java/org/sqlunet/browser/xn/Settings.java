package org.sqlunet.browser.xn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Settings
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Settings extends org.sqlunet.settings.Settings
{
	// preferences

	static private final String PREF_ENABLE_LINKS = "pref_enable_links";
	static private final String PREF_ENABLE_WORDNET = "pref_enable_wordnet";
	static private final String PREF_ENABLE_VERBNET = "pref_enable_verbnet";
	static private final String PREF_ENABLE_PROPBANK = "pref_enable_propbank";
	static private final String PREF_ENABLE_FRAMENET = "pref_enable_framenet";
	static private final String PREF_ENABLE_BNC = "pref_enable_bnc";

	static public final String PREF_ENTRY_PM = "pref_entry_pm";

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
			sharedPref.edit().putString(org.sqlunet.settings.Settings.PREF_SELECTOR, this.name()).apply();
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
			final String name = sharedPref.getString(org.sqlunet.settings.Settings.PREF_SELECTOR, Selector.XSELECTOR.name());
			Selector mode;
			try
			{
				mode = Selector.valueOf(name);
			}
			catch (final Exception e)
			{
				mode = Selector.XSELECTOR;
				sharedPref.edit().putString(org.sqlunet.settings.Settings.PREF_SELECTOR, mode.name()).apply();
			}
			return mode;
		}
	}

	// P R E F E R E N C E   S H O R T C U T S

	/**
	 * Get preferred selector type
	 *
	 * @param context context
	 * @return preferred selector type
	 */
	static public Selector getXSelectorPref(final Context context)
	{
		return Selector.getPref(context);
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
	 * Get pm archive entry
	 *
	 * @param context context
	 * @return pm archive entry
	 */
	static public String getPmEntry(final Context context)
	{
		// test if already in preferences
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String value = sharedPref.getString(Settings.PREF_ENTRY_PM, null);
		if (value != null)
		{
			return value;
		}

		// set to default value
		value = context.getResources().getString(org.sqlunet.xnet.R.string.pref_default_entry_pm);

		// store value in preferences
		sharedPref.edit().putString(Settings.PREF_ENTRY_PM, value).apply();

		return value;
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
	 * Initialize preferences
	 *
	 * @param context context
	 */
	@SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
	static public void initialize(final Context context)
	{
		org.sqlunet.settings.Settings.initialize(context);

		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		final SharedPreferences.Editor editor = sharedPref.edit();

		final String selector_string = sharedPref.getString(org.sqlunet.settings.Settings.PREF_SELECTOR, null);
		if (selector_string == null)
		{
			editor.putString(org.sqlunet.settings.Settings.PREF_SELECTOR, Settings.Selector.XSELECTOR.name());
		}

		editor.commit();
	}

}
