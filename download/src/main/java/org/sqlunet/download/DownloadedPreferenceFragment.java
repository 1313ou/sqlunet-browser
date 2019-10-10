/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.app.Activity;
import android.content.Intent;
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
