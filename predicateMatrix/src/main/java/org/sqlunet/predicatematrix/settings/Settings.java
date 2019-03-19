package org.sqlunet.predicatematrix.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

/**
 * Settings
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Settings
{
	static private final String PREF_PM_MODE = "pref_pm_mode";

	/**
	 * Modes
	 */
	public enum PMMode
	{
		ROLES, ROWS_GROUPED_BY_ROLE, ROWS_GROUPED_BY_SYNSET, ROWS;

		/**
		 * Get mode preference
		 *
		 * @param context context
		 * @return mode preference
		 */
		static public PMMode getPref(final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			final String mode_string = sharedPref.getString(Settings.PREF_PM_MODE, Settings.PMMode.ROLES.name());
			Settings.PMMode mode;
			try
			{
				mode = Settings.PMMode.valueOf(mode_string);
			}
			catch (@NonNull final Exception e)
			{
				mode = Settings.PMMode.ROLES;
				sharedPref.edit().putString(Settings.PREF_PM_MODE, mode.name()).apply();
			}
			return mode;
		}

		/**
		 * Set preferred mode to this mode
		 *
		 * @param context context
		 * @return true if value has changed
		 */
		public boolean setPref(final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			final String prev = sharedPref.getString(Settings.PREF_PM_MODE, null);
			if (this.name().equals(prev))
			{
				return false;
			}
			sharedPref.edit().putString(Settings.PREF_PM_MODE, this.name()).apply();
			return true;
		}
	}
}
