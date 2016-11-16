package org.sqlunet.browser;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.config.ManageActivity;
import org.sqlunet.browser.config.ManageFragment;
import org.sqlunet.browser.config.SettingsActivity;
import org.sqlunet.browser.config.SetupActivity;
import org.sqlunet.browser.config.SetupSqlActivity;
import org.sqlunet.browser.config.Status;
import org.sqlunet.browser.config.StorageActivity;
import org.sqlunet.browser.config.StorageFragment;
import org.sqlunet.predicatematrix.browser.PredicateMatrixActivity;
import org.sqlunet.predicatematrix.browser.PredicateMatrixFragment;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageSettings;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks
{
	static private final String TAG = "MainActivity";

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment navigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
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

		// get fragment
		this.navigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);

		// get title for use in restoreActionBar
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
	}

	@Override
	public void onNavigationDrawerItemSelected(final int position)
	{
		// update the browse content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container_content, PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void restoreActionBar()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(this.title);
	}

	/**
	 * Called on drawer selection
	 *
	 * @param number selected item number
	 */
	public void onSectionAttached(final int number)
	{
		final String[] options = getResources().getStringArray(R.array.title_sections);
		Intent intent = null;
		Fragment fragment = null;
		switch (number)
		{
			case 1:
				this.title = getString(R.string.title_home_section);
				fragment = new HomeFragment();
				break;
			case 2:
				this.title = getString(R.string.title_browse_section);
				intent = new Intent(this, BrowseActivity.class);
				//fragment = new BrowseFragment();
				break;
			case 3:
				this.title = getString(R.string.title_ts_section);
				intent = new Intent(this, TextSearchActivity.class);
				//fragment = new TextSearchFragment();
				break;
			case 4:
				this.title = getString(R.string.title_pm_section);
				intent = new Intent(this, PredicateMatrixActivity.class);
				//fragment = new PredicateMatrixFragment();
				break;
			case 5:
				this.title = getString(R.string.title_status_section);
				//intent = new Intent(this, StatusActivity.class);
				fragment = new StatusFragment();
				break;
			case 6:
				this.title = getString(R.string.title_manage_section);
				//intent = new Intent(this, ManageActivity.class);
				fragment = new ManageFragment();
				break;
			case 7:
				this.title = getString(R.string.title_storage_section);
				//intent = new Intent(this, StorageActivity.class);
				fragment = new StorageFragment();
				break;
			case 8:
				this.title = getString(R.string.title_settings_section);
				intent = new Intent(this, SettingsActivity.class);
				break;
		}
		if (fragment != null)
		{
			getFragmentManager().beginTransaction().add(R.id.container_content, fragment).commit();
			return;
		}
		if (intent != null)
		{
			startActivity(intent);
		}
	}

	// M E N U

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		Intent intent;

		// handle item selection
		switch (item.getItemId())
		{
			// settings

			case R.id.action_settings:
				intent = new Intent(this, SettingsActivity.class);
				break;

			// database

			case R.id.action_storage:
				intent = new Intent(this, StorageActivity.class);
				break;

			case R.id.action_status:
				intent = new Intent(this, StatusActivity.class);
				break;

			case R.id.action_manage:
				intent = new Intent(this, ManageActivity.class);
				break;

			case R.id.action_setup:
				intent = new Intent(this, SetupActivity.class);
				break;

			case R.id.action_setup_sql:
				intent = new Intent(this, SetupSqlActivity.class);
				break;

			// guide

			case R.id.action_help:
				intent = new Intent(this, HelpActivity.class);
				break;

			case R.id.action_about:
				intent = new Intent(this, AboutActivity.class);
				break;

			// lifecycle

			case R.id.action_quit:
				finish();
				return true;

			case R.id.action_appsettings:
				Settings.applicationSettings(this, "org.sqlunet.browser");
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}

		// start activity
		startActivity(intent);
		return true;
	}

	// P L A C E H O L D E R

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{
		/**
		 * The fragment argument representing the section number for this fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Constructor
		 */
		public PlaceholderFragment()
		{
		}

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(final int sectionNumber)
		{
			final PlaceholderFragment fragment = new PlaceholderFragment();
			final Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
		{
			return inflater.inflate(R.layout.fragment_main, container, false);
		}

		@Override
		public void onAttach(final Activity activity)
		{
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}
}
