/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
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
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

/**
 * Base search fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class BaseSearchFragment extends Fragment implements SearchListener
{
	static private final String TAG = "BaseSearchF";

	/**
	 * Saved state of spinner
	 */
	static private final String STATE_SPINNER = "selected_mode";


	// C O M P O N E N T S

	/**
	 * Search view
	 */
	private SearchView searchView;

	/**
	 * Action bar mode spinner
	 */
	Spinner spinner;

	// R E S O U R C E S

	@LayoutRes
	int layoutId;

	@MenuRes
	int menuId;

	@AttrRes
	int colorAttrId;

	@ArrayRes
	int spinnerLabels;

	@ArrayRes
	int spinnerIcons;

	// C R E A T I O N

	/**
	 * Constructor
	 */
	public BaseSearchFragment()
	{
		Log.d(BaseSearchFragment.TAG, "constructor " + this);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		Log.d(BaseSearchFragment.TAG, "on create " + this + " from " + savedInstanceState);
		super.onCreate(savedInstanceState);
	}

	@SuppressWarnings("WeakerAccess")
	@Nullable
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		Log.d(BaseSearchFragment.TAG, "on create view " + this + " from " + savedInstanceState);

		// view
		return inflater.inflate(this.layoutId, container, false);
	}

	@Override
	public void onResume()
	{
		Log.d(BaseSearchFragment.TAG, "on resume " + this);
		super.onResume();

		setupActionBar();
	}

	// S A V E / R E S T O R E

	@SuppressWarnings("WeakerAccess")
	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		Log.d(BaseSearchFragment.TAG, "on save instance");

		// always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(outState);

		// spinner
		if (this.spinner != null)
		{
			// serialize the current dropdown position
			final int position = this.spinner.getSelectedItemPosition();
			outState.putInt(BaseSearchFragment.STATE_SPINNER, position);
		}
	}

	@Override
	public void onViewStateRestored(@Nullable final Bundle savedInstanceState)
	{
		super.onViewStateRestored(savedInstanceState);

		// restore from saved instance
		if (savedInstanceState != null && this.spinner != null)
		{
			final int selected = savedInstanceState.getInt(STATE_SPINNER);
			this.spinner.setSelection(selected);
		}
	}

	// M E N U

	@Override
	public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull final MenuInflater inflater)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		inflater.inflate(this.menuId, menu);

		// set up search
		setupSearch(menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	// A C T I O N B A R

	@SuppressWarnings({"SameReturnValue", "WeakerAccess"})
	@SuppressLint("InflateParams")
	public void setupActionBar()
	{
		Log.d(BaseSearchFragment.TAG, "set up specific action bar " + this);

		final AppCompatActivity activity = (AppCompatActivity) requireActivity();
		final ActionBar actionBar = activity.getSupportActionBar();
		assert actionBar != null;

		// title
		actionBar.setSubtitle(R.string.app_subname);

		// background
		final int color = ColorUtils.fetchColor(activity, this.colorAttrId);
		actionBar.setBackgroundDrawable(new ColorDrawable(color));

		// action bar customized view
		View customView = actionBar.getCustomView();
		if (customView == null)
		{
			customView = LayoutInflater.from(activity).inflate(R.layout.actionbar_custom, null);
			actionBar.setCustomView(customView);
		}

		// spinner
		this.spinner = customView.findViewById(R.id.spinner);
		setupSpinner();

		// set up the action bar to show a custom layout
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
	}

	// S P I N N E R

	/**
	 * Set up spinner
	 */
	@SuppressWarnings("WeakerAccess")
	protected void setupSpinner()
	{
		this.spinner.setVisibility(View.GONE);
	}

	/**
	 * Set up spinner
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
		if (this.spinner != null)
		{
			return this.spinner.getSelectedItemPosition();
		}

		final Settings.Selector selectorMode = Settings.Selector.getPref(requireContext());
		if (selectorMode != null)
		{
			return selectorMode.ordinal();
		}

		return 0;
	}

	// S E A R C H V I E W

	/**
	 * Set up search view
	 *
	 * @param menu menu
	 */
	private void setupSearch(@NonNull final Menu menu)
	{
		// menu item
		final MenuItem searchMenuItem = menu.findItem(R.id.search);

		// activity
		final Activity activity = requireActivity();

		// search info
		final ComponentName componentName = activity.getComponentName();
		final SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
		assert searchManager != null;
		final SearchableInfo searchableInfo = searchManager.getSearchableInfo(componentName);

		// search view
		this.searchView = (SearchView) searchMenuItem.getActionView();
		this.searchView.setSearchableInfo(searchableInfo);
		this.searchView.setIconifiedByDefault(true);
		this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
		{
			@Override
			public boolean onQueryTextSubmit(final String query)
			{
				clearQuery();
				return false;
			}

			@Override
			public boolean onQueryTextChange(final String newText)
			{
				return false;
			}
		});
		if (triggerFocusSearch())
		new Handler(Looper.getMainLooper()).postDelayed(() -> this.searchView.setIconified(false), 1500);
	}

	protected boolean triggerFocusSearch()
	{
		return true;
	}

	public void clearQuery()
	{
		if (this.searchView != null)
		{
			this.searchView.clearFocus();
			this.searchView.setFocusable(false);
			this.searchView.setQuery("", false);
			this.searchView.setIconified(true);
			closeKeyboard();
		}
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
}
