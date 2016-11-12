package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
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
public class TextSearchActivity extends Activity
{
	private static final String TAG = "TextSearchActivity";
	/**
	 * State
	 */
	private static final String STATE_SELECTED_TEXTSEARCH = "org.sqlunet.browser.textsearch.selected";
	/**
	 * Search view
	 */
	private SearchView searchView;

	/**
	 * Status view
	 */
	private TextView statusView;

	/**
	 * Spinner
	 */
	private Spinner spinner;

	/**
	 * Text searches
	 */
	private CharSequence[] textSearches;

	// C R E A T I O N

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// layout
		setContentView(R.layout.activity_textsearch);

		// get views from ids
		this.statusView = (TextView) findViewById(R.id.statusView);

		// show the Up button in the action bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;

		// set up the action bar to show a custom layout
		@SuppressLint("InflateParams") final View actionBarView = getLayoutInflater().inflate(R.layout.actionbar_custom, null);
		actionBar.setCustomView(actionBarView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		// actionBar.setDisplayShowCustomEnabled(true);
		// actionBar.setDisplayShowHomeEnabled(true);
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setDisplayShowTitleEnabled(false);

		// spinner adapter data
		this.textSearches = getResources().getTextArray(R.array.textsearches_names);

		// spinner adapter
		final SpinnerAdapter adapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_item_textsearches, this.textSearches)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				return getCustomView(position, convertView, parent, R.layout.spinner_item_textsearches);
			}

			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent)
			{
				return getCustomView(position, convertView, parent, R.layout.spinner_item_textsearches_dropdown);
			}

			private View getCustomView(int position, @SuppressWarnings("UnusedParameters") final View convertView, ViewGroup parent, int layoutId)
			{
				final LayoutInflater inflater = getLayoutInflater();
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
					label.setText(TextSearchActivity.this.textSearches[position]);
				}
				return row;
			}
		};

		// spinner
		this.spinner = (Spinner) actionBarView.findViewById(R.id.spinner);

		// spinner listener
		this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position, final long id)
			{
				Settings.setSearchModePref(TextSearchActivity.this, position);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parentView)
			{
				//
			}
		});

		// spinner adapter applied
		this.spinner.setAdapter(adapter);

		// spinner position
		final int position = Settings.getSearchModePref(this);
		this.spinner.setSelection(position);
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
		savedInstanceState.putInt(TextSearchActivity.STATE_SELECTED_TEXTSEARCH, position);

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
		if (savedInstanceState.containsKey(TextSearchActivity.STATE_SELECTED_TEXTSEARCH))
		{
			this.spinner.setSelection(savedInstanceState.getInt(TextSearchActivity.STATE_SELECTED_TEXTSEARCH));
		}
	}

	// M E N U

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.text_search, menu);

		// set up search view
		this.searchView = (SearchView) menu.findItem(R.id.searchView).getActionView();
		setupSearchView(this.searchView);
		return true;
	}

	// S E A R C H V I E W

	/**
	 * Associate the searchable configuration with the searchView (associate the searchable configuration with the searchView)
	 *
	 * @param searchView searchView
	 */
	private void setupSearchView(final SearchView searchView)
	{
		final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
		searchView.setSearchableInfo(searchableInfo);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
		{
			@Override
			public boolean onQueryTextSubmit(final String query)
			{
				searchView.clearFocus();
				searchView.setQuery("", false);
				handleSearch(query);
				return true;
			}

			@Override
			public boolean onQueryTextChange(final String newText)
			{
				return false;
			}
		});
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
		// get the action and get the query
		final String action = intent.getAction();
		if (Intent.ACTION_SEARCH.equals(action))
		{
			// arguments
			final String query = intent.getStringExtra(SearchManager.QUERY);
			handleSearch(query);
		}
		else if (Intent.ACTION_VIEW.equals(action))
		{
			final String query = intent.getStringExtra(SearchManager.QUERY);

			// suggestion selection (when a suggested item is selected)
			this.statusView.setText("view: '" + query + "'");
			this.searchView.setQuery(query, true); // submit
		}
	}

	/**
	 * Handle query
	 *
	 * @param query query
	 */
	private void handleSearch(final String query)
	{
		final int itemPosition = this.spinner.getSelectedItemPosition();

		// status
		Log.d(TextSearchActivity.TAG, "TEXT SEARCH " + query);
		this.statusView.setText("search: '" + query + "' " + this.textSearches[itemPosition]);

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

		// clear splash
		final ViewGroup container = (ViewGroup) findViewById(R.id.container_textsearch);
		container.removeAllViews();

		// for fragment to handle
		final Fragment fragment = new TextSearchFragment();
		fragment.setArguments(args);
		getFragmentManager().beginTransaction().replace(R.id.container_textsearch, fragment).commit();
	}
}
