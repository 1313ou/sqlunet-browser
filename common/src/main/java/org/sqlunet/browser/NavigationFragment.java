package org.sqlunet.browser;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import org.sqlunet.browser.common.R;
import org.sqlunet.browser.config.SettingsActivity;
import org.sqlunet.browser.config.SetupActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.contrib.NavigationDrawerFragment;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class NavigationFragment extends NavigationDrawerFragment implements NavigationDrawerFragment.Listener
{
	static private final String TAG = "NavigationF";

	/**
	 * Handlers (private methods in this class
	 */
	private String[] handlers;

	/**
	 * Fragment classes
	 */
	private String[] fragmentClasses;

	/**
	 * Tags fragments are known under with FragmentManager
	 */
	private String[] fragmentTags;

	/**
	 * Whether fragments are recreated (1)
	 */
	private int[] fragmentTransient;

	/**
	 * Constructor
	 */
	@SuppressWarnings("WeakerAccess")
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
		this.handlers = res.getStringArray(R.array.handler_sections);
		this.fragmentClasses = res.getStringArray(R.array.fragment_class_sections);
		this.fragmentTags = res.getStringArray(R.array.fragment_tag_sections);
		this.fragmentTransient = res.getIntArray(R.array.fragment_transient_flags);
	}

	/**
	 * Get active fragment
	 *
	 * @return active fragment
	 */
	@Nullable
	public Fragment getActiveFragment()
	{
		final String tag = this.fragmentTags[this.selectedPosition];
		final FragmentManager manager = getFragmentManager();
		assert manager != null;
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
		if (handle(position))
		{
			return;
		}

		handleFragments(position);
	}

	/**
	 * Drawer selection handler
	 *
	 * @param position selected item number
	 * @return true if handled
	 */
	@SuppressWarnings("TryWithIdenticalCatches")
	private boolean handle(final int position)
	{
		final String run = handlers[position];
		if (run != null && !run.isEmpty())
		{
			try
			{
				final Method method = NavigationFragment.class.getDeclaredMethod(run);
				method.setAccessible(true);
				method.invoke(this);
			}
			catch (NoSuchMethodException e)
			{
				Log.e(NavigationFragment.TAG, "Handling " + position, e);
			}
			catch (InvocationTargetException e)
			{
				Log.e(NavigationFragment.TAG, "Handling " + position, e);
			}
			catch (IllegalAccessException e)
			{
				Log.e(NavigationFragment.TAG, "Handling " + position, e);
			}
			return true;
		}
		return false;
	}

	/**
	 * Setup handler (used by reflexion)
	 */
	@SuppressWarnings("unused")
	private void setupActivity()
	{
		final Intent intent = new Intent(requireContext(), SetupActivity.class);
		startActivity(intent);
	}

	/**
	 * Settings handler (used by reflexion)
	 */
	@SuppressWarnings("unused")
	private void settingsActivity()
	{
		final Intent intent = new Intent(requireContext(), SettingsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	/**
	 * SQL handler (used by reflexion)
	 */
	@SuppressWarnings("unused")
	private void sqlDialog()
	{
		final AppCompatActivity activity = (AppCompatActivity) requireActivity();
		SqlDialogFragment.show(activity.getSupportFragmentManager());
	}

	/**
	 * Handle fragments
	 *
	 * @param position drawer position
	 */
	private void handleFragments(int position)
	{
		Log.d(TAG, "Section fragments " + position);
		final FragmentManager manager = getFragmentManager();
		assert manager != null;
		final FragmentTransaction transaction = manager.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

		final String tag = this.fragmentTags[position];
		if (tag == null || tag.isEmpty())
		{
			// this position does not match a fragment
			return;
		}
		Fragment fragment = manager.findFragmentByTag(tag);
		if (fragment == null)
		{
			// add new one
			fragment = newFragment(position);
			assert fragment != null;
			transaction.add(R.id.container_content, fragment, tag);
		}
		else
		{
			// if it is transient
			if (this.fragmentTransient[position] != 0)
			{
				// remove
				transaction.remove(fragment);

				// add new one
				fragment = newFragment(position);
				assert fragment != null;
				transaction.add(R.id.container_content, fragment, tag);
			}
		}

		// hide others
		for (int i = 0; i < this.fragmentTags.length; i++)
		{
			if (i == position)
			{
				// already handled and further handled later
				continue;
			}
			final String tag2 = this.fragmentTags[i];
			if (tag2 == null)
			{
				// this position does not match a fragment
				continue;
			}
			final Fragment fragment2 = manager.findFragmentByTag(tag2);
			if (fragment2 != null)
			{
				if (this.fragmentTransient[i] != 0)
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

		final AppCompatActivity activity = (AppCompatActivity) requireActivity();

		// toolbar
		final Toolbar toolbar = activity.findViewById(R.id.toolbar);
		Log.d(TAG, "toolbar " + toolbar);

		// action bar
		final ActionBar actionBar = activity.getSupportActionBar();
		assert actionBar != null;
		final ActionBarSetter setter = (ActionBarSetter) fragment;
		if (!setter.setActionBar(actionBar, activity))
		{
			/*
			try
			{
				PackageInfo packageInfo = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), PackageManager.GET_META_DATA);
				int themeResId = packageInfo.applicationInfo.theme;
				String name = getResources().getResourceEntryName(themeResId);
				Log.d(NavigationFragment.TAG, "Theme name " + name);
			}
			catch (PackageManager.NameNotFoundException e)
			{
				e.printStackTrace();
			}
			*/

			// theme
			final Resources.Theme theme = activity.getTheme();
			// theme.dump(Log.DEBUG, NavigationFragment.TAG, "THEME dump");
			restoreActionBar(actionBar, theme);
		}
	}

	/**
	 * Restore action bar
	 */
	private static void restoreActionBar(@NonNull final ActionBar actionBar, @NonNull final Resources.Theme theme)
	{
		Log.d(NavigationFragment.TAG, "Restore standard action bar");

		// res id of style pointed to from actionBarStyle
		final TypedValue typedValue = new TypedValue();
		theme.resolveAttribute(android.R.attr.actionBarStyle, typedValue, true);
		final int resId = typedValue.resourceId;
		// Log.d(NavigationFragment.TAG, "ActionBarStyle ResId " + Integer.toHexString(resId));

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

	@SuppressWarnings("TryWithIdenticalCatches")
	private Fragment newFragment(final int position)
	{
		final String fragmentClass = this.fragmentClasses[position];
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

	@Override
	protected void set(@NonNull final View view, @NonNull final RowItem rowItem)
	{
		super.set(view, rowItem);

		final Context context = getContext();
		assert context != null;
		int[] colors = ColorUtils.getColors(context, R.color.drawer_main_fore_color, R.color.drawer_fore_color, R.color.accentColor);
		int color = colors[rowItem.isMain ? 2 : 1];

		final TextView textView = view.findViewById(android.R.id.text1);
		final Drawable drawable = textView.getCompoundDrawables()[0];
		ColorUtils.tint(color, drawable);
		textView.setTextColor(colors[rowItem.isMain ? 0 : 1]);
	}
}