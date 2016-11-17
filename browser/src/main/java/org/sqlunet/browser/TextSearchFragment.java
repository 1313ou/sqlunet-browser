package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import org.sqlunet.framenet.provider.FrameNetContract.Lookup_FnSentences_X;
import org.sqlunet.propbank.provider.PropBankContract.Lookup_PbExamples_X;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.settings.Settings;
import org.sqlunet.verbnet.provider.VerbNetContract.Lookup_VnExamples_X;
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Definitions;
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Samples;
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Words;

/**
 * Text search activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TextSearchFragment extends Fragment
{
	static private final String TAG = "TextSearchActivity";

	/**
	 * State of spinner
	 */
	static private final String STATE_SPINNER = "selected_textsearch_mode";

	/**
	 * Status view
	 */
	private TextView statusView;

	/**
	 * Search view
	 */
	private SearchView searchView;

	/**
	 * Spinner
	 */
	private Spinner spinner;

	// C R E A T I O N

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);

		// content
		final View view = inflater.inflate(R.layout.fragment_textsearch, container, false);

		// get views from ids
		this.statusView = (TextView) view.findViewById(R.id.statusView);

		// show the Up button in the action bar.
		final ActionBar actionBar = getActivity().getActionBar();
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

	@Override
	public void onSaveInstanceState(final Bundle savedInstanceState)
	{
		// always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);

		// spinner
		if (this.spinner != null)
		{
			// serialize the current dropdown position
			final int position = this.spinner.getSelectedItemPosition();
			savedInstanceState.putInt(TextSearchFragment.STATE_SPINNER, position);
		}
	}

	// M E N U

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater)
	{
		// inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.text_search, menu);

		// set up search view
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
				TextSearchFragment.this.searchView.clearFocus();
				TextSearchFragment.this.searchView.setFocusable(false);
				TextSearchFragment.this.searchView.setQuery("", false);
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

		// spinner adapter data
		final CharSequence[] textSearches = activity.getResources().getTextArray(R.array.textsearches_names);

		// spinner adapter
		final SpinnerAdapter adapter = new ArrayAdapter<CharSequence>(activity, R.layout.spinner_item_textsearches, textSearches)
		{
			@NonNull
			@Override
			public View getView(final int position, final View convertView, @NonNull final ViewGroup parent)
			{
				return getCustomView(position, convertView, parent, R.layout.spinner_item_textsearches);
			}

			@Override
			public View getDropDownView(final int position, final View convertView, @NonNull final ViewGroup parent)
			{
				return getCustomView(position, convertView, parent, R.layout.spinner_item_textsearches_dropdown);
			}

			private View getCustomView(final int position, @SuppressWarnings("UnusedParameters") final View convertView, final ViewGroup parent, final int layoutId)
			{
				final LayoutInflater inflater = activity.getLayoutInflater();
				final View row = inflater.inflate(layoutId, parent, false);
				final ImageView icon = (ImageView) row.findViewById(R.id.icon);
				int resId = 0;
				switch (position)
				{
					case 0:
						resId = R.drawable.ic_search_wnword;
						break;
					case 1:
						resId = R.drawable.ic_search_wndefinition;
						break;
					case 2:
						resId = R.drawable.ic_search_wnsample;
						break;
					case 3:
						resId = R.drawable.ic_search_vnexample;
						break;
					case 4:
						resId = R.drawable.ic_search_pbexample;
						break;
					case 5:
						resId = R.drawable.ic_search_fnsentence;
						break;
				}
				icon.setImageResource(resId);

				final TextView label = (TextView) row.findViewById(R.id.textsearch);
				if (label != null)
				{
					label.setText(textSearches[position]);
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
				Settings.setSearchModePref(activity, position);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parentView)
			{
				//
			}
		});

		// spinner adapter applied
		spinner.setAdapter(adapter);

		// spinner position
		final int position = Settings.getSearchModePref(activity);
		spinner.setSelection(position);
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
	 * Handle query
	 *
	 * @param query query
	 */
	public void search(final String query)
	{
		final int itemPosition = this.spinner.getSelectedItemPosition();

		// log
		Log.d(TextSearchFragment.TAG, "TEXT SEARCH " + query);

		// status
		final CharSequence[] textSearches = getResources().getTextArray(R.array.textsearches_names);
		this.statusView.setText("search: '" + query + "' " + textSearches[itemPosition]);

		// as per selected mode
		String searchUri;
		String id;
		String target;
		String[] columns;
		String[] hiddenColumns;
		String database = null;
		switch (itemPosition)
		{
			case 0:
				searchUri = Lookup_Words.CONTENT_URI;
				id = Lookup_Words.WORDID;
				target = Lookup_Words.LEMMA;
				columns = new String[]{Lookup_Words.LEMMA};
				hiddenColumns = new String[]{Lookup_Words.WORDID};
				break;
			case 1:
				searchUri = Lookup_Definitions.CONTENT_URI;
				id = Lookup_Definitions.SYNSETID;
				target = Lookup_Definitions.DEFINITION;
				columns = new String[]{Lookup_Definitions.DEFINITION};
				hiddenColumns = new String[]{Lookup_Definitions.SYNSETID};
				database = "wn";
				break;
			case 2:
				searchUri = Lookup_Samples.CONTENT_URI;
				id = Lookup_Samples.SYNSETID;
				target = Lookup_Samples.SAMPLE;
				columns = new String[]{Lookup_Samples.SAMPLE};
				hiddenColumns = new String[]{Lookup_Samples.SYNSETID};
				database = "wn";
				break;
			case 3:
				searchUri = Lookup_VnExamples_X.CONTENT_URI;
				id = Lookup_VnExamples_X.EXAMPLEID;
				target = Lookup_VnExamples_X.EXAMPLE;
				columns = new String[]{Lookup_VnExamples_X.EXAMPLE};
				hiddenColumns = new String[]{ //
						"GROUP_CONCAT(class || '@' || classid) AS " + Lookup_VnExamples_X.CLASSES};
				database = "vn";
				break;
			case 4:
				searchUri = Lookup_PbExamples_X.CONTENT_URI;
				id = Lookup_PbExamples_X.EXAMPLEID;
				target = Lookup_PbExamples_X.TEXT;
				columns = new String[]{Lookup_PbExamples_X.TEXT};
				hiddenColumns = new String[]{ //
						"GROUP_CONCAT(rolesetname ||'@'||rolesetid) AS " + Lookup_PbExamples_X.ROLESETS};
				database = "pb";
				break;
			case 5:
				searchUri = Lookup_FnSentences_X.CONTENT_URI;
				id = Lookup_FnSentences_X.SENTENCEID;
				target = Lookup_FnSentences_X.TEXT;
				columns = new String[]{Lookup_FnSentences_X.TEXT};
				hiddenColumns = new String[]{Lookup_FnSentences_X.SENTENCEID, //
						"GROUP_CONCAT(DISTINCT  frame || '@' || frameid) AS " + Lookup_FnSentences_X.FRAMES, //
						"GROUP_CONCAT(DISTINCT  lexunit || '@' || luid) AS " + Lookup_FnSentences_X.LEXUNITS};
				database = "fn";
				break;
			default:
				return;
		}

		// parameters
		final Bundle args = new Bundle();
		args.putString(ProviderArgs.ARG_QUERYURI, searchUri);
		args.putString(ProviderArgs.ARG_QUERYID, id);
		args.putStringArray(ProviderArgs.ARG_QUERYITEMS, columns);
		args.putStringArray(ProviderArgs.ARG_QUERYHIDDENITEMS, hiddenColumns);
		args.putString(ProviderArgs.ARG_QUERYFILTER, target + " MATCH ?");
		args.putString(ProviderArgs.ARG_QUERYARG, query);
		args.putInt(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table1);
		if (database != null)
		{
			args.putString(ProviderArgs.ARG_QUERYDATABASE, database);
		}

		// view
		final View view = getView();

		// clear splash
		assert view != null;
		final ViewGroup container = (ViewGroup) view.findViewById(R.id.container_textsearch);
		container.removeAllViews();

		// for fragment to handle
		final Fragment fragment = new TextSearchResultFragment();
		fragment.setArguments(args);
		getFragmentManager().beginTransaction().replace(R.id.container_textsearch, fragment).commit();
	}
}
