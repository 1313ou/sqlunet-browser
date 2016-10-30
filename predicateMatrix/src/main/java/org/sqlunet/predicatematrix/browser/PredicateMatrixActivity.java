package org.sqlunet.predicatematrix.browser;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.sqlunet.Word;
import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.predicatematrix.R;
import org.sqlunet.predicatematrix.settings.Settings;
import org.sqlunet.provider.SqlUNetContract;

/**
 * Predicate Matrix activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateMatrixActivity extends Activity
{
	private static final String TAG = "PredicateMatrixActivity"; //

	/**
	 * PredicateMatrix mode spinner
	 */
	private Spinner spinner;

	/**
	 * QueryData
	 */
	private String query;

	/**
	 * Pointer
	 */
	private PmRolePointer pointer;

	/**
	 * Selector mode state
	 */
	private static final String STATE_SELECTED_PM_MODE = "org.sqlunet.browser.predicatematrix.mode.selected"; //

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content layout
		setContentView(R.layout.activity_predicatematrix);

		// show the Up button in the action bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;

		// set up the action bar to show a custom layout
		@SuppressLint("InflateParams") final View actionBarView = getLayoutInflater().inflate(R.layout.actionbar_custom, null);
		actionBar.setCustomView(actionBarView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		// actionBar.setDisplayShowCustomEnabled(true);
		// actionBar.setDisplayShowHomeEnabled(true);
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setDisplayShowTitleEnabled(false);

		// adapter range
		final CharSequence[] modes = getResources().getTextArray(R.array.pmmodes);

		// adapter
		SpinnerAdapter adapter = new ArrayAdapter<CharSequence>(this, R.layout.actionbar_item_pmmodes, modes)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				return getCustomView(position, convertView, parent, R.layout.actionbar_item_pmmodes);
			}

			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent)
			{
				return getCustomView(position, convertView, parent, R.layout.actionbar_item_pmmodes_dropdown);
			}

			private View getCustomView(int position, @SuppressWarnings("UnusedParameters") View convertView, ViewGroup parent, int layoutId)
			{
				final LayoutInflater inflater = getLayoutInflater();
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

		// spinner
		this.spinner = (Spinner) actionBarView.findViewById(R.id.spinner);

		// spinner listener
		this.spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position, final long id)
			{
				final Settings.PMMode mode = Settings.PMMode.values()[position];
				mode.setPref(PredicateMatrixActivity.this);

				Log.d(PredicateMatrixActivity.TAG, mode.name());

				// restart
				if (PredicateMatrixActivity.this.pointer != null)
				{
					handleSearch(PredicateMatrixActivity.this.pointer);
				}
				else
				{
					handleSearch(PredicateMatrixActivity.this.query);
				}
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parentView)
			{
				//
			}
		});

		// apply spinner adapter
		this.spinner.setAdapter(adapter);

		// saved mode
		final Settings.PMMode mode = Settings.PMMode.getPref(this);
		if (mode != null)
		{
			this.spinner.setSelection(mode.ordinal());
		}
	}

	@Override
	public void onSaveInstanceState(final Bundle savedInstanceState)
	{
		if (this.spinner == null)
		{
			return;
		}

		// serialize the current dropdown position
		final int position = this.spinner.getSelectedItemPosition();
		savedInstanceState.putInt(PredicateMatrixActivity.STATE_SELECTED_PM_MODE, position);

		// always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(final Bundle savedInstanceState)
	{
		if (this.spinner == null)
		{
			return;
		}

		// always call the superclass so it can restore the view hierarchy
		super.onRestoreInstanceState(savedInstanceState);

		// restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(PredicateMatrixActivity.STATE_SELECTED_PM_MODE))
		{
			this.spinner.setSelection(savedInstanceState.getInt(PredicateMatrixActivity.STATE_SELECTED_PM_MODE));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.predicate_matrix, menu);

		// set up search
		final MenuItem searchMenuItem = menu.findItem(R.id.searchView);
		setupSearch(searchMenuItem);

		return true;
	}

	// S E A R C H V I E W

	/**
	 * Associate the searchable configuration with the searchView (associate the searchable configuration with the searchView)
	 *
	 * @param searchMenuItem search menu item
	 */
	private void setupSearch(final MenuItem searchMenuItem)
	{
		final SearchView searchView = (SearchView) searchMenuItem.getActionView();
		final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
		searchView.setSearchableInfo(searchableInfo);
		// TODO
		searchView.setIconifiedByDefault(true);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
		{
			@Override
			public boolean onQueryTextSubmit(final String query)
			{
				searchView.clearFocus();
				searchView.setFocusable(false);
				searchView.setQuery("", false); //
				closeKeyboard();
				searchMenuItem.collapseActionView();

				handleSearch(query);
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
		// System.out.println("CLOSE");
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
		final android.view.View view = this.getCurrentFocus();
		if (view != null)
		{
			final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		handleIntent(getIntent());
	}

	// I N T E N T

	@Override
	protected void onNewIntent(final Intent intent)
	{
		handleIntent(intent);
	}

	/**
	 * Handle intent (either onCreate or if activity is single top)
	 *
	 * @param intent intent
	 */
	private void handleIntent(final Intent intent)
	{
		// get the intent action and get the query
		final String intentAction = intent.getAction();
		if (Intent.ACTION_SEARCH.equals(intentAction))
		{
			// arguments
			final String query = intent.getStringExtra(SearchManager.QUERY);
			handleSearch(query);
		}
		else
		{
			final Bundle args = intent.getExtras();
			if (args != null)
			{
				final int action = args.getInt(SqlUNetContract.ARG_QUERYACTION);
				if (SqlUNetContract.ARG_QUERYACTION_PM == action || SqlUNetContract.ARG_QUERYACTION_PMROLE == action)
				{
					final Parcelable pointer = args.getParcelable(SqlUNetContract.ARG_QUERYPOINTER);
					if (pointer instanceof PmRolePointer)
					{
						final PmRolePointer rolePointer = (PmRolePointer) pointer;
						handleSearch(rolePointer);
					}
				}
			}
		}
	}

	/**
	 * Handle search
	 *
	 * @param pointer query pointer
	 */
	private void handleSearch(final PmRolePointer pointer)
	{
		if (pointer == null)
		{
			return;
		}

		// status
		Log.d(PredicateMatrixActivity.TAG, "PredicateMatrix search " + pointer); //
		this.pointer = pointer;
		this.query = null;

		final Bundle args = new Bundle();
		args.putParcelable(SqlUNetContract.ARG_QUERYPOINTER, pointer);
		args.putInt(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_PM);

		// fragment
		final Fragment fragment = new PredicateMatrixFragment();
		fragment.setArguments(args);
		getFragmentManager().beginTransaction().replace(R.id.container_predicatematrix, fragment).commit();
	}

	/**
	 * Handle search
	 *
	 * @param query query string
	 */
	private void handleSearch(final String query)
	{
		if (query == null || query.isEmpty())
		{
			return;
		}

		// status
		Log.d(PredicateMatrixActivity.TAG, "PredicateMatrix search " + query); //
		this.query = query;
		this.pointer = null;

		// for fragment to handle
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

		final Bundle args = new Bundle();
		args.putParcelable(SqlUNetContract.ARG_QUERYPOINTER, pointer);
		args.putInt(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_PM);

		// fragment
		final Fragment fragment = new PredicateMatrixFragment();
		fragment.setArguments(args);
		getFragmentManager().beginTransaction().replace(R.id.container_predicatematrix, fragment).commit();
	}
}
