/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.EntryActivity;
import org.sqlunet.browser.common.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public abstract class BaseSettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
{
	private static final String TITLE_TAG = "settingsActivityTitle";

	public static final String INITIAL_ARG = "settings_header";

	public static final int SETTINGS_FLAGS = 0;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		// super
		super.onCreate(savedInstanceState);

		// content view
		setContentView(R.layout.activity_settings);

		// fragment manager
		final FragmentManager fm = getSupportFragmentManager();

		// fragment
		if (savedInstanceState == null)
		{
			boolean initial = getIntent().getBooleanExtra(INITIAL_ARG, false);
			fm.beginTransaction().replace(R.id.settings, initial ? new Header2Fragment() : new HeaderFragment()).commit();
			setTitle(R.string.title_settings);
		}
		else
		{
			setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
		}
		fm.addOnBackStackChangedListener(() -> {

			if (fm.getBackStackEntryCount() == 0)
			{
				setTitle(R.string.title_settings);
			}
			else
			{
				CharSequence title = null;
				final List<Fragment> fragments = fm.getFragments();
				if (fragments.size() > 0)
				{
					final Fragment fragment = fragments.get(0); // only one at a time
					final PreferenceFragmentCompat preferenceFragment = (PreferenceFragmentCompat) fragment;
					title = preferenceFragment.getPreferenceScreen().getTitle();
				}
				if (title == null || title.length() == 0)
				{
					setTitle(R.string.title_settings);
				}
				else
				{
					setTitle(title);
				}
			}
			final ActionBar actionBar = getSupportActionBar();
			if (actionBar != null)
			{
				actionBar.setSubtitle(getTitle());
			}
		});

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.setTitle(R.string.app_name);
			actionBar.setSubtitle(getTitle());
			actionBar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		}
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState)
	{
		super.onSaveInstanceState(outState);
		// Save current activity title so we can set it again after a configuration change
		outState.putCharSequence(TITLE_TAG, getTitle());
	}

	// M E N U

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		final int itemId = item.getItemId();
		if (itemId == R.id.action_clear_settings)
		{
			resetSettings();
			restart();
			return true;
		}
		else if (itemId == R.id.action_diagnostics)
		{
			Intent intent = new Intent(this, DiagnosticsActivity.class);
			startActivity(intent);
			return true;
		}
		else if (itemId == R.id.action_logs)
		{
			final Intent intent = new Intent(this, LogsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// U P

	@Override
	public boolean onSupportNavigateUp()
	{
		final FragmentManager fm = getSupportFragmentManager();
		if (fm.popBackStackImmediate())
		{
			return true;
		}
		return super.onSupportNavigateUp();
	}

	// U T I L S

	@Override
	public boolean onPreferenceStartFragment(@NonNull PreferenceFragmentCompat caller, @NonNull Preference pref)
	{
		// Instantiate the new Fragment
		final Bundle args = pref.getExtras();
		final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(), pref.getFragment());
		fragment.setArguments(args);
		//noinspection deprecation
		fragment.setTargetFragment(caller, 0);

		// Replace the existing Fragment with the new Fragment
		getSupportFragmentManager().beginTransaction().replace(R.id.settings, fragment).addToBackStack(null).commit();
		return true;
	}

	// H E A D E R   F R A G M E N T

	@SuppressWarnings("WeakerAccess")
	public static class HeaderFragment extends PreferenceFragmentCompat
	{
		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
		{
			setPreferencesFromResource(R.xml.pref_headers, rootKey);
		}
	}

	@SuppressWarnings("WeakerAccess")
	public static class Header2Fragment extends PreferenceFragmentCompat
	{
		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
		{
			setPreferencesFromResource(R.xml.pref_headers2, rootKey);
		}
	}

	// U T I L S

	/**
	 * Reset settings
	 */
	private void resetSettings()
	{
		final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		final SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.clear();
		tryCommit(editor);
	}

	/**
	 * Try to commit
	 *
	 * @param editor editor editor
	 */
	@SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
	private void tryCommit(@NonNull final SharedPreferences.Editor editor)
	{
		try
		{
			editor.apply();
		}
		catch (@NonNull final AbstractMethodError ignored)
		{
			// The app injected its own pre-Gingerbread SharedPreferences.Editor implementation without an apply method.
			editor.commit();
		}
	}

	/**
	 * Restart app
	 */
	private void restart()
	{
		final Intent restartIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
		assert restartIntent != null;
		restartIntent.addFlags(EntryActivity.REENTER_FLAGS);
		startActivity(restartIntent);
	}
}
