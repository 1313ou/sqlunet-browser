package org.sqlunet.download;

import android.content.Context;
import android.os.Bundle;

import org.sqlunet.download.R;

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

	private static final Preference.SummaryProvider<Preference> MODEL_STRING_SUMMARY_PROVIDER = (preference) -> preference.getContext().getSharedPreferences(Settings.PREFERENCES_MODEL, Context.MODE_PRIVATE).getString(preference.getKey(), unrecorded);

	private static final Preference.SummaryProvider<Preference> MODEL_LONG_SUMMARY_PROVIDER = (preference) -> {
		long value = preference.getContext().getSharedPreferences(Settings.PREFERENCES_MODEL, Context.MODE_PRIVATE).getLong(preference.getKey(), -1);
		return value == -1 ? unrecorded : Long.toString(value);
	};

	private static final Preference.SummaryProvider<Preference> MODEL_DATE_SUMMARY_PROVIDER = (preference) -> {
		long value = preference.getContext().getSharedPreferences(Settings.PREFERENCES_MODEL, Context.MODE_PRIVATE).getLong(preference.getKey(), -1);
		return value == -1 ? unrecorded : new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(new Date(value));
	};

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
	{
		unrecorded = getString(R.string.pref_value_unrecorded);

		final PreferenceManager manager = this.getPreferenceManager();
		manager.setSharedPreferencesName(Settings.PREFERENCES_MODEL);
		manager.setSharedPreferencesMode(Context.MODE_PRIVATE);

		addPreferencesFromResource(R.xml.pref_downloaded);

		final Preference namePreference = findPreference(Settings.PREF_MODEL_NAME);
		assert namePreference != null;
		namePreference.setSummary(MODEL_STRING_SUMMARY_PROVIDER.provideSummary(namePreference));

		final Preference datePreference = findPreference(Settings.PREF_MODEL_DATE);
		assert datePreference != null;
		datePreference.setSummary(MODEL_DATE_SUMMARY_PROVIDER.provideSummary(datePreference));

		final Preference sizePreference = findPreference(Settings.PREF_MODEL_SIZE);
		assert sizePreference != null;
		sizePreference.setSummary(MODEL_LONG_SUMMARY_PROVIDER.provideSummary(sizePreference));

		final Preference sourcePreference = findPreference(Settings.PREF_MODEL_SOURCE);
		assert sourcePreference != null;
		sourcePreference.setSummary(MODEL_STRING_SUMMARY_PROVIDER.provideSummary(sourcePreference));

		final Preference sourceDatePreference = findPreference(Settings.PREF_MODEL_SOURCE_DATE);
		assert sourceDatePreference != null;
		sourceDatePreference.setSummary(MODEL_DATE_SUMMARY_PROVIDER.provideSummary(sourceDatePreference));

		final Preference sourceSizePreference = findPreference(Settings.PREF_MODEL_SOURCE_SIZE);
		assert sourceSizePreference != null;
		sourceSizePreference.setSummary(MODEL_LONG_SUMMARY_PROVIDER.provideSummary(sourceSizePreference));

		final Preference sourceEtagPreference = findPreference(Settings.PREF_MODEL_SOURCE_ETAG);
		assert sourceEtagPreference != null;
		sourceEtagPreference.setSummary(MODEL_STRING_SUMMARY_PROVIDER.provideSummary(sourceEtagPreference));

		final Preference sourceVersionPreference = findPreference(Settings.PREF_MODEL_SOURCE_VERSION);
		assert sourceVersionPreference != null;
		sourceVersionPreference.setSummary(MODEL_STRING_SUMMARY_PROVIDER.provideSummary(sourceVersionPreference));

		final Preference sourceStaticVersionPreference = findPreference(Settings.PREF_MODEL_SOURCE_STATIC_VERSION);
		assert sourceStaticVersionPreference != null;
		sourceStaticVersionPreference.setSummary(MODEL_STRING_SUMMARY_PROVIDER.provideSummary(sourceStaticVersionPreference));

		// unset button
		final Preference unsetButton = findPreference(Settings.PREF_MODEL_CLEAR_BUTTON);
		assert unsetButton != null;
		unsetButton.setOnPreferenceClickListener(preference -> {

			preference.getContext().getSharedPreferences(Settings.PREFERENCES_MODEL, Context.MODE_PRIVATE).edit() //
					.remove(Settings.PREF_MODEL_NAME) //
					.remove(Settings.PREF_MODEL_DATE) //
					.remove(Settings.PREF_MODEL_SIZE) //
					.remove(Settings.PREF_MODEL_SOURCE) //
					.remove(Settings.PREF_MODEL_SOURCE_DATE) //
					.remove(Settings.PREF_MODEL_SOURCE_SIZE) //
					.remove(Settings.PREF_MODEL_SOURCE_ETAG) //
					.remove(Settings.PREF_MODEL_SOURCE_VERSION) //
					.remove(Settings.PREF_MODEL_SOURCE_STATIC_VERSION) //
					.apply();

			namePreference.setSummary(MODEL_STRING_SUMMARY_PROVIDER.provideSummary(namePreference));
			datePreference.setSummary(MODEL_DATE_SUMMARY_PROVIDER.provideSummary(datePreference));
			sizePreference.setSummary(MODEL_LONG_SUMMARY_PROVIDER.provideSummary(sizePreference));
			sourcePreference.setSummary(MODEL_STRING_SUMMARY_PROVIDER.provideSummary(sourcePreference));
			sourceDatePreference.setSummary(MODEL_DATE_SUMMARY_PROVIDER.provideSummary(sourceDatePreference));
			sourceSizePreference.setSummary(MODEL_LONG_SUMMARY_PROVIDER.provideSummary(sourceSizePreference));
			sourceEtagPreference.setSummary(MODEL_STRING_SUMMARY_PROVIDER.provideSummary(sourceEtagPreference));
			sourceVersionPreference.setSummary(MODEL_STRING_SUMMARY_PROVIDER.provideSummary(sourceVersionPreference));
			sourceStaticVersionPreference.setSummary(MODEL_STRING_SUMMARY_PROVIDER.provideSummary(sourceStaticVersionPreference));
			return true;
		});
	}
}
