/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.xn;

import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;

/**
 * Download Preferences
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadPreferenceFragment extends org.sqlunet.browser.config.SettingsActivity.DownloadPreferenceFragment
{
	@Override
	public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey)
	{
		super.onCreatePreferences(savedInstanceState, rootKey);
		final Preference entryPmPreference = findPreference(Settings.PREF_ENTRY_PM);
		assert entryPmPreference != null;
		entryPmPreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
	}
}
