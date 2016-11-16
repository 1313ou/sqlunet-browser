package org.sqlunet.predicatematrix.browser;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.sqlunet.Word;
import org.sqlunet.browser.Module;
import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.predicatematrix.R;
import org.sqlunet.predicatematrix.loaders.PredicateRoleFromWordModule;
import org.sqlunet.predicatematrix.loaders.PredicateRoleModule;
import org.sqlunet.predicatematrix.settings.Settings;
import org.sqlunet.predicatematrix.style.PredicateMatrixFactories;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.control.IconTreeController;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;

/**
 * PredicateMatrix fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateMatrixFragment extends Fragment
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
	 * Status view
	 */
	private TextView statusView;

	/**
	 * Action bar mode spinner
	 */
	private Spinner spinner;

	/**
	 * Constructor
	 */
	public PredicateMatrixFragment()
	{
		Log.d(TAG,"CREATE");
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);

		// view
		final View view = inflater.inflate(R.layout.fragment_predicatematrix, container, false);

		// activity
		final Activity activity = getActivity();

		// status view
		this.statusView = (TextView) view.findViewById(R.id.statusView);

		// show the Up button in the action bar.
		final ActionBar actionBar = activity.getActionBar();
		assert actionBar != null;

		// set up the action bar to show a custom layout
		@SuppressLint("InflateParams") //
		final View actionBarView = inflater.inflate(R.layout.actionbar_custom, null);
		actionBar.setCustomView(actionBarView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		// actionBar.setDisplayShowCustomEnabled(true);
		// actionBar.setDisplayShowHomeEnabled(true);
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setDisplayShowTitleEnabled(false);

		// spinner
		this.spinner = (Spinner) actionBarView.findViewById(R.id.spinner);
		setupSpinner(this.spinner);
		if (savedInstanceState != null)
		{
			final int selected = savedInstanceState.getInt(STATE_SPINNER);
			this.spinner.setSelection(selected);
		}

		return view;
	}

	// M E N U

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.predicate_matrix, menu);

		// set up search
		final MenuItem searchMenuItem = menu.findItem(R.id.searchView);
		setupSearch(searchMenuItem);
	}

	// S E A R C H V I E W

	/**
	 * Set up searchView
	 *
	 * @param searchMenuItem search menu item
	 */
	private void setupSearch(final MenuItem searchMenuItem)
	{
		// activity
		final Activity activity = getActivity();

		// search view
		this.searchView = (SearchView) searchMenuItem.getActionView();
		final SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
		final SearchableInfo searchableInfo = searchManager.getSearchableInfo(activity.getComponentName());
		this.searchView.setSearchableInfo(searchableInfo);
		// TODO
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

				search(query);
				return true;
			}

			@Override
			public boolean onQueryTextChange(final String newText)
			{
				return false;
			}
		});

		// TODO Won't work
		// searchView.setOnCloseListener(new SearchView.OnCloseListener()
		// {
		// @Override
		// public boolean onClose()
		// {
		// searchView.clearFocus();
		// searchView.setFocusable(false);
		// searchView.setQuery("", false);
		// searchMenuItem.collapseActionView();
		// return false;
		// }
		// });
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
	 * Set up action bar spinner
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
		SpinnerAdapter adapter = new ArrayAdapter<CharSequence>(activity, R.layout.spinner_item_pmmodes, modes)
		{
			@Override
			public View getView(final int position, final View convertView, final ViewGroup parent)
			{
				return getCustomView(position, convertView, parent, R.layout.spinner_item_pmmodes);
			}

			@Override
			public View getDropDownView(final int position, final View convertView, final ViewGroup parent)
			{
				return getCustomView(position, convertView, parent, R.layout.spinner_item_pmmodes_dropdown);
			}

			private View getCustomView(final int position, @SuppressWarnings("UnusedParameters") final View convertView, ViewGroup parent, int layoutId)
			{
				final LayoutInflater inflater = activity.getLayoutInflater();
				final View row = inflater.inflate(layoutId, parent, false);
				final ImageView icon = (ImageView) row.findViewById(R.id.icon);
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
				icon.setImageResource(resId);

				final TextView label = (TextView) row.findViewById(R.id.pmmode);
				if (label != null)
				{
					label.setText(modes[position]);
				}
				return row;
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

	// S U G G E S T

	/**
	 * Handle suggestion
	 *
	 * @param query query
	 */
	public void suggest(final String query)
	{
		Log.d(TAG, "SUGGEST " + query);
		this.statusView.setText("view: '" + query + "'");
		this.searchView.setQuery(query, true); // true=submit
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

		// status
		Log.d(PredicateMatrixFragment.TAG, "Search " + pointer);
		this.pointer = pointer;
		this.query = null;

		final Bundle args = new Bundle();
		args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);
		args.putInt(ProviderArgs.ARG_QUERYACTION, ProviderArgs.ARG_QUERYACTION_PM);

		// view
		final View view = getView();

		// clear
		final ViewGroup container = (ViewGroup) view.findViewById(R.id.container_predicatematrix);
		container.removeAllViews();

		// fragment
		final Fragment fragment = new PredicateMatrixResultFragment();
		fragment.setArguments(args);
		getFragmentManager().beginTransaction().replace(R.id.container_predicatematrix, fragment).commit();
	}

	/**
	 * Handle search
	 *
	 * @param query query string
	 */
	public void search(final String query)
	{
		if (query == null || query.isEmpty())
		{
			return;
		}

		// status
		Log.d(PredicateMatrixFragment.TAG, "Search " + query);
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
		args.putInt(ProviderArgs.ARG_QUERYACTION, ProviderArgs.ARG_QUERYACTION_PM);

		// view
		final View view = getView();

		// clear
		final ViewGroup container = (ViewGroup) view.findViewById(R.id.container_predicatematrix);
		assert container != null;
		container.removeAllViews();

		// fragment
		final Fragment fragment = new PredicateMatrixResultFragment();
		fragment.setArguments(args);
		getFragmentManager().beginTransaction().replace(R.id.container_predicatematrix, fragment).commit();
	}
}
