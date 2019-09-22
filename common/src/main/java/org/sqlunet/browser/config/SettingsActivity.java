/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.AppCompatCommonPreferenceActivity;
import org.sqlunet.browser.ColorUtils;
import org.sqlunet.browser.EntryActivity;
import org.sqlunet.browser.common.R;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageReports;
import org.sqlunet.settings.StorageUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
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
public class SettingsActivity extends AppCompatCommonPreferenceActivity
{
	// L I F E C Y C L E

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		// super
		super.onCreate(savedInstanceState);

		// toolbar
		//setupToolbar(R.layout.toolbar, R.id.toolbar);

		// action bar
		//setupActionBar();
	}

	// T O O L B A R

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	private void setupActionBar()
	{
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			// Show the Up button in the action bar.
			// actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);

			// background
			final int color = ColorUtils.getColor(this, R.color.primaryColor);
			actionBar.setBackgroundDrawable(new ColorDrawable(color));
		}
	}

	@SuppressWarnings({"SameParameterValue", "WeakerAccess"})
	protected void setupToolbar(int toolbarLayout, int toolbarId)
	{
		// TODO hacked dependency on R.id.action_bar_root
		final ViewGroup rootView = findViewById(R.id.action_bar_root); //id from appcompat
		if (rootView != null)
		{
			final View view = getLayoutInflater().inflate(toolbarLayout, rootView, false);
			rootView.addView(view, 0);

			final Toolbar toolbar = findViewById(toolbarId);
			setSupportActionBar(toolbar);
		}
	}
	
	// L I S T E N E R

	/**
	 * A preference value change listener that updates the preference's summary to reflect its new value.
	 */
	@Nullable
	static private final Preference.OnPreferenceChangeListener listener = (preference, value) -> {
		if (preference instanceof ListPreference && value != null)
		{
			// For list preferences, look up the correct display value in the preference's 'entries' list.
			final ListPreference listPreference = (ListPreference) preference;
			final String stringValue = value.toString();
			final int index = listPreference.findIndexOfValue(stringValue);

			// Set the summary to reflect the new value.
			preference.setSummary(index >= 0 ? listPreference.getEntries()[index].toString().trim() : null);
		}
		else
		{
			// For all other preferences, set the summary to the value's simple string representation.
			final String stringValue = value != null ? value.toString() : "<default>";
			preference.setSummary(stringValue);
		}
		String key = preference.getKey();
		if (Settings.PREF_SELECTOR_MODE.equals(key) || Settings.PREF_DETAIL_MODE.equals(key))
		{
			final String prevValue = preference.getSharedPreferences().getString(key, null);

			//if (Objects.equals(prevValue, value))
			//noinspection EqualsReplaceableByObjectsCall
			if (value == null ? prevValue != null : !value.equals(prevValue))
			{
				final Context context = preference.getContext();
				EntryActivity.reenter(context);
			}
		}
		return true;
	};

	// B I N D

	/**
	 * Binds a preference's summary to its value. More specifically, when the preference's value is changed, its summary (line of text below the preference
	 * titleId) is updated to reflect the value. The summary is also immediately updated upon calling this method. The exact display format is dependent on the
	 * type of preference.
	 *
	 * @see #listener
	 */
	static public void bind(@NonNull final Preference preference)
	{
		// set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(SettingsActivity.listener);

		// trigger the listener immediately with the preference's current value.
		final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
		final String key = preference.getKey();
		if (sharedPreferences.contains(key))
		{
			final Object val = sharedPreferences.getAll().get(key);
			assert listener != null;
			listener.onPreferenceChange(preference, val);
		}

		// globals
		if (Settings.PREF_SQL_LOG.equals(key))
		{
			Settings.update(preference.getContext());
		}
	}

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
		@Override
		public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey)
		{
			// inflate
			addPreferencesFromResource(R.xml.pref_general);

			// bind the summaries to their values.
			final Preference selectorPreference = findPreference(Settings.PREF_SELECTOR_MODE);
			assert selectorPreference != null;
			bind(selectorPreference);

			final Preference detailPreference = findPreference(Settings.PREF_DETAIL_MODE);
			assert detailPreference != null;
			bind(detailPreference);
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
			bind(storagePreference);
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

			// bind the summaries to their values.
			final Preference downloaderPreference = findPreference(Settings.PREF_DOWNLOADER);
			assert downloaderPreference != null;
			bind(downloaderPreference);

			final Preference sitePreference = findPreference(Settings.PREF_DOWNLOAD_SITE);
			assert sitePreference != null;
			bind(sitePreference);

			final Preference dbFilePreference = findPreference(Settings.PREF_DOWNLOAD_DBFILE);
			assert dbFilePreference != null;
			bind(dbFilePreference);

			final Preference sqlFilePreference = findPreference(Settings.PREF_DOWNLOAD_SQLFILE);
			assert sqlFilePreference != null;
			bind(sqlFilePreference);

			final Preference entryImportPreference = findPreference(Settings.PREF_ENTRY_IMPORT);
			assert entryImportPreference != null;
			bind(entryImportPreference);

			final Preference entryIndexPreference = findPreference(Settings.PREF_ENTRY_INDEX);
			assert entryIndexPreference != null;
			bind(entryIndexPreference);

			bind(cachePreference);
		}
	}
}
