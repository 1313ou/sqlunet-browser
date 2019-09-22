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

import com.bbou.rate.AppRate;
import com.google.android.material.navigation.NavigationView;

import org.sqlunet.browser.common.R;
import org.sqlunet.settings.StorageSettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity
{
	static private final String TAG = "MainA";

	private AppBarConfiguration appBarConfiguration;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// rate
		AppRate.invoke(this);

		// info
		Log.d(TAG, "DATABASE=" + StorageSettings.getDatabasePath(getBaseContext()));

		// content view
		setContentView(R.layout.activity_main);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// navigation
		final DrawerLayout drawer = findViewById(R.id.drawer_layout);
		final NavigationView navigationView = findViewById(R.id.nav_view);
		final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

		// passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
		this.appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_search_browse, R.id.nav_search_text, R.id.nav_status, R.id.nav_setup, R.id.nav_settings).setDrawerLayout(drawer).build();
		NavigationUI.setupActionBarWithNavController(this, navController, this.appBarConfiguration);
		NavigationUI.setupWithNavController(navigationView, navController);
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
		final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
		final Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
		if (fragment instanceof BaseSearchFragment)
		{
			final String action = intent.getAction();
			final boolean isActionView = Intent.ACTION_VIEW.equals(action);
			if (isActionView || Intent.ACTION_SEARCH.equals(action))
			{
				final BaseSearchFragment searchFragment = (BaseSearchFragment) fragment;
				// search query submit (SEARCH) or suggestion selection (when a suggested item is selected) (VIEW)
				final String query = intent.getStringExtra(SearchManager.QUERY);

				if (isActionView)
				{
					searchFragment.clearQuery();
				}

				// search query submit or suggestion selection (when a suggested item is selected)
				Log.d(TAG, "search " + query);
				searchFragment.search(query);
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

	// N A V

	@Override
	public boolean onSupportNavigateUp()
	{
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		return NavigationUI.navigateUp(navController, this.appBarConfiguration) || super.onSupportNavigateUp();
	}
}
