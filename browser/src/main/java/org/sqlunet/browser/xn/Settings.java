/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.xn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

/**
 * Settings
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Settings extends org.sqlunet.settings.Settings
{
	// preferences

	static private final String PREF_RELATION_RECURSE = "pref_relation_recurse";
	static private final String PREF_ENABLE_WORDNET = "pref_enable_wordnet";
	static private final String PREF_ENABLE_VERBNET = "pref_enable_verbnet";
	static private final String PREF_ENABLE_PROPBANK = "pref_enable_propbank";
	static private final String PREF_ENABLE_FRAMENET = "pref_enable_framenet";
	static private final String PREF_ENABLE_BNC = "pref_enable_bnc";

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
		public void setPref(@NonNull final Context context)
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
		static public Selector getPref(@NonNull final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			final String name = sharedPref.getString(org.sqlunet.settings.Settings.PREF_SELECTOR, Selector.XSELECTOR.name());
			Selector mode;
			try
			{
				mode = Selector.valueOf(name);
			}
			catch (@NonNull final Exception e)
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
	static public Selector getXSelectorPref(@NonNull final Context context)
	{
		return Selector.getPref(context);
	}

	/**
	 * Get preferred enable aggregated flag
	 *
	 * @param context context
	 * @return preferred enable WordNet flag
	 */
	static public int getAllPref(@NonNull final Context context)
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
	static public boolean getWordNetPref(@NonNull final Context context)
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
	static public boolean getVerbNetPref(@NonNull final Context context)
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
	static public boolean getPropBankPref(@NonNull final Context context)
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
	static public boolean getFrameNetPref(@NonNull final Context context)
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
	static public boolean getBncPref(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(Settings.PREF_ENABLE_BNC, true);
	}

	/**
	 * Get preferred recurse max level
	 *
	 * @param context context
	 * @return preferred recurse max level
	 */
	static public int getRecursePref(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		final String value = sharedPref.getString(Settings.PREF_RELATION_RECURSE, null);
		return value == null ? -1 : Integer.parseInt(value);
	}

	/**
	 * Initialize selector preferences
	 *
	 * @param context context
	 */
	@SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
	static public void initializeSelectorPrefs(@NonNull final Context context)
	{
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
