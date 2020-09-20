/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.fn;

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

	static private final String PREF_ENABLE_FRAMENET = "pref_enable_framenet";


	// D A T A

	/**
	 * Source
	 */
	public enum Source
	{FRAMENET(0x40);

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
}
