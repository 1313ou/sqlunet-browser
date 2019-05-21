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
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

/**
 * This fragment shows download preferences only. It is used when the activity is showing a two-pane settings UI.
 */
public class DownloadPreferenceFragment extends PreferenceFragmentCompat
{
	/**
	 * A preference value change listener that updates the preference's summary to reflect its new value.
	 */
	private static final Preference.OnPreferenceChangeListener bindListener = (preference, value) -> {
		String stringValue = value.toString();
		if (preference instanceof ListPreference)
		{
			// For list preferences, look up the correct display value in the preference's 'entries' list.
			ListPreference listPreference = (ListPreference) preference;
			int index = listPreference.findIndexOfValue(stringValue);

			// Set the summary to reflect the new value.
			preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
		}
		else
		{
			// For all other preferences, set the summary to the value's simple string representation.
			preference.setSummary(stringValue);
		}
		return true;
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 *
	 * @see #bindListener
	 */
	private static void bindPreferenceSummaryToValue(final Preference preference)
	{
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(bindListener);

		// Trigger the listener immediately with the preference's current value.
		String value = PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), "");
		bindListener.onPreferenceChange(preference, value);
	}

	private static void bindLongPreferenceSummaryToValue(final Preference preference)
	{
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(bindListener);

		// Trigger the listener immediately with the preference's current value.
		long value = PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getLong(preference.getKey(), -1);
		bindListener.onPreferenceChange(preference, value == -1 ? "" : Long.toString(value));
	}

	private static void bindDatePreferenceSummaryToValue(final Preference preference)
	{
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(bindListener);

		// Trigger the listener immediately with the preference's current value.
		long value = PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getLong(preference.getKey(), -1);
		bindListener.onPreferenceChange(preference, value == -1 ? "" : new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(new Date(value)));
	}

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
	{
		addPreferencesFromResource(R.xml.pref_downloaded);
		setHasOptionsMenu(true);

		// Bind the summaries of preferences to their values. When their values change, their summaries are updated to reflect the new value, per the Android Design guidelines.
		bindPreferenceSummaryToValue(findPreference(Settings.PREF_DB_NAME));
		bindDatePreferenceSummaryToValue(findPreference(Settings.PREF_DB_DATE));
		bindLongPreferenceSummaryToValue(findPreference(Settings.PREF_DB_SIZE));
	}

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		int id = item.getItemId();
		if (id == android.R.id.home)
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
