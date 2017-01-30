package org.sqlunet.browser;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.config.SettingsActivity;
import org.sqlunet.browser.config.SetupActivity;
import org.sqlunet.browser.config.Status;
import org.sqlunet.browser.config.StorageActivity;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageSettings;

public class MainActivity extends AppCompatActivity // implements NavigationFragment.Listener
{
	static private final String TAG = "MainActivity";

	private NavigationFragment navigationDrawerFragment;

	/**
	 * Used to store the last screen titleId. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence title;

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
		// final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
		// setSupportActionBar(toolbar);

		// get fragment
		this.navigationDrawerFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

		// get titleId for use in restoreActionBar
		this.title = getTitle();

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
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
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

	//	@Override
	//	public void onItemSelected(final int position, final boolean fromSavedInstanceState)
	//	{
	//		if (position == 8)
	//		{
	//			SqlDialogFragment.show(getSupportFragmentManager());
	//			return;
	//		}
	//
	//		if (!fromSavedInstanceState)
	//		{
	//			// update the browse content by replacing fragments
	//			getSupportFragmentManager() //
	//					.beginTransaction() //
	//					.replace(R.id.container_content, PlaceholderFragment.newInstance(position)) //
	//					.commit();
	//		}
	//	}

	//	public void restoreActionBar()
	//	{
	//		// action bar
	//		final ActionBar actionBar = getSupportActionBar();
	//		assert actionBar != null;
	//		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
	//		actionBar.setTitle(this.titleId);
	//
	//		// theme
	//		final Resources.Theme theme = getTheme();
	//
	//		// res id of style pointed to from actionBarStyle
	//		final TypedValue typedValue = new TypedValue();
	//		theme.resolveAttribute(android.R.attr.actionBarStyle, typedValue, true);
	//		int resId = typedValue.resourceId;
	//
	//		// now get action bar style values
	//		final TypedArray style = theme.obtainStyledAttributes(resId, new int[]{android.R.attr.background});
	//		try
	//		{
	//			final Drawable drawable = style.getDrawable(0);
	//			actionBar.setBackgroundDrawable(drawable);
	//		}
	//		finally
	//		{
	//			style.recycle();
	//		}
	//	}

	//	/**
	//	 * Called on drawer selection
	//	 *
	//	 * @param number selected item number
	//	 */
	//	private void onSectionAttached(final int number)
	//	{
	//		// final String[] options = getResources().getStringArray(R.array.title_sections);
	//		// this.titleId = options[number];
	//		this.fragment = null;
	//		Intent intent = null;
	//		switch (number)
	//		{
	//			case 0:
	//				this.titleId = getString(R.string.title_home_section);
	//				this.fragment = new HomeFragment();
	//				break;
	//			case 1:
	//				this.titleId = getString(R.string.title_browse_section);
	//				//intent = new Intent(this, BrowseActivity.class);
	//				this.fragment = new BrowseFragment();
	//				break;
	//			case 2:
	//				this.titleId = getString(R.string.title_ts_section);
	//				//intent = new Intent(this, TextSearchActivity.class);
	//				this.fragment = new TextSearchFragment();
	//				break;
	//			case 3:
	//				this.titleId = getString(R.string.title_pm_section);
	//				//intent = new Intent(this, PredicateMatrixActivity.class);
	//				this.fragment = new PredicateMatrixFragment();
	//				break;
	//			case 4:
	//				this.titleId = getString(R.string.title_status_section);
	//				//intent = new Intent(this, StatusActivity.class);
	//				this.fragment = new StatusFragment();
	//				break;
	//			case 5:
	//				this.titleId = getString(R.string.title_setup_section);
	//				intent = new Intent(this, SetupActivity.class);
	//				break;
	//			case 6:
	//				this.titleId = getString(R.string.title_storage_section);
	//				//intent = new Intent(this, StorageActivity.class);
	//				this.fragment = new StorageFragment();
	//				break;
	//			case 7:
	//				this.titleId = getString(R.string.title_settings_section);
	//				intent = new Intent(this, SettingsActivity.class);
	//				break;
	//			case 8:
	//				this.titleId = getString(R.string.title_sql_section);
	//				this.fragment = new SqlFragment();
	//				break;
	//			case 9:
	//				this.titleId = getString(R.string.title_help_section);
	//				//intent = new Intent(this, HelpActivity.class);
	//				this.fragment = new HelpFragment();
	//				break;
	//			case 10:
	//				this.titleId = getString(R.string.title_about_section);
	//				//intent = new Intent(this, SettingsActivity.class);
	//				this.fragment = new AboutFragment();
	//				break;
	//		}
	//		if (this.fragment != null)
	//		{
	//			getSupportFragmentManager() //
	//					.beginTransaction() //
	//					.replace(R.id.container_content, this.fragment) //
	//					.commit();
	//			return;
	//		}
	//		if (intent != null)
	//		{
	//			startActivity(intent);
	//		}
	//	}

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
		return menuDispatch(this, item);
	}

	/**
	 * Dispatch menu item action
	 *
	 * @param activity activity
	 * @param item     menu item
	 * @return true if processed/consumed
	 */
	static public boolean menuDispatch(final Activity activity, final MenuItem item)
	{
		Intent intent;

		// handle item selection
		switch (item.getItemId())
		{
			// settings

			case R.id.action_settings:
				intent = new Intent(activity, SettingsActivity.class);
				break;

			// database

			case R.id.action_storage:
				intent = new Intent(activity, StorageActivity.class);
				break;

			case R.id.action_status:
				intent = new Intent(activity, StatusActivity.class);
				break;

			case R.id.action_setup:
				intent = new Intent(activity, SetupActivity.class);
				break;

			// guide

			case R.id.action_help:
				intent = new Intent(activity, HelpActivity.class);
				break;

			case R.id.action_about:
				intent = new Intent(activity, AboutActivity.class);
				break;

			// lifecycle

			case R.id.action_quit:
				activity.finish();
				return true;

			case R.id.action_appsettings:
				Settings.applicationSettings(activity, "org.sqlunet.browser");
				return true;

			default:
				return false;
		}

		// start activity
		activity.startActivity(intent);
		return true;
	}
}
