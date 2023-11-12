/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.sqlunet.browser.common.R;
import org.sqlunet.settings.Settings;

import androidx.annotation.ArrayRes;
import androidx.annotation.AttrRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

/**
 * Base search fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class BaseSearchFragment extends LoggingFragment implements SearchListener
{
	static private final String TAG = "BaseSearchF";

	/**
	 * Saved state of spinner
	 */
	static private final String STATE_SPINNER = "selected_mode";

	// Q U E R Y

	@Nullable
	protected String query;

	// C O M P O N E N T S

	/**
	 * Search view -held in search menuitem) that holds query
	 */
	@Nullable
	private SearchView searchView;

	/**
	 * Stored between onViewStateRestored and onResume
	 */
	private int spinnerPosition;

	// R E S O U R C E S

	@LayoutRes
	protected int layoutId;

	@MenuRes
	protected int menuId;

	@AttrRes
	protected int colorAttrId;

	@ArrayRes
	protected int spinnerLabels;

	@ArrayRes
	protected int spinnerIcons;

	// C R E A T I O N

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		final FragmentManager manager = getChildFragmentManager();
		manager.addOnBackStackChangedListener(() -> {

			int count = manager.getBackStackEntryCount();
			Log.d(TAG, "BackStack: " + count);
			final Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
			assert toolbar != null;
			if (count > 0)
			{
				toolbar.setSubtitle(query);
			}
			else
			{
				toolbar.setSubtitle(R.string.app_subname);
			}
		});
	}

	// V I E W

	@SuppressLint("InflateParams")
	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		return inflater.inflate(this.layoutId, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// menu provider
		final MenuProvider menuProvider = new MenuProvider()
		{
			@Override
			public void onCreateMenu(@NonNull final Menu menu, @NonNull final MenuInflater menuInflater)
			{
				// inflate
				menu.clear();
				menuInflater.inflate(R.menu.main_safedata, menu);
				menuInflater.inflate(menuId, menu);
				// MenuCompat.setGroupDividerEnabled(menu, true);
				Log.d(TAG, "MenuProvider: onCreateMenu() size=" + menu.size());

				// set up search view
				BaseSearchFragment.this.searchView = getSearchView(menu);
				assert BaseSearchFragment.this.searchView != null; // must have
				setupSearchView(BaseSearchFragment.this.searchView, getSearchInfo(requireActivity()));

				// toolbar
				final Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
				assert toolbar != null; // must have
				setupToolBar(toolbar);
			}

			@SuppressWarnings("deprecation")
			@Override
			public boolean onMenuItemSelected(@NonNull final MenuItem menuItem)
			{
				boolean handled = onOptionsItemSelected(menuItem);
				if (handled)
				{
					return true;
				}
				return MenuHandler.menuDispatch((AppCompatActivity) requireActivity(), menuItem);
			}
		};

		final MenuHost menuHost = requireActivity();
		menuHost.addMenuProvider(menuProvider, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		// spinner, added to toolbar if it does not have one
		Spinner spinner = ensureSpinner();
		// acquire it
		acquireSpinner(spinner);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		closeKeyboard();

		Spinner spinner = getSpinner();
		assert spinner != null;
		releaseSpinner(spinner);
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		final Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
		assert toolbar != null;
		toolbar.setSubtitle(R.string.app_subname);
	}

	// T O O L B A R

	/**
	 * Set up toolbar's custom view, its spinner, title, background
	 *
	 * @param toolbar toolbar
	 */
	@SuppressLint("InflateParams")
	public void setupToolBar(@NonNull final Toolbar toolbar)
	{
		Log.d(TAG, "Toolbar: set up in " + this);

		final AppCompatActivity activity = (AppCompatActivity) requireActivity();

		// title
		toolbar.setTitle(R.string.title_activity_browse);
		// toolbar.setSubtitle(R.string.app_subname);

		// background
		final int color = ColorUtils.fetchColor(activity, this.colorAttrId);
		toolbar.setBackground(new ColorDrawable(color));
	}

	// S E A R C H V I E W

	@Nullable
	private static SearchView getSearchView(@NonNull final Menu menu)
	{
		// menu item
		final MenuItem searchMenuItem = menu.findItem(R.id.search);
		if (searchMenuItem == null)
		{
			return null;
		}
		// search view
		return (SearchView) searchMenuItem.getActionView();
	}

	/**
	 * Set up search view
	 *
	 * @param searchView     search view
	 * @param searchableInfo searchable info
	 */
	private void setupSearchView(@NonNull final SearchView searchView, @Nullable final SearchableInfo searchableInfo)
	{
		// search view
		searchView.setSearchableInfo(searchableInfo);
		searchView.setIconifiedByDefault(true);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
		{
			@Override
			public boolean onQueryTextSubmit(final String query)
			{
				clearSearchView(searchView);
				closeKeyboard();
				return false;
			}

			@Override
			public boolean onQueryTextChange(final String newText)
			{
				return false;
			}
		});

		// trigger focus
		if (triggerFocusSearch())
		{
			new Handler(Looper.getMainLooper()).postDelayed(() -> searchView.setIconified(false), 1500);
		}
	}

	@Nullable
	private static SearchableInfo getSearchInfo(@NonNull final Activity activity)
	{
		final ComponentName componentName = activity.getComponentName();
		final SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
		assert searchManager != null;
		return searchManager.getSearchableInfo(componentName);
	}

	protected boolean triggerFocusSearch()
	{
		return true;
	}

	public void clearQuery()
	{
		if (this.searchView != null)
		{
			clearSearchView(this.searchView);
		}
		closeKeyboard();
	}

	private static void clearSearchView(@NonNull final SearchView searchView)
	{
		searchView.clearFocus();
		searchView.setFocusable(false);
		searchView.setQuery("", false);
		searchView.setIconified(true);
	}

	private void closeKeyboard()
	{
		// activity
		final Activity activity = requireActivity();

		// view
		final View view = activity.getCurrentFocus();
		if (view != null)
		{
			final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			assert imm != null;
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	// S P I N N E R

	/**
	 * Get toolbar's spinner
	 *
	 * @return spinner
	 */
	private @NonNull Spinner getSpinner()
	{
		final Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
		assert toolbar != null; // must have
		Spinner spinner = toolbar.findViewById(R.id.spinner);
		assert spinner != null; // must have
		return spinner;
	}

	/**
	 * Ensure toolbar has a spinner
	 *
	 * @return spinner
	 */
	private @NonNull Spinner ensureSpinner()
	{
		final Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
		assert toolbar != null; // must have
		Spinner spinner = toolbar.findViewById(R.id.spinner);
		if (spinner == null)
		{
			// toolbar customized view if toolbar does not already contain spinner
			final View customView = getLayoutInflater().inflate(R.layout.actionbar_custom, null); // raises "The specified child already has a parent" if toolbar
			toolbar.addView(customView);
			spinner = toolbar.findViewById(R.id.spinner);
			assert spinner != null; // because actionbar_custom has a @+id/spinner
		}
		return spinner;
	}

	/**
	 * Acquire the spinner
	 *
	 * @param spinner spinner
	 */
	protected void acquireSpinner(@NonNull final Spinner spinner)
	{
		// leave in limbo if spinner is not needed
		spinner.setSelection(this.spinnerPosition);
	}

	/**
	 * Release spinner
	 *
	 * @param spinner spinner
	 */
	private void releaseSpinner(@NonNull final Spinner spinner)
	{
		spinner.setSelection(0);
		spinner.setOnItemSelectedListener(null);
		spinner.setAdapter(null);
		spinner.setVisibility(View.GONE);
	}

	/**
	 * Build spinner adapter
	 */
	@NonNull
	@SuppressWarnings("WeakerAccess")
	protected BaseAdapter getSpinnerAdapter()
	{
		final Context context = requireContext();

		// resources
		final Resources resources = context.getResources();

		// adapter values and icons
		final CharSequence[] modeLabels = resources.getTextArray(this.spinnerLabels);
		final TypedArray array = resources.obtainTypedArray(this.spinnerIcons);
		int n = array.length();
		final int[] modeIcons = new int[n];
		for (int i = 0; i < n; i++)
		{
			modeIcons[i] = array.getResourceId(i, -1);
		}
		array.recycle();

		// adapter
		final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(context, R.layout.spinner_item_actionbar, android.R.id.text1, modeLabels)
		{
			@NonNull
			@Override
			public View getView(final int position, final View convertView, @NonNull final ViewGroup parent)
			{
				final CharSequence rowItem = getItem(position);
				assert rowItem != null;

				final View view = super.getView(position, convertView, parent);
				final TextView textView = view.findViewById(android.R.id.text1);
				textView.setText("");
				int resId = modeIcons[position];

				final int color = ColorUtils.fetchColor(context, R.attr.colorOnPrimary);
				final Drawable drawable = ColorUtils.getDrawable(context, resId);
				ColorUtils.tint(color, drawable);

				textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);

				return view;
			}

			@NonNull
			@Override
			public View getDropDownView(final int position, final View convertView, @NonNull final ViewGroup parent)
			{
				final CharSequence rowItem = getItem(position);
				assert rowItem != null;

				final View view = super.getDropDownView(position, convertView, parent);
				final TextView textView = view.findViewById(android.R.id.text1);
				textView.setText(rowItem);
				int resId = modeIcons[position];

				final int color = ColorUtils.fetchColor(context, R.attr.colorOnPrimary);
				final Drawable drawable = ColorUtils.getDrawable(context, resId);
				ColorUtils.tint(color, drawable);

				textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);

				return view;
			}
		};
		adapter.setDropDownViewResource(R.layout.spinner_item_actionbar_dropdown);

		return adapter;
	}

	/**
	 * Get search type position by peeking at spinner state or registry if spinner is still null
	 *
	 * @return search type position
	 */
	protected int getSearchModePosition()
	{
		Spinner spinner = getSpinner();
		if (spinner != null)
		{
			return spinner.getSelectedItemPosition();
		}

		final Settings.Selector selectorMode = Settings.Selector.getPref(requireContext());
		return selectorMode.ordinal();
	}

	// S E A R C H   L I S T E N E R
	public void search(final String query)
	{
		this.query = query;

		// subtitle
		// final Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
		// assert toolbar != null;
		// toolbar.setSubtitle(query);
	}

	// S A V E / R E S T O R E

	@SuppressWarnings("WeakerAccess")
	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		// always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(outState);

		// spinner
		Spinner spinner = getSpinner();
		if (spinner != null)
		{
			// serialize the current dropdown position
			final int position = spinner.getSelectedItemPosition();
			outState.putInt(BaseSearchFragment.STATE_SPINNER, position);
		}
	}

	@Override
	public void onViewStateRestored(@Nullable final Bundle savedInstanceState)
	{
		super.onViewStateRestored(savedInstanceState);

		// restore from saved instance
		if (savedInstanceState != null)
		{
			final int selected = savedInstanceState.getInt(STATE_SPINNER);
			this.spinnerPosition = selected;
		}
	}

	// F R A G M E N T   M A N A G E M E N T

	/**
	 * Remove children fragments with tags and insert given fragment with at given location
	 *
	 * @param fragment          new fragment
	 * @param tag               new fragment's tag
	 * @param where             new fragment's location
	 * @param childFragmentTags removed children's tags
	 * @noinspection SameParameterValue, EmptyMethod
	 */
	protected void beforeSaving(final Fragment fragment, @Nullable final String tag, @IdRes final int where, final String... childFragmentTags)
	{
		// FragmentUtils.removeAllChildFragment(getChildFragmentManager(), fragment, tag, where, childFragmentTags);
	}
}
