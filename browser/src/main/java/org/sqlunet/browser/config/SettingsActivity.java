package org.sqlunet.browser.config;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Pair;

import org.sqlunet.browser.R;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageStyle;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On handset devices, settings are presented as a single list. On tablets, settings
 * are split by category, with category headers shown to the left of the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html"> Android Design: Settings</a> for design guidelines and the
 * <a href="http://developer.android.com/guide/topics/ui/settings.html">Settings API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity
{
	/**
	 * A preference value change listener that updates the preference's summary to reflect its new value.
	 */
	static private final Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener()
	{
		@Override
		public boolean onPreferenceChange(final Preference preference, final Object value)
		{
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
			return true;
		}
	};
	/**
	 * Determines whether to always show the simplified settings UI, where settings are presented in a single list. When false, settings are shown as a
	 * master/detail two-pane view on tablets. When true, a single pane is shown on tablets.
	 */
	static private boolean ALWAYS_SIMPLE_PREFS = false;

	/**
	 * Set always-simple-preferences flag
	 *
	 * @param alwaysSimplePrefs flag
	 */
	@SuppressWarnings("unused")
	public static void setAlwaysSimplePrefs(boolean alwaysSimplePrefs)
	{
		ALWAYS_SIMPLE_PREFS = alwaysSimplePrefs;
	}

	// E V E N T S

	/**
	 * Helper method to determine if the device has an large screen.
	 */
	static private boolean isLargeTablet(final Context context)
	{
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device doesn't
	 * have newer APIs like {@link PreferenceFragment}, or the device doesn't have an extra-large screen. In these cases, a single-pane "simplified" settings UI
	 * should be shown.
	 */
	static private boolean isSimplePreferences(final Context context)
	{
		return SettingsActivity.ALWAYS_SIMPLE_PREFS || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || !SettingsActivity.isLargeTablet(context);
	}

	// S E T U P

	/**
	 * Binds a preference's summary to its value. More specifically, when the preference's value is changed, its summary (line of text below the preference
	 * title) is updated to reflect the value. The summary is also immediately updated upon calling this method. The exact display format is dependent on the
	 * type of preference.
	 *
	 * @see #listener
	 */
	static private void bind(final Preference preference)
	{
		// set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(SettingsActivity.listener);

		// trigger the listener immediately with the preference's current value.
		final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
		final String key = preference.getKey();
		if (sharedPreferences.contains(key))
		{
			final Object val = sharedPreferences.getAll().get(key);
			SettingsActivity.listener.onPreferenceChange(preference, val);
		}
	}

	// D E T E C T I O N

	/**
	 * Set storage preference
	 *
	 * @param context context
	 * @param pref    preference
	 */
	static private void setStoragePreference(final Context context, final Preference pref)
	{
		final ListPreference listPreference = (ListPreference) pref;
		setStoragePreferenceData(context, listPreference);
		listPreference.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			@Override
			public boolean onPreferenceClick(Preference preference)
			{
				setStoragePreferenceData(context, listPreference);
				return false;
			}
		});
	}

	/**
	 * Set storage preference data
	 *
	 * @param context  context
	 * @param listPref pref
	 */
	static private void setStoragePreferenceData(final Context context, final ListPreference listPref)
	{
		final Pair<CharSequence[], CharSequence[]> candidateNamesValues = StorageStyle.getCandidateNamesValues(context);

		CharSequence[] entries = candidateNamesValues.first;
		CharSequence[] entryValues = candidateNamesValues.second;
		Object defaultValue;
		if (entries == null || entries.length == 0 || entryValues == null || entryValues.length == 0)
		{
			entries = new CharSequence[]{"auto"};
			entryValues = new CharSequence[]{"auto"};
			defaultValue = "auto";
		}
		else
		{
			defaultValue = entryValues[0];
		}

		listPref.setEntries(entries);
		listPref.setDefaultValue(defaultValue);
		listPref.setEntryValues(entryValues);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// show the Up button in the action bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onPostCreate(final Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);

		if (SettingsActivity.isSimplePreferences(this))
		{
			setupSimplePreferencesScreen();
		}
	}

	// L I S T E N E R

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(final List<Header> target)
	{
		if (!SettingsActivity.isSimplePreferences(this))
		{
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	// B I N D


	@Override
	protected boolean isValidFragment(final String fragmentName)
	{
		return GeneralPreferenceFragment.class.getName().equals(fragmentName) || //
				FilterPreferenceFragment.class.getName().equals(fragmentName) || //
				DatabasePreferenceFragment.class.getName().equals(fragmentName) || //
				DownloadPreferenceFragment.class.getName().equals(fragmentName);
	}

	// S T O R A G E

	/**
	 * Shows the simplified settings UI if the device configuration if the device configuration dictates that a simplified, single-pane UI should be shown.
	 */
	@SuppressWarnings("deprecation")
	private void setupSimplePreferencesScreen()
	{
		// in the simplified UI, fragments are not used at all and we instead use the older PreferenceActivity APIs.

		// Add container
		addPreferencesFromResource(R.xml.pref_container);
		final PreferenceScreen prefScreen = getPreferenceScreen();

		// add 'general' header
		final PreferenceCategory generalHeader = new PreferenceCategory(this);
		generalHeader.setTitle(R.string.pref_header_general);
		prefScreen.addPreference(generalHeader);

		// add 'general' preferences.
		addPreferencesFromResource(R.xml.pref_general);

		// add 'filter' header
		final PreferenceCategory filterHeader = new PreferenceCategory(this);
		filterHeader.setTitle(R.string.pref_header_filter);
		prefScreen.addPreference(filterHeader);

		// add 'filter' header
		addPreferencesFromResource(R.xml.pref_filter);

		// add 'database' header
		final PreferenceCategory databaseHeader = new PreferenceCategory(this);
		databaseHeader.setTitle(R.string.pref_header_database);
		prefScreen.addPreference(databaseHeader);

		// add 'download'
		addPreferencesFromResource(R.xml.pref_download);

		// add 'database'
		addPreferencesFromResource(R.xml.pref_database);
		setStoragePreference(this, findPreference(Settings.PREF_STORAGE));

		// bind the summaries of preferences to their values.
		SettingsActivity.bind(findPreference(Settings.PREF_SELECTOR_MODE));
		SettingsActivity.bind(findPreference(Settings.PREF_DETAIL_MODE));
		SettingsActivity.bind(findPreference(Settings.PREF_STORAGE));
		SettingsActivity.bind(findPreference(Settings.PREF_DOWNLOAD_SITE));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onIsMultiPane()
	{
		return SettingsActivity.isLargeTablet(this) && !SettingsActivity.isSimplePreferences(this);
	}

	// F R A G M E N T S

	/**
	 * This fragment shows general preferences only.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(final Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			// inflate
			addPreferencesFromResource(R.xml.pref_general);

			// bind the summaries to their values.
			SettingsActivity.bind(findPreference(Settings.PREF_SELECTOR_MODE));
			SettingsActivity.bind(findPreference(Settings.PREF_DETAIL_MODE));
		}
	}

	/**
	 * This fragment shows general preferences only.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class FilterPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(final Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			// inflate
			addPreferencesFromResource(R.xml.pref_filter);
		}
	}

	/**
	 * This fragment shows database preferences only.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class DatabasePreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(final Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			// inflate
			try
			{
				addPreferencesFromResource(R.xml.pref_database);
			}
			catch (Exception ignored)
			{
			}

			// required if no 'entries' and 'entryValues' in XML
			final Preference listPreference = findPreference(Settings.PREF_STORAGE);
			setStoragePreference(getActivity(), listPreference);

			// bind the summaries to their values.
			SettingsActivity.bind(listPreference);
		}
	}

	/**
	 * This fragment shows download preferences only.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class DownloadPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(final Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			// inflate
			addPreferencesFromResource(R.xml.pref_download);

			// bind the summaries to their values.
			SettingsActivity.bind(findPreference(Settings.PREF_DOWNLOAD_SITE));
			SettingsActivity.bind(findPreference(Settings.PREF_DOWNLOAD_DBFILE));
			SettingsActivity.bind(findPreference(Settings.PREF_DOWNLOAD_SQLFILE));
			SettingsActivity.bind(findPreference(Settings.PREF_ENTRY_IMPORT));
			SettingsActivity.bind(findPreference(Settings.PREF_ENTRY_PM));
			SettingsActivity.bind(findPreference(Settings.PREF_ENTRY_INDEX));
		}
	}
}
