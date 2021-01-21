/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

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

	// S U M M A R Y

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
		namePreference.setSummaryProvider(STRING_SUMMARY_PROVIDER);

		final Preference datePreference = findPreference(Settings.PREF_DB_DATE);
		assert datePreference != null;
		datePreference.setSummaryProvider(DATE_SUMMARY_PROVIDER);

		final Preference sizePreference = findPreference(Settings.PREF_DB_SIZE);
		assert sizePreference != null;
		sizePreference.setSummaryProvider(LONG_SUMMARY_PROVIDER);

		final Preference sourcePreference = findPreference(Settings.PREF_DB_SOURCE);
		assert sourcePreference != null;
		sourcePreference.setSummaryProvider(STRING_SUMMARY_PROVIDER);

		final Preference sourceDatePreference = findPreference(Settings.PREF_DB_SOURCE_DATE);
		assert sourceDatePreference != null;
		sourceDatePreference.setSummaryProvider(DATE_SUMMARY_PROVIDER);

		final Preference sourceSizePreference = findPreference(Settings.PREF_DB_SOURCE_SIZE);
		assert sourceSizePreference != null;
		sourceSizePreference.setSummaryProvider(LONG_SUMMARY_PROVIDER);

		final Preference sourceEtagPreference = findPreference(Settings.PREF_DB_SOURCE_ETAG);
		assert sourceEtagPreference != null;
		sourceEtagPreference.setSummaryProvider(STRING_SUMMARY_PROVIDER);

		final Preference sourceVersionPreference = findPreference(Settings.PREF_DB_SOURCE_VERSION);
		assert sourceVersionPreference != null;
		sourceVersionPreference.setSummaryProvider(STRING_SUMMARY_PROVIDER);

		final Preference sourceStaticVersionPreference = findPreference(Settings.PREF_DB_SOURCE_STATIC_VERSION);
		assert sourceStaticVersionPreference != null;
		sourceStaticVersionPreference.setSummaryProvider(STRING_SUMMARY_PROVIDER);

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
			return true;
		});
	}

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		final int itemId = item.getItemId();
		if (itemId == android.R.id.home)
		{
			final Activity activity = getActivity();
			if (activity != null)
			{
				startActivity(new Intent(activity, activity.getClass()));
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
