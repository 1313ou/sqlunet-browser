package org.sqlunet.browser;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.contrib.NavigationDrawerFragment;
import android.util.Log;
import android.util.TypedValue;

import org.sqlunet.browser.config.SettingsActivity;
import org.sqlunet.browser.config.SetupActivity;
import org.sqlunet.browser.config.StorageFragment;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class NavigationFragment extends NavigationDrawerFragment implements NavigationDrawerFragment.Listener
{
	static private final String TAG = "NavigationFragment";

	static private final String[] fragmentTags = { //
			"home", // 0
			"browse", // 1
			"textsearch", // 2
			"predicatematrix", // 3
			"status", // 4
			null, // 5 setup
			"storage", // 6
			null, // 7 settings
			null, // 8 sql
			"help", // 9
			"about" // 10
	};

	static private final boolean[] fragmentTransient = { //
			true, // 0
			false, // 1 browse
			false, // 2 textsearch
			false, // 3 predicatematrix
			true, // 4
			true, // 5 setup: not a fragment
			true, // 6
			true, // 7 settings: not a fragment
			true, // 8 sql: not a fragment
			true, // 9
			true // 10
	};

	/**
	 * Constructor
	 */
	public NavigationFragment()
	{
		this.listener = this;
		// Log.d(TAG, "CONSTRUCTOR");
	}

	/*
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// Log.d(TAG, "ON CREATE");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Log.d(TAG, "ON CREATE VIEW");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart()
	{
		// Log.d(TAG, "ON START");
		super.onStart();
	}

	@Override
	public void onResume()
	{
		// Log.d(TAG, "ON RESUME");
		super.onResume();
	}

	@Override
	public void onPause()
	{
		// Log.d(TAG, "ON PAUSE");
		super.onPause();
	}

	@Override
	public void onStop()
	{
		// Log.d(TAG, "ON STOP");
		super.onStop();
	}

	@Override
	public void onDestroy()
	{
		// Log.d(TAG, "ON DESTROY");
		super.onDestroy();
	}

	@Override
	public void onDetach()
	{
		//Log.d(TAG, "ON DETACH");
		super.onDetach();
	}

	@Override
	public void onSaveInstanceState(final Bundle outState)
	{
		//Log.d(TAG, "ON SAVE INSTANCE STATE");
		super.onSaveInstanceState(outState);
	}
	*/

	/**
	 * Get active fragment
	 *
	 * @return active fragment
	 */
	public Fragment getActiveFragment()
	{
		final FragmentActivity activity = getActivity();
		final FragmentManager manager = activity.getSupportFragmentManager();

		final String tag = fragmentTags[this.selectedPosition];
		return tag == null ? null : manager.findFragmentByTag(tag);
	}

	/**
	 * Called when an item in the navigation drawer is selected.
	 *
	 * @param position position
	 */
	@Override
	public void onItemSelected(int position)
	{
		Log.d(TAG, "SELECTED " + position);
		if (tryActivity(position))
		{
			return;
		}

		updateFragments(position);
	}

	/**
	 * Drawer selection tryActivity
	 *
	 * @param number selected item number
	 */
	private boolean tryActivity(final int number)
	{
		Intent intent = null;
		switch (number)
		{
			case 5:
				intent = new Intent(getActivity(), SetupActivity.class);
				break;
			case 7:
				intent = new Intent(getActivity(), SettingsActivity.class);
				break;
			case 8:
				SqlDialogFragment.show(getActivity().getSupportFragmentManager());
				return true;
		}

		if (intent != null)
		{
			startActivity(intent);
			return true;
		}
		return false;
	}

	private void updateFragments(int position)
	{
		Log.d(TAG, "UPDATE SECTION FRAGMENTS " + position);
		final AppCompatActivity activity = (AppCompatActivity) getActivity();
		final FragmentManager manager = activity.getSupportFragmentManager();
		final FragmentTransaction transaction = manager.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

		final String tag = fragmentTags[position];
		if (tag == null)
		{
			// this position does not match a fragment
			return;
		}
		Fragment fragment = manager.findFragmentByTag(tag);
		if (fragment == null)
		{
			// add new one
			fragment = newFragment(position);
			transaction.add(R.id.container_content, fragment, tag);
		}
		else
		{
			// if it is transient
			if (fragmentTransient[position])
			{
				// remove
				transaction.remove(fragment);

				// add new one
				fragment = newFragment(position);
				transaction.add(R.id.container_content, fragment, tag);
			}
		}

		// hide others
		for (int i = 0; i < fragmentTags.length; i++)
		{
			if (i == position)
			{
				// already handled and further handled later
				continue;
			}
			final String tag2 = fragmentTags[i];
			if (tag2 == null)
			{
				// this position does not match a fragment
				continue;
			}
			final Fragment fragment2 = manager.findFragmentByTag(tag2);
			if (fragment2 != null)
			{
				if (fragmentTransient[i])
				{
					transaction.remove(fragment2);
				}
				else
				{
					transaction.hide(fragment2);
				}
			}
		}

		// show this fragment
		transaction.show(fragment);

		// complete transaction
		transaction.commit();

		// A C T I O N   B A R

		// action bar
		final ActionBar actionBar = activity.getSupportActionBar();
		assert actionBar != null;

		// action bar
		final ActionBarSetter setter = (ActionBarSetter) fragment;
		assert setter != null;
		if (!setter.setActionBar(actionBar, activity))
		{
			// theme
			final Resources.Theme theme = activity.getTheme();
			restoreActionBar(actionBar, theme);
		}
	}

	private Fragment newFragment(final int position)
	{
		switch (position)
		{
			case 0:
				return new HomeFragment();

			case 1:
				return new BrowseFragment();

			case 2:
				return new TextSearchFragment();

			case 3:
				return new BrowsePredicateMatrixFragment();

			case 4:
				return new StatusFragment();

			case 6:
				return new StorageFragment();

			case 8:
				return new SqlFragment();

			case 9:
				return new HelpFragment();

			case 10:
				return new AboutFragment();
		}
		return null;
	}


	/**
	 * Restore toolbar bar
	 */
	private static void restoreActionBar(final ActionBar actionBar, final Resources.Theme theme)
	{
		Log.d(NavigationFragment.TAG, "restore standard action bar");

		// res id of style pointed to from actionBarStyle
		final TypedValue typedValue = new TypedValue();
		theme.resolveAttribute(android.R.attr.actionBarStyle, typedValue, true);
		final int resId = typedValue.resourceId;

		// now get action bar style values
		final TypedArray style = theme.obtainStyledAttributes(resId, new int[]{android.R.attr.background});
		try
		{
			final Drawable drawable = style.getDrawable(0);
			actionBar.setBackgroundDrawable(drawable);
		}
		finally
		{
			style.recycle();
		}

		// options
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
	}

	@SuppressWarnings("unused")
	private String title(final int number)
	{
		final String[] options = getResources().getStringArray(R.array.title_sections);
		return options[number];
	}
}