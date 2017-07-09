package org.sqlunet.browser;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class NavigationFragment extends NavigationDrawerFragment implements NavigationDrawerFragment.Listener
{
	static private final String TAG = "NavigationFragment";

	/**
	 * Fragment classes
	 */
	private String[] fragmentClasses;

	/**
	 * Tags fragments are known under with FragmentManager
	 */
	private String[] fragmentTags;

	/**
	 * Whether fragments are recreated
	 */
	private int[] fragmentTransient;

	/**
	 * Constructor
	 */
	public NavigationFragment()
	{
		this.listener = this;
		// Log.d(TAG, "CONSTRUCTOR");
	}

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// read fragment data
		final Resources res = getResources();
		this.fragmentClasses = res.getStringArray(R.array.fragment_class_sections);
		this.fragmentTags = res.getStringArray(R.array.fragment_tag_sections);
		this.fragmentTransient = res.getIntArray(R.array.fragment_transient_flags);
	}

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
		Log.d(TAG, "Section selected " + position);
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
		Log.d(TAG, "Section fragments " + position);
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
			if (fragmentTransient[position] != 0)
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
				if (fragmentTransient[i] != 0)
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
			try
			{
				PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
				int themeResId = packageInfo.applicationInfo.theme;
				String name = getResources().getResourceEntryName(themeResId);
				Log.d(NavigationFragment.TAG, "Theme name " + name);
			}
			catch (PackageManager.NameNotFoundException e)
			{
				e.printStackTrace();
			}

			// theme
			final Resources.Theme theme = activity.getTheme();
			// theme.dump(Log.DEBUG, NavigationFragment.TAG, "THEME dump");
			restoreActionBar(actionBar, theme);
		}
	}

	/**
	 * Restore toolbar bar
	 */
	private static void restoreActionBar(final ActionBar actionBar, final Resources.Theme theme)
	{
		Log.d(NavigationFragment.TAG, "Restore standard action bar");

		/*
		final TypedValue style = new TypedValue();
		theme.resolveAttribute(R.attr.colorPrimary, style, true);
		int color = style.data;
		final Drawable drawable = new ColorDrawable(color);
		actionBar.setBackgroundDrawable(drawable);
		*/

		// res id of style pointed to from actionBarStyle
		final TypedValue typedValue = new TypedValue();
		theme.resolveAttribute(android.R.attr.actionBarStyle, typedValue, true);
		final int resId = typedValue.resourceId;
		Log.d(NavigationFragment.TAG, "ActionBarStyle ResId " + Integer.toHexString(resId));

		// now get action bar style values
		final TypedArray style = theme.obtainStyledAttributes(resId, new int[]{android.R.attr.background});

		//
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

	private Fragment newFragment(final int position)
	{
		final String fragmentClass = fragmentClasses[position];
		if (fragmentClass != null && !fragmentClass.isEmpty())
		{
			try
			{
				final Class<?> cl = Class.forName(fragmentClass);
				final Constructor<?> cons = cl.getConstructor();
				return (Fragment) cons.newInstance();
			}
			catch (ClassNotFoundException e)
			{
				Log.e(TAG, "Navigation fragment", e);
			}
			catch (NoSuchMethodException e)
			{
				Log.e(TAG, "Navigation fragment", e);
			}
			catch (java.lang.InstantiationException e)
			{
				Log.e(TAG, "Navigation fragment", e);
			}
			catch (IllegalAccessException e)
			{
				Log.e(TAG, "Navigation fragment", e);
			}
			catch (InvocationTargetException e)
			{
				Log.e(TAG, "Navigation fragment", e);
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private String title(final int number)
	{
		final String[] options = getResources().getStringArray(R.array.title_sections);
		return options[number];
	}
}