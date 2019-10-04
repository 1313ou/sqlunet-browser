/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Pair;

import org.sqlunet.browser.EntryActivity;
import org.sqlunet.browser.common.R;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageReports;
import org.sqlunet.settings.StorageUtils;
import org.sqlunet.sql.PreparedStatement;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

/**
 * A PreferenceActivity that presents a set of application settings. On handset devices, settings are presented as a single list. On tablets, settings
 * are split by category, with category headers shown to the left of the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html"> Android Design: Settings</a> for design guidelines and the
 * <a href="http://developer.android.com/guide/topics/ui/settings.html">Settings API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends BaseSettingsActivity
{
	// P O P U L A T E    L I S T S

	/**
	 * Set storage preference
	 *
	 * @param context context
	 * @param pref    preference
	 */
	@SuppressWarnings("SameReturnValue")
	static private void populateStoragePreference(@NonNull final Context context, final Preference pref)
	{
		final ListPreference listPreference = (ListPreference) pref;
		populateStorageListPreference(context, listPreference);
		listPreference.setOnPreferenceClickListener(preference -> {
			populateStorageListPreference(context, listPreference);
			return false;
		});
	}

	/**
	 * Set cache preference
	 *
	 * @param context context
	 * @param pref    preference
	 */
	@SuppressWarnings("SameReturnValue")
	static private void populateCachePreference(@NonNull final Context context, final Preference pref)
	{
		final ListPreference listPreference = (ListPreference) pref;
		populateCacheListPreference(context, listPreference);
		listPreference.setOnPreferenceClickListener(preference -> {
			populateCacheListPreference(context, listPreference);
			return false;
		});
	}

	/**
	 * Set storage preference data
	 *
	 * @param context  context
	 * @param listPref pref
	 */
	@SuppressWarnings("SameReturnValue")
	static private void populateStorageListPreference(@NonNull final Context context, @NonNull final ListPreference listPref)
	{
		final Pair<CharSequence[], CharSequence[]> namesValues = StorageReports.getStyledStorageDirectoriesNamesValues(context);

		CharSequence[] entries = namesValues.first;
		CharSequence[] entryValues = namesValues.second;
		CharSequence defaultValue;
		if (entries == null || entries.length == 0 || entryValues == null || entryValues.length == 0)
		{
			defaultValue = StorageUtils.DirType.AUTO.toString();
			entryValues = new CharSequence[]{defaultValue};
			entries = new CharSequence[]{StorageUtils.DirType.AUTO.toDisplay()};
		}
		else
		{
			defaultValue = entryValues[0];
		}

		listPref.setEntries(entries);
		listPref.setDefaultValue(defaultValue);
		listPref.setEntryValues(entryValues);
	}

	/**
	 * Set cache preference data
	 *
	 * @param context  context
	 * @param listPref pref
	 */
	static private void populateCacheListPreference(@NonNull final Context context, @NonNull final ListPreference listPref)
	{
		final Pair<CharSequence[], CharSequence[]> result = StorageReports.getStyledCachesNamesValues(context);
		final CharSequence[] names = result.first;
		final CharSequence[] values = result.second;

		listPref.setEntries(names);
		listPref.setEntryValues(values);
		listPref.setDefaultValue(values[0]);
	}

	// F R A G M E N T S

	/**
	 * This fragment shows general preferences only.
	 */
	static public class GeneralPreferenceFragment extends PreferenceFragmentCompat
	{
		static private final Preference.OnPreferenceChangeListener listener = (preference, value) -> {

			final String key = preference.getKey();
			final String prevValue = preference.getSharedPreferences().getString(key, null);
			//noinspection EqualsReplaceableByObjectsCall
			if (value == null ? prevValue != null : !value.equals(prevValue))
			{
				final Context context = preference.getContext();
				EntryActivity.reenter(context);
			}
			return true;
		};

		@Override
		public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey)
		{
			// inflate
			addPreferencesFromResource(R.xml.pref_general);

			// bind the summaries to their values.
			final Preference launchPreference = findPreference(Settings.PREF_LAUNCH);
			assert launchPreference != null;
			launchPreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());

			final Preference selectorPreference = findPreference(Settings.PREF_SELECTOR_MODE);
			assert selectorPreference != null;
			selectorPreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
			selectorPreference.setOnPreferenceChangeListener(listener);

			final Preference detailPreference = findPreference(Settings.PREF_DETAIL_MODE);
			assert detailPreference != null;
			detailPreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
			detailPreference.setOnPreferenceChangeListener(listener);

			final EditTextPreference sqlBufferCapacityPreference = getPreferenceManager().findPreference(BaseProvider.CircularBuffer.PREF_SQL_BUFFER_CAPACITY);
			assert sqlBufferCapacityPreference != null;
			sqlBufferCapacityPreference.setOnBindEditTextListener((editText) -> editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED));
			sqlBufferCapacityPreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
			sqlBufferCapacityPreference.setOnPreferenceChangeListener((preference, value) -> {

				final String sqlBufferCapacity = (String) value;
				if (sqlBufferCapacity != null)
				{
					try
					{
						int capacity = Integer.parseInt(sqlBufferCapacity);
						if (capacity >= 1 && capacity <= 64)
						{
							BaseProvider.resizeSql(capacity);
							return true;
						}
					}
					catch (Exception e)
					{
						//
					}
				}
				return false;
			});

			final Preference sqlLogPreference = findPreference(BaseProvider.CircularBuffer.PREF_SQL_LOG);
			assert sqlLogPreference != null;
			sqlLogPreference.setOnPreferenceChangeListener((preference, value) -> {

				boolean flag = (Boolean) value;
				PreparedStatement.logSql = flag;
				BaseProvider.logSql = flag;
				return true;
			});
		}
	}

	/**
	 * This fragment shows general preferences only.
	 */
	static public class FilterPreferenceFragment extends PreferenceFragmentCompat
	{
		@Override
		public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey)
		{
			// inflate
			addPreferencesFromResource(R.xml.pref_filter);
		}
	}

	/**
	 * This fragment shows database preferences only.
	 */
	static public class DatabasePreferenceFragment extends PreferenceFragmentCompat
	{
		@Override
		public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey)
		{
			// inflate
			addPreferencesFromResource(R.xml.pref_database);

			// required if no 'entries' and 'entryValues' in XML
			final Preference storagePreference = findPreference(Settings.PREF_STORAGE);
			assert storagePreference != null;
			populateStoragePreference(requireContext(), storagePreference);

			// bind the summaries to their values.
			storagePreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
		}
	}

	/**
	 * This fragment shows download preferences only.
	 */
	static public class DownloadPreferenceFragment extends PreferenceFragmentCompat
	{
		@Override
		public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey)
		{
			// inflate
			addPreferencesFromResource(R.xml.pref_download);

			// required if no 'entries' and 'entryValues' in XML
			final Preference cachePreference = findPreference(Settings.PREF_CACHE);
			assert cachePreference != null;
			populateCachePreference(requireContext(), cachePreference);
			cachePreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());

			// bind the summaries to their values.
			final Preference downloaderPreference = findPreference(Settings.PREF_DOWNLOADER);
			assert downloaderPreference != null;
			downloaderPreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());

			final Preference sitePreference = findPreference(Settings.PREF_DOWNLOAD_SITE);
			assert sitePreference != null;
			sitePreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());

			final Preference dbFilePreference = findPreference(Settings.PREF_DOWNLOAD_DBFILE);
			assert dbFilePreference != null;
			dbFilePreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());

			final Preference sqlFilePreference = findPreference(Settings.PREF_DOWNLOAD_SQLFILE);
			assert sqlFilePreference != null;
			sqlFilePreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());

			final Preference entryImportPreference = findPreference(Settings.PREF_ENTRY_IMPORT);
			assert entryImportPreference != null;
			entryImportPreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());

			final Preference entryIndexPreference = findPreference(Settings.PREF_ENTRY_INDEX);
			assert entryIndexPreference != null;
			entryIndexPreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
		}
	}
}
