/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.download;

import android.content.SharedPreferences;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

/**
 * This fragment shows download preferences only. It is used when the activity is showing a two-pane settings UI.
 */
public class DownloadedPreferenceFragment extends PreferenceFragmentCompat
{
	public DownloadedPreferenceFragment()
	{
		setHasOptionsMenu(true);
	}

	@NonNull
	private static String unrecorded = "";

	private static final Preference.SummaryProvider<Preference> STRING_SUMMARY_PROVIDER = (preference) -> PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), unrecorded);

	private static final Preference.SummaryProvider<Preference> LONG_SUMMARY_PROVIDER = (preference) -> {
		long value = PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getLong(preference.getKey(), -1);
		return value == -1 ? unrecorded : Long.toString(value);
	};

	private static final Preference.SummaryProvider<Preference> DATE_SUMMARY_PROVIDER = (preference) -> {
		long value = PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getLong(preference.getKey(), -1);
		return value == -1 ? unrecorded : new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(new Date(value));
	};

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
	{
		unrecorded = getString(R.string.pref_value_unrecorded);

		addPreferencesFromResource(R.xml.pref_downloaded);

		// Bind the summaries of preferences to their values. When their values change, their summaries are updated to reflect the new value, per the Android Design guidelines.
		final Preference namePreference = findPreference(Settings.PREF_DB_NAME);
		assert namePreference != null;
		namePreference.setSummary(STRING_SUMMARY_PROVIDER.provideSummary(namePreference));

		final Preference datePreference = findPreference(Settings.PREF_DB_DATE);
		assert datePreference != null;
		datePreference.setSummary(DATE_SUMMARY_PROVIDER.provideSummary(datePreference));

		final Preference sizePreference = findPreference(Settings.PREF_DB_SIZE);
		assert sizePreference != null;
		sizePreference.setSummary(LONG_SUMMARY_PROVIDER.provideSummary(sizePreference));

		final Preference sourcePreference = findPreference(Settings.PREF_DB_SOURCE);
		assert sourcePreference != null;
		sourcePreference.setSummary(STRING_SUMMARY_PROVIDER.provideSummary(sourcePreference));

		final Preference sourceDatePreference = findPreference(Settings.PREF_DB_SOURCE_DATE);
		assert sourceDatePreference != null;
		sourceDatePreference.setSummary(DATE_SUMMARY_PROVIDER.provideSummary(sourceDatePreference));

		final Preference sourceSizePreference = findPreference(Settings.PREF_DB_SOURCE_SIZE);
		assert sourceSizePreference != null;
		sourceSizePreference.setSummary(LONG_SUMMARY_PROVIDER.provideSummary(sourceSizePreference));

		final Preference sourceEtagPreference = findPreference(Settings.PREF_DB_SOURCE_ETAG);
		assert sourceEtagPreference != null;
		sourceEtagPreference.setSummary(STRING_SUMMARY_PROVIDER.provideSummary(sourceEtagPreference));

		final Preference sourceVersionPreference = findPreference(Settings.PREF_DB_SOURCE_VERSION);
		assert sourceVersionPreference != null;
		sourceVersionPreference.setSummary(STRING_SUMMARY_PROVIDER.provideSummary(sourceVersionPreference));

		final Preference sourceStaticVersionPreference = findPreference(Settings.PREF_DB_SOURCE_STATIC_VERSION);
		assert sourceStaticVersionPreference != null;
		sourceStaticVersionPreference.setSummary(STRING_SUMMARY_PROVIDER.provideSummary(sourceStaticVersionPreference));

		// unset button
		final Preference unsetButton = findPreference(Settings.PREF_DB_CLEAR_BUTTON);
		assert unsetButton != null;
		unsetButton.setOnPreferenceClickListener(pref -> {

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(pref.getContext());
			prefs.edit() //
					.remove(Settings.PREF_DB_NAME) //
					.remove(Settings.PREF_DB_DATE) //
					.remove(Settings.PREF_DB_SIZE) //
					.remove(Settings.PREF_DB_SOURCE) //
					.remove(Settings.PREF_DB_SOURCE_DATE) //
					.remove(Settings.PREF_DB_SOURCE_SIZE) //
					.remove(Settings.PREF_DB_SOURCE_ETAG) //
					.remove(Settings.PREF_DB_SOURCE_VERSION) //
					.remove(Settings.PREF_DB_SOURCE_STATIC_VERSION) //
					.apply();

			namePreference.setSummary(STRING_SUMMARY_PROVIDER.provideSummary(namePreference));
			datePreference.setSummary(DATE_SUMMARY_PROVIDER.provideSummary(datePreference));
			sizePreference.setSummary(LONG_SUMMARY_PROVIDER.provideSummary(sizePreference));
			sourcePreference.setSummary(STRING_SUMMARY_PROVIDER.provideSummary(sourcePreference));
			sourceDatePreference.setSummary(DATE_SUMMARY_PROVIDER.provideSummary(sourceDatePreference));
			sourceSizePreference.setSummary(LONG_SUMMARY_PROVIDER.provideSummary(sourceSizePreference));
			sourceEtagPreference.setSummary(STRING_SUMMARY_PROVIDER.provideSummary(sourceEtagPreference));
			sourceVersionPreference.setSummary(STRING_SUMMARY_PROVIDER.provideSummary(sourceVersionPreference));
			sourceStaticVersionPreference.setSummary(STRING_SUMMARY_PROVIDER.provideSummary(sourceStaticVersionPreference));
			return true;
		});
	}
}
