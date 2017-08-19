package org.sqlunet.browser;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.common.R;
import org.sqlunet.browser.config.Status;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageSettings;

public class MainActivity extends AppCompatActivity // implements NavigationFragment.Listener
{
	static private final String TAG = "MainActivity";

	private NavigationFragment navigationDrawerFragment;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// settings
		Settings.initialize(this);

		// info
		Log.d(MainActivity.TAG, "DATABASE=" + StorageSettings.getDatabasePath(getBaseContext()));

		// content view
		setContentView(R.layout.activity_main);

		// toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// get fragment
		this.navigationDrawerFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

		// set up the drawer
		this.navigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// check hook
		boolean canRun = Status.canRun(getBaseContext());
		if (!canRun)
		{
			final Intent intent = new Intent(this, StatusActivity.class);
			intent.putExtra(Status.CANTRUN, true);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}

		// handle sent intent
		handleSearchIntent(getIntent());
	}

	@Override
	protected void onNewIntent(final Intent intent)
	{
		handleSearchIntent(intent);
	}

	// S E A R C H

	/**
	 * Handle intent dispatched by search view (either onCreate or onNewIntent if activity is single top)
	 *
	 * @param intent intent
	 */
	private void handleSearchIntent(final Intent intent)
	{
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

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		return MenuHandler.menuDispatch(this, item);
	}
}
