package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.sqlunet.Word;
import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.predicatematrix.settings.Settings;
import org.sqlunet.provider.ProviderArgs;

/**
 * PredicateMatrix fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateMatrixFragment extends Fragment implements SearchListener
{
	static private final String TAG = "PredicateMatrixFragment";

	/**
	 * State of spinner
	 */
	static private final String STATE_SPINNER = "selected_mode";

	/**
	 * Query
	 */
	private String query;

	/**
	 * Pointer
	 */
	private PmRolePointer pointer;

	/**
	 * Search view
	 */
	private SearchView searchView;

	/**
	 * Action bar mode spinner
	 */
	private Spinner spinner;

	/**
	 * Constructor
	 */
	public PredicateMatrixFragment()
	{
		//
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);

		// view
		final View view = inflater.inflate(R.layout.fragment_predicatematrix, container, false);

		// action bar
		this.spinner = setupActionBar(inflater);

		// spinner update
		if (savedInstanceState != null)
		{
			final int selected = savedInstanceState.getInt(STATE_SPINNER);
			this.spinner.setSelection(selected);
		}

		return view;
	}

	@Override
	public void onDetach()
	{
		restoreActionBar();
		super.onDetach();
	}

	// M E N U

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		inflater.inflate(R.menu.predicate_matrix, menu);

		// set up search
		setupSearch(menu);
	}

	// A C T I O N B A R

	/**
	 * Set up action bar
	 *
	 * @return spinner
	 */
	@TargetApi(Build.VERSION_CODES.M)
	private Spinner setupActionBar(final LayoutInflater inflater)
	{
		// activity
		final Activity activity = getActivity();

		// action bar
		final ActionBar actionBar = activity.getActionBar();
		assert actionBar != null;

		// color
		int color;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			color = getResources().getColor(R.color.predicatematrix_action_bar_color, activity.getTheme());
		}
		else
		{
			//noinspection deprecation
			color = getResources().getColor(R.color.predicatematrix_action_bar_color);
		}
		actionBar.setBackgroundDrawable(new ColorDrawable(color));

		// set up the type bar to show a custom layout
		@SuppressLint("InflateParams") //
		final View actionBarView = inflater.inflate(R.layout.actionbar_custom, null);
		actionBar.setCustomView(actionBarView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		// actionBar.setDisplayShowCustomEnabled(true);
		// actionBar.setDisplayShowHomeEnabled(true);
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setDisplayShowTitleEnabled(false);

		// spinner
		final Spinner spinner = (Spinner) actionBarView.findViewById(R.id.spinner);
		setupSpinner(spinner);

		return spinner;
	}

	/**
	 * Restore action bar
	 */
	private void restoreActionBar()
	{
		// activity
		final Activity activity = getActivity();

		// action bar
		final ActionBar actionBar = activity.getActionBar();
		assert actionBar != null;
		actionBar.setDisplayShowCustomEnabled(false);

		// theme
		final Resources.Theme theme = activity.getTheme();

		// res id of style pointed to from actionBarStyle
		final TypedValue typedValue = new TypedValue();
		theme.resolveAttribute(android.R.attr.actionBarStyle, typedValue, true);
		int resId = typedValue.resourceId;

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
	}

	// S E A R C H V I E W

	/**
	 * Set up search view
	 *
	 * @param menu menu
	 */
	private void setupSearch(final Menu menu)
	{
		// menu item
		final MenuItem searchMenuItem = menu.findItem(R.id.search);

		// activity
		final Activity activity = getActivity();

		// search info
		final ComponentName componentName = activity.getComponentName();
		final SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
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
				PredicateMatrixFragment.this.searchView.clearFocus();
				PredicateMatrixFragment.this.searchView.setFocusable(false);
				PredicateMatrixFragment.this.searchView.setQuery("", false);
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

		// view
		final View view = activity.getCurrentFocus();
		if (view != null)
		{
			final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	// S P I N N E R

	/**
	 * Set up type bar spinner
	 *
	 * @param spinner spinner
	 */
	private void setupSpinner(final Spinner spinner)
	{
		// activity
		final Activity activity = getActivity();

		// adapter values
		final CharSequence[] modes = activity.getResources().getTextArray(R.array.pmmodes);

		// adapter
		final SpinnerAdapter adapter = new ArrayAdapter<CharSequence>(activity, android.R.layout.simple_list_item_activated_1, android.R.id.text1, modes)
		{
			@NonNull
			@Override
			public View getView(final int position, final View convertView, @NonNull final ViewGroup parent)
			{
				final CharSequence rowItem = getItem(position);
				assert rowItem != null;

				final View view = super.getView(position, convertView, parent);
				final TextView textView = (TextView) view.findViewById(android.R.id.text1);
				textView.setText("");
				int resId = posToResId(position);
				textView.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);

				return view;
			}

			@Override
			public View getDropDownView(final int position, final View convertView, @NonNull final ViewGroup parent)
			{
				final CharSequence rowItem = getItem(position);
				assert rowItem != null;

				final View view = super.getDropDownView(position, convertView, parent);
				final TextView textView = (TextView) view.findViewById(android.R.id.text1);
				textView.setText(rowItem);
				int resId = posToResId(position);
				textView.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);

				return view;
			}

			private int posToResId(final int position)
			{
				int resId = 0;
				switch (position)
				{
					case 0: // ROLES
						resId = R.drawable.ic_roles_grouped;
						break;
					case 1: // ROWS_GROUPED_BY_ROLE
						resId = R.drawable.ic_rows_byrole;
						break;
					case 2: // ROWS_GROUPED_BY_SYNSET
						resId = R.drawable.ic_rows_bysynset;
						break;
					case 3: // ROWS
						resId = R.drawable.ic_rows_ungrouped;
						break;
				}
				return resId;
			}
		};

		// spinner listener
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position, final long id)
			{
				final Settings.PMMode mode = Settings.PMMode.values()[position];
				mode.setPref(activity);

				Log.d(PredicateMatrixFragment.TAG, mode.name());

				// restart
				if (PredicateMatrixFragment.this.pointer != null)
				{
					search(PredicateMatrixFragment.this.pointer);
				}
				else
				{
					search(PredicateMatrixFragment.this.query);
				}
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parentView)
			{
				//
			}
		});

		// apply spinner adapter
		spinner.setAdapter(adapter);

		// saved mode
		final Settings.PMMode mode = Settings.PMMode.getPref(activity);
		if (mode != null)
		{
			spinner.setSelection(mode.ordinal());
		}
	}

	// S A V E   R E S T O R E

	@Override
	public void onSaveInstanceState(final Bundle outState)
	{
		// always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(outState);

		// spinner
		if (this.spinner != null)
		{
			// serialize the current dropdown position
			final int position = this.spinner.getSelectedItemPosition();
			outState.putInt(PredicateMatrixFragment.STATE_SPINNER, position);
		}
	}

	// S E A R C H

	/**
	 * Handle search
	 *
	 * @param pointer query pointer
	 */
	public void search(final PmRolePointer pointer)
	{
		if (pointer == null)
		{
			return;
		}

		// log
		Log.d(PredicateMatrixFragment.TAG, "PM SEARCH " + pointer);

		// reset
		this.pointer = pointer;
		this.query = null;

		// arguments
		final Bundle args = new Bundle();
		args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);
		args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_PM);

		// view
		final View view = getView();

		// clear splash
		assert view != null;
		final ViewGroup container = (ViewGroup) view.findViewById(R.id.container_predicatematrix);
		container.removeAllViews();

		// fragment
		final Fragment fragment = new PredicateMatrixResultFragment();
		fragment.setArguments(args);
		getChildFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_predicatematrix, fragment) //
				.commit();
	}

	/**
	 * Handle search
	 *
	 * @param query query string
	 */
	@Override
	public void search(final String query)
	{
		if (query == null || query.isEmpty())
		{
			return;
		}

		// log
		Log.d(PredicateMatrixFragment.TAG, "PM SEARCH " + query);

		// reset
		this.query = query;
		this.pointer = null;

		// pointer
		Parcelable pointer;
		if (query.startsWith("#mr")) //
		{
			final long roleId = Long.parseLong(query.substring(3));
			pointer = new PmRolePointer(roleId);
		}
		else
		{
			pointer = new Word(query);
		}

		// arguments
		final Bundle args = new Bundle();
		args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);
		args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_PM);

		// view
		final View view = getView();

		// clear splash
		assert view != null;
		final ViewGroup container = (ViewGroup) view.findViewById(R.id.container_predicatematrix);
		assert container != null;
		container.removeAllViews();

		// fragment
		final Fragment fragment = new PredicateMatrixResultFragment();
		fragment.setArguments(args);
		getChildFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_predicatematrix, fragment) //
				.commit();
	}
}
