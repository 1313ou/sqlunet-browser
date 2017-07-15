package org.sqlunet.browser.xn;

import android.os.Bundle;

/**
 * Download Preferences
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadPreferenceFragment extends org.sqlunet.browser.config.SettingsActivity.DownloadPreferenceFragment
{
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		org.sqlunet.browser.config.SettingsActivity.bind(findPreference(Settings.PREF_ENTRY_PM));
	}
}
