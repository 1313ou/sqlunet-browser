package org.sqlunet.browser.vn;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * Settings
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Settings extends org.sqlunet.settings.Settings
{
	// preferences

	static private final String PREF_ENABLE_WORDNET = "pref_enable_wordnet";
	static private final String PREF_ENABLE_VERBNET = "pref_enable_verbnet";
	static private final String PREF_ENABLE_PROPBANK = "pref_enable_propbank";

	static public final int ENABLE_WORDNET = 0x1;
	static public final int ENABLE_VERBNET = 0x10;
	static public final int ENABLE_PROPBANK = 0x20;

	// D A T A

	/**
	 * Source
	 */
	public enum Source
	{
		WORDNET(ENABLE_WORDNET), VERBNET(ENABLE_VERBNET), PROPBANK(ENABLE_PROPBANK);

		/**
		 * Source mask
		 */
		final private int mask;

		/**
		 * Constructor
		 *
		 * @param mask mask
		 */
		Source(@SuppressWarnings("SameParameterValue") final int mask)
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
		XSELECTOR;

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
			final String name = sharedPref.getString(Settings.PREF_SELECTOR, Selector.XSELECTOR.name());
			Settings.Selector mode;
			try
			{
				mode = Settings.Selector.valueOf(name);
			}
			catch (@NonNull final Exception e)
			{
				mode = Selector.XSELECTOR;
				sharedPref.edit().putString(Settings.PREF_SELECTOR, mode.name()).apply();
			}
			return mode;
		}
	}

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

	// P R E F E R E N C E S H O R T C U T S

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
		if (sharedPref.getBoolean(Settings.PREF_ENABLE_VERBNET, true))
		{
			result |= ENABLE_VERBNET;
		}
		if (sharedPref.getBoolean(Settings.PREF_ENABLE_PROPBANK, true))
		{
			result |= ENABLE_PROPBANK;
		}
		if (sharedPref.getBoolean(Settings.PREF_ENABLE_WORDNET, true))
		{
			result |= ENABLE_WORDNET;
		}
		return result;
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
}
