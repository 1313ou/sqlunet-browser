/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

/**
 * This fragment shows download preferences only.
 */
public class DownloadPreferenceFragment extends PreferenceFragmentCompat
{
	@NonNull
	private static String unrecorded = "";

	private static final Preference.SummaryProvider<Preference> STRING_SUMMARY_PROVIDER = (preference) -> PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), unrecorded);

	private static final Preference.SummaryProvider<Preference> DEVICE_STRING_SUMMARY_PROVIDER = (preference) -> preference.getContext().getSharedPreferences(Settings.PREFERENCES_DEVICE, Context.MODE_PRIVATE).getString(preference.getKey(), unrecorded);

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
	{
		unrecorded = getString(R.string.pref_value_unrecorded);

		addPreferencesFromResource(R.xml.pref_download);

		final Preference repoPreference = findPreference(Settings.PREF_REPO);
		assert repoPreference != null;
		repoPreference.setSummaryProvider(STRING_SUMMARY_PROVIDER);

		final Preference downloadModePreference = findPreference(Settings.PREF_DOWNLOAD_MODE);
		assert downloadModePreference != null;
		downloadModePreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());

		final Preference cachePreference = findPreference(Settings.PREF_CACHE);
		assert cachePreference != null;
		cachePreference.setSummaryProvider(DEVICE_STRING_SUMMARY_PROVIDER);

		final Preference datapackDirPreference = findPreference(Settings.PREF_DATAPACK_DIR);
		assert datapackDirPreference != null;
		datapackDirPreference.setSummaryProvider(DEVICE_STRING_SUMMARY_PROVIDER);
	}
}
