/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.content.Context;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

/**
 * This fragment shows download preferences only.
 */
public class DownloadedPreferenceFragment extends PreferenceFragmentCompat
{
	@NonNull
	private static String unrecorded = "";

	private static final Preference.SummaryProvider<Preference> DATAPACK_STRING_SUMMARY_PROVIDER = (preference) -> preference.getContext().getSharedPreferences(Settings.PREFERENCES_DATAPACK, Context.MODE_PRIVATE).getString(preference.getKey(), unrecorded);

	private static final Preference.SummaryProvider<Preference> DATAPACK_LONG_SUMMARY_PROVIDER = (preference) -> {
		long value = preference.getContext().getSharedPreferences(Settings.PREFERENCES_DATAPACK, Context.MODE_PRIVATE).getLong(preference.getKey(), -1);
		return value == -1 ? unrecorded : Long.toString(value);
	};

	private static final Preference.SummaryProvider<Preference> DATAPACK_DATE_SUMMARY_PROVIDER = (preference) -> {
		long value = preference.getContext().getSharedPreferences(Settings.PREFERENCES_DATAPACK, Context.MODE_PRIVATE).getLong(preference.getKey(), -1);
		return value == -1 ? unrecorded : new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(new Date(value));
	};

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
	{
		unrecorded = getString(R.string.pref_value_unrecorded);

		final PreferenceManager manager = this.getPreferenceManager();
		manager.setSharedPreferencesName(Settings.PREFERENCES_DATAPACK);
		manager.setSharedPreferencesMode(Context.MODE_PRIVATE);

		addPreferencesFromResource(R.xml.pref_downloaded);

		final Preference namePreference = findPreference(Settings.PREF_DATAPACK_NAME);
		assert namePreference != null;
		namePreference.setSummary(DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(namePreference));

		final Preference datePreference = findPreference(Settings.PREF_DATAPACK_DATE);
		assert datePreference != null;
		datePreference.setSummary(DATAPACK_DATE_SUMMARY_PROVIDER.provideSummary(datePreference));

		final Preference sizePreference = findPreference(Settings.PREF_DATAPACK_SIZE);
		assert sizePreference != null;
		sizePreference.setSummary(DATAPACK_LONG_SUMMARY_PROVIDER.provideSummary(sizePreference));

		final Preference sourcePreference = findPreference(Settings.PREF_DATAPACK_SOURCE);
		assert sourcePreference != null;
		sourcePreference.setSummary(DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourcePreference));

		final Preference sourceDatePreference = findPreference(Settings.PREF_DATAPACK_SOURCE_DATE);
		assert sourceDatePreference != null;
		sourceDatePreference.setSummary(DATAPACK_DATE_SUMMARY_PROVIDER.provideSummary(sourceDatePreference));

		final Preference sourceSizePreference = findPreference(Settings.PREF_DATAPACK_SOURCE_SIZE);
		assert sourceSizePreference != null;
		sourceSizePreference.setSummary(DATAPACK_LONG_SUMMARY_PROVIDER.provideSummary(sourceSizePreference));

		final Preference sourceEtagPreference = findPreference(Settings.PREF_DATAPACK_SOURCE_ETAG);
		assert sourceEtagPreference != null;
		sourceEtagPreference.setSummary(DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourceEtagPreference));

		final Preference sourceVersionPreference = findPreference(Settings.PREF_DATAPACK_SOURCE_VERSION);
		assert sourceVersionPreference != null;
		sourceVersionPreference.setSummary(DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourceVersionPreference));

		final Preference sourceStaticVersionPreference = findPreference(Settings.PREF_DATAPACK_SOURCE_STATIC_VERSION);
		assert sourceStaticVersionPreference != null;
		sourceStaticVersionPreference.setSummary(DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourceStaticVersionPreference));

		// unset button
		final Preference unsetButton = findPreference(Settings.PREF_DATAPACK_CLEAR_BUTTON);
		assert unsetButton != null;
		unsetButton.setOnPreferenceClickListener(preference -> {

			preference.getContext().getSharedPreferences(Settings.PREFERENCES_DATAPACK, Context.MODE_PRIVATE).edit() //
					.remove(Settings.PREF_DATAPACK_NAME) //
					.remove(Settings.PREF_DATAPACK_DATE) //
					.remove(Settings.PREF_DATAPACK_SIZE) //
					.remove(Settings.PREF_DATAPACK_SOURCE) //
					.remove(Settings.PREF_DATAPACK_SOURCE_DATE) //
					.remove(Settings.PREF_DATAPACK_SOURCE_SIZE) //
					.remove(Settings.PREF_DATAPACK_SOURCE_ETAG) //
					.remove(Settings.PREF_DATAPACK_SOURCE_VERSION) //
					.remove(Settings.PREF_DATAPACK_SOURCE_STATIC_VERSION) //
					.apply();

			namePreference.setSummary(DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(namePreference));
			datePreference.setSummary(DATAPACK_DATE_SUMMARY_PROVIDER.provideSummary(datePreference));
			sizePreference.setSummary(DATAPACK_LONG_SUMMARY_PROVIDER.provideSummary(sizePreference));
			sourcePreference.setSummary(DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourcePreference));
			sourceDatePreference.setSummary(DATAPACK_DATE_SUMMARY_PROVIDER.provideSummary(sourceDatePreference));
			sourceSizePreference.setSummary(DATAPACK_LONG_SUMMARY_PROVIDER.provideSummary(sourceSizePreference));
			sourceEtagPreference.setSummary(DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourceEtagPreference));
			sourceVersionPreference.setSummary(DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourceVersionPreference));
			sourceStaticVersionPreference.setSummary(DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourceStaticVersionPreference));
			return true;
		});
	}
}
