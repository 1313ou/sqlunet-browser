/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Pair;

import org.sqlunet.browser.EntryActivity;
import org.sqlunet.browser.common.R;
import com.bbou.download.workers.utils.ResourcesDownloader;
import org.sqlunet.preference.OpenEditTextPreference;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.Storage;
import org.sqlunet.settings.StorageReports;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;
import org.sqlunet.sql.PreparedStatement;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

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
		final Pair<CharSequence[], CharSequence[]> namesValues = StorageReports.getStyledStoragesNamesValues(context);

		CharSequence[] entries = namesValues.first;
		CharSequence[] entryValues = namesValues.second;
		CharSequence defaultValue;
		if (entries == null || entries.length == 0 || entryValues == null || entryValues.length == 0)
		{
			defaultValue = StorageUtils.AUTO;
			entryValues = new CharSequence[]{defaultValue};
			entries = new CharSequence[]{StorageUtils.AUTO_LABEL};
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
		final Pair<CharSequence[], String[]> result = StorageReports.getStyledCachesNamesValues(context);
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
	@SuppressWarnings("WeakerAccess")
	static public class GeneralPreferenceFragment extends PreferenceFragmentCompat
	{
		static private final Preference.OnPreferenceChangeListener listener = (preference, value) -> {

			final SharedPreferences sharedPrefs = preference.getSharedPreferences();
			assert sharedPrefs != null;
			final String key = preference.getKey();
			final String prevValue = sharedPrefs.getString(key, null);
			//noinspection EqualsReplaceableByObjectsCall
			if (value == null ? prevValue != null : !value.equals(prevValue))
			{
				final Context context = preference.getContext();
				EntryActivity.rerun(context);
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

			final Preference twoPanesPreference = findPreference(Settings.PREF_TWO_PANES);
			assert twoPanesPreference != null;
			twoPanesPreference.setOnPreferenceChangeListener((preference, value) -> {

				boolean flag = (Boolean) value;
				Settings.paneMode = flag ? 2 : 0;
				return true;
			});
		}
	}

	/**
	 * This fragment shows general preferences only.
	 */
	@SuppressWarnings("WeakerAccess")
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
	@SuppressWarnings("WeakerAccess")
	static public class DatabasePreferenceFragment extends PreferenceFragmentCompat
	{
		@Override
		public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey)
		{
			// inflate
			addPreferencesFromResource(R.xml.pref_database);

			// db file
			final Preference dbFilePreference = findPreference(Settings.PREF_DB_FILE);
			assert dbFilePreference != null;
			String storage = StorageSettings.getDatabasePath(requireContext());
			dbFilePreference.setSummary(storage);
			dbFilePreference.setOnPreferenceChangeListener((preference, newValue) -> {
				String storage2 = (String) newValue;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && StorageUtils.isAuto(storage2)) //
				{
					storage2 = requireContext().getFilesDir().getAbsolutePath();
				}
				storage2 += File.separatorChar + Storage.DBFILE;
				dbFilePreference.setSummary(storage2);
				return false;
			});

			// storage
			final Preference storagePreference = findPreference(Settings.PREF_STORAGE);
			assert storagePreference != null;
			// required if no 'entries' and 'entryValues' in XML
			populateStoragePreference(requireContext(), storagePreference);
			storagePreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
			final Preference.OnPreferenceChangeListener listener1 = storagePreference.getOnPreferenceChangeListener();
			storagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
				if (listener1 != null)
				{
					listener1.onPreferenceChange(preference, newValue);
				}
				dbFilePreference.callChangeListener(newValue);
				return true;
			});
		}
	}

	/**
	 * This fragment shows database preferences only.
	 */
	@SuppressWarnings("WeakerAccess")
	static public class DatabasePreference2Fragment extends PreferenceFragmentCompat
	{
		@Override
		public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey)
		{
			// inflate
			addPreferencesFromResource(R.xml.pref_database2);

			// db file
			final Preference dbFilePreference = findPreference(Settings.PREF_DB_FILE);
			assert dbFilePreference != null;
			String storage = StorageSettings.getDatabasePath(requireContext());
			dbFilePreference.setSummary(storage);
			dbFilePreference.setOnPreferenceChangeListener((preference, newValue) -> {
				String storage2 = (String) newValue;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && StorageUtils.isAuto(storage2)) //
				{
					storage2 = requireContext().getFilesDir().getAbsolutePath();
				}
				storage2 += File.separatorChar + Storage.DBFILE;
				dbFilePreference.setSummary(storage2);
				return false;
			});

			// storage
			final Preference storagePreference = findPreference(Settings.PREF_STORAGE);
			assert storagePreference != null;
			// required if no 'entries' and 'entryValues' in XML
			populateStoragePreference(requireContext(), storagePreference);
			storagePreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
			final Preference.OnPreferenceChangeListener listener1 = storagePreference.getOnPreferenceChangeListener();
			storagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
				if (listener1 != null)
				{
					listener1.onPreferenceChange(preference, newValue);
				}
				dbFilePreference.callChangeListener(newValue);
				return true;
			});
		}
	}

	/**
	 * This fragment shows asset pack preferences only.
	 */
	@SuppressWarnings("WeakerAccess")
	static public class AssetsPreferenceFragment extends PreferenceFragmentCompat
	{
		@Override
		public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey)
		{
			// inflate
			addPreferencesFromResource(R.xml.pref_assets);

			// required if no 'entries' and 'entryValues' in XML
			final Preference dbAssetPreference = findPreference(org.sqlunet.assetpack.Settings.PREF_DB_ASSET);
			assert dbAssetPreference != null;

			// bind the summaries to their values.
			dbAssetPreference.setSummaryProvider((preference) -> PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), "-"));
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

			// bind the summaries to their values.
			final Preference downloadModePreference = findPreference(Settings.PREF_DOWNLOAD_MODE);
			assert downloadModePreference != null;
			downloadModePreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());

			final OpenEditTextPreference sitePreference = findPreference(Settings.PREF_DOWNLOAD_SITE);
			assert sitePreference != null;
			ResourcesDownloader.populateLists(requireContext(), sitePreference::addOptions);
			sitePreference.setSummaryProvider(OpenEditTextPreference.SUMMARY_PROVIDER);

			final Preference dbFilePreference = findPreference(Settings.PREF_DOWNLOAD_DBFILE);
			assert dbFilePreference != null;
			dbFilePreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
		}

		@Override
		public void onDisplayPreferenceDialog(@NonNull final Preference preference)
		{
			if (!OpenEditTextPreference.onDisplayPreferenceDialog(this, preference))
			{
				super.onDisplayPreferenceDialog(preference);
			}
		}
	}
}
