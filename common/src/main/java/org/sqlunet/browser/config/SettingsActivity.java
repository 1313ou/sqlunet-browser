package org.sqlunet.browser.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.contrib.AppCompatPreferenceActivity;
import android.util.Pair;
import android.view.MenuItem;

import org.sqlunet.browser.ColorUtils;
import org.sqlunet.browser.common.R;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageReports;
import org.sqlunet.settings.StorageUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On handset devices, settings are presented as a single list. On tablets, settings
 * are split by category, with category headers shown to the left of the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html"> Android Design: Settings</a> for design guidelines and the
 * <a href="http://developer.android.com/guide/topics/ui/settings.html">Settings API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity
{
	/**
	 * Determines whether to always show the simplified settings UI, where settings are presented in a single list. When false, settings are shown as a
	 * master/detail two-pane view on tablets. When true, a single pane is shown on tablets.
	 */
	static private boolean ALWAYS_SIMPLE_PREFS = false;

	// L I S T E N E R

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		// super
		super.onCreate(savedInstanceState);

		// toolbar
		setupToolbar(R.layout.toolbar, R.id.toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			// background
			final int color = ColorUtils.getColor(this, R.color.primaryColor);
			actionBar.setBackgroundDrawable(new ColorDrawable(color));

			// options
			actionBar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		}
	}

	// L I S T E N E R

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

	// S E T T I N G S

	/**
	 * Set always-simple-preferences flag
	 *
	 * @param alwaysSimplePrefs flag
	 */
	@SuppressWarnings("unused")
	static public void setAlwaysSimplePrefs(boolean alwaysSimplePrefs)
	{
		ALWAYS_SIMPLE_PREFS = alwaysSimplePrefs;
	}

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
		return SettingsActivity.ALWAYS_SIMPLE_PREFS || !SettingsActivity.isLargeTablet(context);
	}

	@Override
	public boolean onIsMultiPane()
	{
		return SettingsActivity.isLargeTablet(this) && !SettingsActivity.isSimplePreferences(this);
	}

	// V A L I D A T I O N

	static private Set<String> allowedFragments;

	@SuppressWarnings("TryWithIdenticalCatches")
	@Override
	protected boolean isValidFragment(final String fragmentName)
	{
		if (allowedFragments == null)
		{
			allowedFragments = new HashSet<>();
			final Resources res = getResources();
			final XmlResourceParser xrp = res.getXml(R.xml.pref_headers);
			try
			{
				while (xrp.next() != XmlPullParser.END_DOCUMENT)
				{
					if (xrp.getEventType() != XmlPullParser.START_TAG)
					{
						continue;
					}
					if ("header".equals(xrp.getName()))
					{
						final String attr = xrp.getAttributeValue("http://schemas.android.com/apk/res/android", "fragment");
						allowedFragments.add(attr);
					}
				}
			}
			catch (XmlPullParserException e)
			{
				//
			}
			catch (IOException e)
			{
				//
			}
		}

		return allowedFragments.contains(fragmentName);
	}

	// B I N D

	/**
	 * Binds a preference's summary to its value. More specifically, when the preference's value is changed, its summary (line of text below the preference
	 * titleId) is updated to reflect the value. The summary is also immediately updated upon calling this method. The exact display format is dependent on the
	 * type of preference.
	 *
	 * @see #listener
	 */
	static public void bind(final Preference preference)
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

	// P O P U L A T E    L I S T S

	/**
	 * Set storage preference
	 *
	 * @param context context
	 * @param pref    preference
	 */
	static private void populateStoragePreference(final Context context, final Preference pref)
	{
		final ListPreference listPreference = (ListPreference) pref;
		populateStorageListPreference(context, listPreference);
		listPreference.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			@Override
			public boolean onPreferenceClick(Preference preference)
			{
				populateStorageListPreference(context, listPreference);
				return false;
			}
		});
	}

	/**
	 * Set cache preference
	 *
	 * @param context context
	 * @param pref    preference
	 */
	static private void populateCachePreference(final Context context, final Preference pref)
	{
		final ListPreference listPreference = (ListPreference) pref;
		populateCacheListPreference(context, listPreference);
		listPreference.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			@Override
			public boolean onPreferenceClick(Preference preference)
			{
				populateCacheListPreference(context, listPreference);
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
	static private void populateStorageListPreference(final Context context, final ListPreference listPref)
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
	static private void populateCacheListPreference(final Context context, final ListPreference listPref)
	{
		final Pair<CharSequence[], CharSequence[]> result = StorageReports.getStyledCachesNamesValues(context);
		final CharSequence[] names = result.first;
		final CharSequence[] values = result.second;

		listPref.setEntries(names);
		listPref.setEntryValues(values);
		listPref.setDefaultValue(values[0]);
	}

	// L I F E C Y C L E

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
	public void onBuildHeaders(final List<Header> target)
	{
		if (!SettingsActivity.isSimplePreferences(this))
		{
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	// S I M P L E

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

		// addItem 'general' header
		final PreferenceCategory generalHeader = new PreferenceCategory(this);
		generalHeader.setTitle(R.string.pref_header_general);
		prefScreen.addPreference(generalHeader);

		// addItem 'general' preferences.
		addPreferencesFromResource(R.xml.pref_general);

		// addItem 'filter' header
		final PreferenceCategory filterHeader = new PreferenceCategory(this);
		filterHeader.setTitle(R.string.pref_header_filter);
		prefScreen.addPreference(filterHeader);

		// addItem 'filter' header
		addPreferencesFromResource(R.xml.pref_filter);

		// addItem 'database' header
		final PreferenceCategory databaseHeader = new PreferenceCategory(this);
		databaseHeader.setTitle(R.string.pref_header_database);
		prefScreen.addPreference(databaseHeader);

		// addItem 'download'
		addPreferencesFromResource(R.xml.pref_download);
		populateCachePreference(this, findPreference(Settings.PREF_CACHE));

		// addItem 'database'
		addPreferencesFromResource(R.xml.pref_database);
		populateStoragePreference(this, findPreference(Settings.PREF_STORAGE));

		// addItem 'database'

		// bind the summaries of preferences to their values.
		SettingsActivity.bind(findPreference(Settings.PREF_SELECTOR_MODE));
		SettingsActivity.bind(findPreference(Settings.PREF_DETAIL_MODE));
		SettingsActivity.bind(findPreference(Settings.PREF_STORAGE));
		SettingsActivity.bind(findPreference(Settings.PREF_DOWNLOAD_SITE));
		SettingsActivity.bind(findPreference(Settings.PREF_DOWNLOADER));
		SettingsActivity.bind(findPreference(Settings.PREF_CACHE));
	}

	// F R A G M E N T S

	/**
	 * This fragment shows general preferences only.
	 */
	static public class GeneralPreferenceFragment extends PreferenceFragment
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
	static public class FilterPreferenceFragment extends PreferenceFragment
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
	static public class DatabasePreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(final Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			// inflate
			addPreferencesFromResource(R.xml.pref_database);

			// required if no 'entries' and 'entryValues' in XML
			final Preference storagePreference = findPreference(Settings.PREF_STORAGE);
			populateStoragePreference(getActivity(), storagePreference);

			// bind the summaries to their values.
			SettingsActivity.bind(storagePreference);
		}
	}

	/**
	 * This fragment shows download preferences only.
	 */
	static public class DownloadPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(final Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			// inflate
			addPreferencesFromResource(R.xml.pref_download);

			// required if no 'entries' and 'entryValues' in XML
			final Preference cachePreference = findPreference(Settings.PREF_CACHE);
			populateCachePreference(getActivity(), cachePreference);

			// bind the summaries to their values.
			SettingsActivity.bind(findPreference(Settings.PREF_DOWNLOAD_SITE));
			SettingsActivity.bind(findPreference(Settings.PREF_DOWNLOADER));
			SettingsActivity.bind(findPreference(Settings.PREF_DOWNLOAD_DBFILE));
			SettingsActivity.bind(findPreference(Settings.PREF_DOWNLOAD_SQLFILE));
			SettingsActivity.bind(findPreference(Settings.PREF_ENTRY_IMPORT));
			SettingsActivity.bind(findPreference(Settings.PREF_ENTRY_INDEX));
			SettingsActivity.bind(cachePreference);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
