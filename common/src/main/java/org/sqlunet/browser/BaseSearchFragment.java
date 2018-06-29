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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.sqlunet.browser.common.R;

/**
 * Base search fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class BaseSearchFragment extends NavigableFragment implements SearchListener
{
	static private final String TAG = "BaseSearchFragment";

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

	int layoutId;

	int menuId;

	int colorId;

	int spinnerLabels;

	int spinnerIcons;

	// C R E A T I O N

	/**
	 * Constructor
	 */
	public BaseSearchFragment()
	{
		Log.d(BaseSearchFragment.TAG, "constructor " + this);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		Log.d(BaseSearchFragment.TAG, "on create " + this + " from " + savedInstanceState);
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		Log.d(BaseSearchFragment.TAG, "on create view " + this + " from " + savedInstanceState);

		setHasOptionsMenu(true);

		// view
		return inflater.inflate(this.layoutId, container, false);
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

	// S A V E

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

	@SuppressLint("InflateParams")
	@Override
	public boolean setActionBar(@NonNull final ActionBar actionBar, @NonNull final Context context)
	{
		Log.d(BaseSearchFragment.TAG, "set up specific action bar " + this);

		// title
		actionBar.setTitle(this.titleId);
		actionBar.setSubtitle(R.string.app_subname);

		// background
		final int color = ColorUtils.getColor(context, this.colorId);
		actionBar.setBackgroundDrawable(new ColorDrawable(color));

		// action bar customized view
		View customView = actionBar.getCustomView();
		if (customView == null)
		{
			customView = LayoutInflater.from(context).inflate(R.layout.actionbar_custom, null);
			actionBar.setCustomView(customView);
		}
		this.spinner = customView.findViewById(R.id.spinner);

		// spinner
		setupSpinner(context);

		// set up the action bar to show a custom layout
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		// actionBar.setDisplayShowCustomEnabled(true);
		// actionBar.setDisplayShowHomeEnabled(true);
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setDisplayShowTitleEnabled(false);

		return true;
	}

	@SuppressWarnings("UnusedReturnValue")
	public boolean setActionBarUpDisabled(@NonNull final ActionBar actionBar, @NonNull final Context context)
	{
		boolean result = setActionBar(actionBar, context);
		actionBar.setDisplayHomeAsUpEnabled(false);
		return result;
	}

	// S P I N N E R

	/**
	 * Set up spinner
	 */
	void setupSpinner(@NonNull final Context context)
	{
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
		final ArrayAdapter adapter = new ArrayAdapter<CharSequence>(context, R.layout.spinner_item_actionbar, android.R.id.text1, modeLabels)
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

				final int color = ColorUtils.getColor(context, R.color.actionbar_fore_color);
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

				final int color = ColorUtils.getColor(context, R.color.actionbar_fore_color);
				final Drawable drawable = ColorUtils.getDrawable(context, resId);
				ColorUtils.tint(color, drawable);

				textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null,  null);

				return view;
			}
		};
		adapter.setDropDownViewResource(R.layout.spinner_item_actionbar_dropdown);

		// apply spinner adapter
		this.spinner.setAdapter(adapter);
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
		final AppCompatActivity activity = (AppCompatActivity) getActivity();
		assert activity != null;

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
				BaseSearchFragment.this.searchView.clearFocus();
				BaseSearchFragment.this.searchView.setFocusable(false);
				BaseSearchFragment.this.searchView.setQuery("", false);
				closeKeyboard();
				searchMenuItem.collapseActionView();
				return false;
			}

			@Override
			public boolean onQueryTextChange(final String newText)
			{
				return false;
			}
		});
	}

	private void closeKeyboard()
	{
		// activity
		final Activity activity = getActivity();
		assert activity != null;

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
