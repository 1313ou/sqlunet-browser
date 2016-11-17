package org.sqlunet.browser;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment
{
	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_SECTION = "selected_section";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the user manually
	 * expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks callbacks;

	/**
	 * Fragment container view
	 */
	private View containerView;

	/**
	 * Drawer list view
	 */
	private ListView drawerListView;

	/**
	 * Drawer layout
	 */
	private DrawerLayout drawerLayout;

	/**
	 * Helper component that ties together the the proper interactions between the navigation drawer and the action bar app icon
	 */
	private ActionBarDrawerToggle drawerToggle;

	/**
	 * Current selected position
	 */
	private int selectedPosition = 0;

	/**
	 * Whether was created from saved instance
	 */
	private boolean fromSavedInstanceState;

	/**
	 * Whether user has learned drawer operation
	 */
	private boolean userLearnedDrawer;

	/**
	 * Constructor
	 */
	public NavigationDrawerFragment()
	{
	}

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// read in the flag indicating whether or not the user has demonstrated awareness of the drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		this.userLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		// read saved instance data
		if (savedInstanceState != null)
		{
			this.selectedPosition = savedInstanceState.getInt(STATE_SELECTED_SECTION);
			this.fromSavedInstanceState = true;
		}

		// select either the default item (0) or the last selected item.
		selectItem(this.selectedPosition);
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// sections
		final String[] options = getResources().getStringArray(R.array.title_sections);

		// set up the drawer's list view with items and click listener
		this.drawerListView = (ListView) inflater.inflate(R.layout.drawer_main, container, false);
		this.drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				selectItem(position);
			}
		});
		this.drawerListView.setAdapter(new ArrayAdapter<>(getActionBar().getThemedContext(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, options));
		this.drawerListView.setItemChecked(this.selectedPosition, true);
		return this.drawerListView;
	}

	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 *
	 * @param fragmentId   The android:id of this fragment in its activity's layout.
	 * @param drawerLayout The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(final int fragmentId, final DrawerLayout drawerLayout)
	{
		this.containerView = getActivity().findViewById(fragmentId);

		// drawer layout
		this.drawerLayout = drawerLayout;

		// set a custom shadow that overlays the browse content when the drawer opens
		this.drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// action bar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions between the navigation drawer and the action bar app icon.
		this.drawerToggle = new ActionBarDrawerToggle(getActivity(), // host Activity
				NavigationDrawerFragment.this.drawerLayout, // DrawerLayout object
				// R.drawable.ic_drawer, // nav drawer image to replace 'Up' caret
				R.string.hint_navigation_drawer_open,  // "open drawer" description for accessibility
				R.string.hint_navigation_drawer_close) // "close drawer" description for accessibility
		{
			@Override
			public void onDrawerClosed(View drawerView)
			{
				super.onDrawerClosed(drawerView);
				if (!isAdded())
				{
					return;
				}

				getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);
				if (!isAdded())
				{
					return;
				}

				if (!NavigationDrawerFragment.this.userLearnedDrawer)
				{
					// The user manually opened the drawer; store this flag to prevent auto-showing the navigation drawer automatically in the future.
					NavigationDrawerFragment.this.userLearnedDrawer = true;
					SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
					sharedPreferences.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
				}

				getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}
		};

		// if the user hasn't 'learned' about the drawer, open it to introduce them to the drawer, per the navigation drawer design guidelines.
		if (!this.userLearnedDrawer && !this.fromSavedInstanceState)
		{
			this.drawerLayout.openDrawer(this.containerView);
		}

		// defer code dependent on restoration of previous instance state.
		this.drawerLayout.post(new Runnable()
		{
			@Override
			public void run()
			{
				NavigationDrawerFragment.this.drawerToggle.syncState();
			}
		});

		// listener
		this.drawerLayout.setDrawerListener(this.drawerToggle);
	}

	/**
	 * Select item in list
	 *
	 * @param position position of selected item
	 */
	private void selectItem(final int position)
	{
		this.selectedPosition = position;
		if (this.drawerListView != null)
		{
			this.drawerListView.setItemChecked(position, true);
		}
		if (this.drawerLayout != null)
		{
			this.drawerLayout.closeDrawer(this.containerView);
		}
		if (this.callbacks != null)
		{
			this.callbacks.onNavigationDrawerItemSelected(position);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onAttach(final Activity activity)
	{
		super.onAttach(activity);
		try
		{
			this.callbacks = (NavigationDrawerCallbacks) activity;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onAttach(final Context context)
	{
		super.onAttach(context);
		try
		{
			this.callbacks = (NavigationDrawerCallbacks) context;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		this.callbacks = null;
	}

	@Override
	public void onSaveInstanceState(final Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_SECTION, this.selectedPosition);
	}

	@Override
	public void onConfigurationChanged(final Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);

		// forward the new configuration the drawer toggle component.
		this.drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater)
	{
		// if the drawer is open, show the global app actions in the action bar. See also showGlobalContextActionBar, which controls the top-left area of the action bar.
		if (this.drawerLayout != null && isDrawerOpen())
		{
			inflater.inflate(R.menu.global, menu);
			showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (this.drawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to show the global app 'context', rather than just what's in the current screen.
	 */
	private void showGlobalContextActionBar()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	/**
	 * Access to action bar
	 *
	 * @return action bar
	 */
	private ActionBar getActionBar()
	{
		return getActivity().getActionBar();
	}

	/**
	 * Helper to report drawer state
	 *
	 * @return true if drawer is open
	 */
	public boolean isDrawerOpen()
	{
		return this.drawerLayout != null && this.drawerLayout.isDrawerOpen(this.containerView);
	}

	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	public interface NavigationDrawerCallbacks
	{
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position);
	}
}
