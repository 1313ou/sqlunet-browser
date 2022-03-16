/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.wn;

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
	static private final String PREF_ENABLE_BNC = "pref_enable_bnc";

	static public final int ENABLE_WORDNET = 0x1;
	static public final int ENABLE_BNC = 0x100;

	// D A T A

	/**
	 * Source
	 */
	public enum Source
	{WORDNET(0x1), BNC(0x2);

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
		}}

	// P R E F E R E N C E S H O R T C U T S

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
}
