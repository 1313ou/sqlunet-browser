/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.common.R;
import org.sqlunet.settings.StorageSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity // implements NavigationFragment.Listener
{
	static private final String TAG = "MainA";

	@Nullable
	private NavigationFragment navigationDrawerFragment;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// info
		Log.d(MainActivity.TAG, "DATABASE=" + StorageSettings.getDatabasePath(getBaseContext()));

		// content view
		setContentView(R.layout.activity_main);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// get fragment
		this.navigationDrawerFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

		// set up the drawer
		assert this.navigationDrawerFragment != null;
		this.navigationDrawerFragment.setUp(R.id.navigation_drawer, findViewById(R.id.drawer_layout));
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// check hook
		EntryActivity.forkOffIfCantRun(this);

		// handle sent intent
		handleSearchIntent(getIntent());
	}

	@Override
	protected void onNewIntent(@NonNull final Intent intent)
	{
		super.onNewIntent(intent);
		handleSearchIntent(intent);
	}

	// S E A R C H

	/**
	 * Handle intent dispatched by search view (either onCreate or onNewIntent if activity is single top)
	 *
	 * @param intent intent
	 */
	private void handleSearchIntent(@NonNull final Intent intent)
	{
		assert this.navigationDrawerFragment != null;
		final Fragment fragment = this.navigationDrawerFragment.getActiveFragment();
		if (fragment instanceof SearchListener)
		{
			final String action = intent.getAction();
			if (Intent.ACTION_VIEW.equals(action) || Intent.ACTION_SEARCH.equals(action))
			{
				final SearchListener listener = (SearchListener) fragment;
				final String query = intent.getStringExtra(SearchManager.QUERY);

				// search query submit or suggestion selection (when a suggested item is selected)
				Log.d(TAG, "search " + query);
				listener.search(query);
			}
		}
	}

	// M E N U

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		return MenuHandler.menuDispatch(this, item);
	}
}
