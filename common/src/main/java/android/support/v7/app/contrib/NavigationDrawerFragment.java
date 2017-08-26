package android.support.v7.app.contrib;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.sqlunet.browser.common.R;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment
{
	static private final String TAG = "DrawerFragment";

	/**
	 * Listener interface that all activities using this fragment must implement
	 */
	public interface Listener
	{
		/**
		 * Called when an item in the navigation drawer is selected.
		 *
		 * @param position position
		 */
		void onItemSelected(int position);
	}

	static public class RowItem
	{
		public final int iconId;

		public final String title;

		public final boolean isMain;

		RowItem(final int iconId, final String title, final boolean isMain)
		{
			this.iconId = iconId;
			this.title = title;
			this.isMain = isMain;
		}
	}

	/**
	 * Remember the position of the selected item.
	 */
	static private final String STATE_SELECTED_SECTION = "selected_section";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the user manually expands it. This shared preference tracks this.
	 */
	static private final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * Listener instance
	 */
	protected Listener listener;

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
	 * Helper component that ties together the the proper interactions between the navigation drawer and the type bar app icon
	 */
	private ActionBarDrawerToggle drawerToggle;

	/**
	 * Current selected position
	 */
	protected int selectedPosition = 0;

	/**
	 * Whether was created from saved instance
	 */
	private boolean fromSavedInstanceState;

	/**
	 * Whether user has learned drawer operation
	 */
	private boolean userLearnedDrawer;

	/**
	 * Section swapInFlags
	 */
	private int[] swapInFlags;

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

		// read in the swap in flags
		final Resources res = getResources();
		this.swapInFlags = res.getIntArray(R.array.drawer_swap_in_flags);

		// read in the flag indicating whether or not the user has demonstrated awareness of the drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		this.userLearnedDrawer = prefs.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		// read saved instance data
		if (savedInstanceState != null)
		{
			this.selectedPosition = savedInstanceState.getInt(STATE_SELECTED_SECTION);
			this.fromSavedInstanceState = true;
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// resources
		final Resources res = getResources();

		// sections
		final String[] options = res.getStringArray(R.array.title_sections);
		int[] displayFlags = res.getIntArray(R.array.drawer_display_flags);
		final TypedArray icons = res.obtainTypedArray(R.array.drawer_icons);

		final RowItem[] items = new RowItem[options.length];
		for (int i = 0; i < options.length; i++)
		{
			items[i] = new RowItem(icons.getResourceId(i, -1), options[i], displayFlags[i] != 0);
		}
		icons.recycle();

		// adapter
		final ListAdapter adapter = new ArrayAdapter<RowItem>(getContext(), R.layout.drawer_item_simple, android.R.id.text1, items) //simple_list_item_activated_1
		{
			@NonNull
			@Override
			public View getView(final int position, final View convertView, @NonNull final ViewGroup parent)
			{
				final RowItem rowItem = getItem(position);
				assert rowItem != null;

				final View view = super.getView(position, convertView, parent);
				set(view, rowItem);
				return view;
			}
		};

		// set up the drawer's list view with items and click listener
		final View view = inflater.inflate(R.layout.drawer_main, container, false);
		this.drawerListView = (ListView) view.findViewById(R.id.sections);
		this.drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view0, int position, long id)
			{
				selectItem(position);
			}
		});
		this.drawerListView.setAdapter(adapter);
		this.drawerListView.setItemChecked(this.selectedPosition, true);
		return view;
	}

	protected void set(final View view, final RowItem rowItem)
	{
		final TextView textView = (TextView) view.findViewById(android.R.id.text1);
		textView.setTextColor(rowItem.isMain ? Color.WHITE : Color.LTGRAY);
		textView.setText(rowItem.title);
		textView.setCompoundDrawablesWithIntrinsicBounds(rowItem.iconId, 0, 0, 0);
		textView.setCompoundDrawablePadding(10);
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// indicate that this fragment would like to influence the set of actions in the type bar.
		setHasOptionsMenu(true);
	}

	// create/start/resume
	// (settings)->pause/stop/start/resume
	// (search new intent VIEW/SEARCH)->pause/resume
	@Override
	public void onStart()
	{
		super.onStart();
		selectItem(this.selectedPosition);
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

		// forward the new configuration to the drawer toggle component
		this.drawerToggle.onConfigurationChanged(newConfig);
	}

	// M E N U

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater)
	{
		// if the drawer is open, show the global app actions in the type bar. See also showGlobalContextActionBar, which controls the top-left area of the type bar.
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
		//noinspection SimplifiableIfStatement
		if (this.drawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// S E T  U P

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

		// type bar
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions between the navigation drawer and the type bar app icon.
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

		// if the user hasn't 'learned' _about the drawer, open it to introduce them to the drawer, per the navigation drawer design guidelines.
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
		this.drawerLayout.addDrawerListener(this.drawerToggle);
	}

	// S E L E C T

	/**
	 * Select item in list
	 *
	 * @param position position of selected item
	 */
	private void selectItem(final int position)
	{
		Log.d(TAG, "SELECT " + position);

		// record position
		if (this.swapInFlags[position] != 0)
		{
			this.selectedPosition = position;
			if (this.drawerListView != null)
			{
				this.drawerListView.setItemChecked(position, true);
			}
		}

		// close
		if (this.drawerLayout != null)
		{
			this.drawerLayout.closeDrawer(this.containerView);
		}

		// fire event
		if (this.listener != null)
		{
			this.listener.onItemSelected(position);
		}
	}

	// S T A T E

	/**
	 * Helper to report drawer state
	 *
	 * @return true if drawer is open
	 */
	@SuppressWarnings("WeakerAccess")
	public boolean isDrawerOpen()
	{
		return this.drawerLayout != null && this.drawerLayout.isDrawerOpen(this.containerView);
	}

	// A C T I O N   B A R

	/**
	 * Per the navigation drawer design guidelines, updates the type bar to show the global app 'textViewId', rather than just what's in the current screen.
	 */
	private void showGlobalContextActionBar()
	{
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		// actionBar.setTitle(R.string.app_name);
		// actionBar.setSubtitle(R.string.app_subname);
		// actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	/**
	 * Access to type bar
	 *
	 * @return type bar
	 */
	private ActionBar getSupportActionBar()
	{
		return ((AppCompatActivity) getActivity()).getSupportActionBar();
	}
}
