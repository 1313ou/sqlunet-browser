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
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import org.sqlunet.browser.config.TableActivity;
import org.sqlunet.browser.selector.SelectorActivity;
import org.sqlunet.browser.web.WebActivity;
import org.sqlunet.browser.xselector.XSelectorActivity;
import org.sqlunet.framenet.FnAnnoSetPointer;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.framenet.FnLexUnitPointer;
import org.sqlunet.framenet.FnPatternPointer;
import org.sqlunet.framenet.FnSentencePointer;
import org.sqlunet.framenet.FnValenceUnitPointer;
import org.sqlunet.framenet.browser.FnAnnoSetActivity;
import org.sqlunet.framenet.browser.FnFrameActivity;
import org.sqlunet.framenet.browser.FnLexUnitActivity;
import org.sqlunet.framenet.browser.FnSentenceActivity;
import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.predicatematrix.browser.PredicateMatrixActivity;
import org.sqlunet.propbank.PbRoleSetPointer;
import org.sqlunet.propbank.browser.PbRoleSetActivity;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.settings.Settings;
import org.sqlunet.verbnet.VnClassPointer;
import org.sqlunet.verbnet.browser.VnClassActivity;
import org.sqlunet.wordnet.SynsetPointer;
import org.sqlunet.wordnet.browser.SynsetActivity;
import org.sqlunet.wordnet.provider.WordNetContract.AdjPositionTypes;
import org.sqlunet.wordnet.provider.WordNetContract.LexDomains;
import org.sqlunet.wordnet.provider.WordNetContract.LinkTypes;
import org.sqlunet.wordnet.provider.WordNetContract.PosTypes;

/**
 * Browse fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BrowseFragment extends Fragment
{
	static private final String TAG = "BrowseFragment";

	/**
	 * Selector mode state
	 */
	static private final String STATE_SPINNER = "org.sqlunet.browser.selector.selected";

	/**
	 * Search view
	 */
	private SearchView searchView;

	/**
	 * Status view
	 */
	private TextView statusView;

	/**
	 * Selector mode spinner
	 */
	private Spinner spinner;

	// C R E A T I O N

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);

		// view
		final View view = inflater.inflate(R.layout.fragment_browse, container, false);

		// activity
		final Activity activity = getActivity();

		// status view
		this.statusView = (TextView) view.findViewById(R.id.statusView);

		// show the Up button in the action bar.
		final ActionBar actionBar = activity.getActionBar();
		assert actionBar != null;

		// set up the action bar to show a custom layout
		@SuppressLint("InflateParams") final View actionBarView = inflater.inflate(R.layout.actionbar_custom, null);
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
		// inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.browse, menu);

		// set up search view
		final MenuItem searchMenuItem = menu.findItem(R.id.searchView);
		setupSearch(searchMenuItem);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		// activity
		final Activity activity = getActivity();

		// intent
		Intent intent;

		// handle item selection
		switch (item.getItemId())
		{
			case R.id.action_table_lexdomains:
				intent = new Intent(activity, TableActivity.class);
				intent.putExtra(ProviderArgs.ARG_QUERYURI, LexDomains.CONTENT_URI);
				intent.putExtra(ProviderArgs.ARG_QUERYID, LexDomains.LEXDOMAINID);
				intent.putExtra(ProviderArgs.ARG_QUERYITEMS, new String[]{LexDomains.LEXDOMAINID, LexDomains.LEXDOMAIN, LexDomains.POS});
				intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table3);
				break;

			case R.id.action_table_postypes:
				intent = new Intent(activity, TableActivity.class);
				intent.putExtra(ProviderArgs.ARG_QUERYURI, PosTypes.CONTENT_URI);
				intent.putExtra(ProviderArgs.ARG_QUERYID, PosTypes.POS);
				intent.putExtra(ProviderArgs.ARG_QUERYITEMS, new String[]{PosTypes.POS, PosTypes.POSNAME});
				intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table2);
				break;

			case R.id.action_table_adjpositiontypes:
				intent = new Intent(activity, TableActivity.class);
				intent.putExtra(ProviderArgs.ARG_QUERYURI, AdjPositionTypes.CONTENT_URI);
				intent.putExtra(ProviderArgs.ARG_QUERYID, AdjPositionTypes.POSITION);
				intent.putExtra(ProviderArgs.ARG_QUERYITEMS, new String[]{AdjPositionTypes.POSITION, AdjPositionTypes.POSITIONNAME});
				intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table2);
				break;

			case R.id.action_table_linktypes:
				intent = new Intent(activity, TableActivity.class);
				intent.putExtra(ProviderArgs.ARG_QUERYURI, LinkTypes.CONTENT_URI);
				intent.putExtra(ProviderArgs.ARG_QUERYID, LinkTypes.LINKID);
				intent.putExtra(ProviderArgs.ARG_QUERYITEMS, new String[]{LinkTypes.LINKID, LinkTypes.LINK, LinkTypes.RECURSESSELECT});
				intent.putExtra(ProviderArgs.ARG_QUERYSORT, LinkTypes.LINKID + " ASC");
				intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table3);
				break;

			default:
				return super.onOptionsItemSelected(item);
		}

		// start activity
		startActivity(intent);
		return true;
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
				BrowseFragment.this.searchView.clearFocus();
				BrowseFragment.this.searchView.setFocusable(false);
				BrowseFragment.this.searchView.setQuery("", false);
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

		// selector mode adapter values
		final CharSequence[] modes = activity.getResources().getTextArray(R.array.selectors_names);

		// selector mode adapter
		final SpinnerAdapter adapter = new ArrayAdapter<CharSequence>(activity, R.layout.spinner_item_selectors, modes)
		{
			@Override
			public View getView(final int position, final View convertView, final ViewGroup parent)
			{
				return getCustomView(position, convertView, parent, R.layout.spinner_item_selectors);
			}

			@Override
			public View getDropDownView(final int position, final View convertView, final ViewGroup parent)
			{
				return getCustomView(position, convertView, parent, R.layout.spinner_item_selectors_dropdown);
			}

			/**
			 * Get custom view
			 *
			 * @param position      position
			 * @param convertView   convert view
			 * @param parent        parent
			 * @param layoutId      layout id
			 * @return view
			 */
			private View getCustomView(final int position, @SuppressWarnings("UnusedParameters") final View convertView, final ViewGroup parent, final int layoutId)
			{
				final LayoutInflater inflater = activity.getLayoutInflater();
				final View row = inflater.inflate(layoutId, parent, false);
				final ImageView icon = (ImageView) row.findViewById(R.id.icon);
				icon.setImageResource(position == 0 ? R.drawable.ic_selector : R.drawable.ic_xselector);

				final TextView label = (TextView) row.findViewById(R.id.selector);
				if (label != null)
				{
					label.setText(modes[position]);
				}
				return row;
			}
		};

		// spinner listener
		spinner.setOnItemSelectedListener( //
				new OnItemSelectedListener()
				{
					@Override
					public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position, final long id)
					{
						final Settings.Selector selectorMode = Settings.Selector.values()[position];
						selectorMode.setPref(activity);

						// Log.d(BrowseFragment.TAG, selectorMode.name());
					}

					@Override
					public void onNothingSelected(final AdapterView<?> parentView)
					{
						//
					}
				});

		// apply spinner adapter
		spinner.setAdapter(adapter);

		// saved selector mode
		final Settings.Selector selectorMode = Settings.Selector.getPref(activity);
		if (selectorMode != null)
		{
			spinner.setSelection(selectorMode.ordinal());
		}
	}

	@Override
	public void onSaveInstanceState(final Bundle savedInstanceState)
	{
		// always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);

		// spinner
		if (this.spinner == null)
		{
			// serialize the current dropdown position
			final int position = this.spinner.getSelectedItemPosition();
			savedInstanceState.putInt(BrowseFragment.STATE_SPINNER, position);
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
	 * @param query query
	 */
	@SuppressWarnings("boxing")
	public void search(final String query)
	{
		// recurse
		final boolean recurse = Settings.getRecursePref(getActivity());

		// dispatch as per query prefix
		Intent searchIntent;
		if (query.matches("#\\p{Lower}\\p{Lower}\\d+"))
		{
			final long id = Long.valueOf(query.substring(3));

			// wordnet
			if (query.startsWith("#ws"))
			{
				final Parcelable synsetPointer = new SynsetPointer(id, null);
				searchIntent = makeDetailIntent(SynsetActivity.class);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYACTION, ProviderArgs.ARG_QUERYACTION_SYNSET);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, synsetPointer);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYRECURSE, recurse);
			}

			// verbnet
			else if (query.startsWith("#vc"))
			{
				final Parcelable framePointer = new VnClassPointer(id);
				searchIntent = makeDetailIntent(VnClassActivity.class);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYACTION, ProviderArgs.ARG_QUERYACTION_VNCLASS);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, framePointer);
			}

			// propbank
			else if (query.startsWith("#pr"))
			{
				final Parcelable roleSetPointer = new PbRoleSetPointer(id);
				searchIntent = makeDetailIntent(PbRoleSetActivity.class);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYACTION, ProviderArgs.ARG_QUERYACTION_PBROLESET);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, roleSetPointer);
			}

			// framenet
			else if (query.startsWith("#ff"))
			{
				final Parcelable framePointer = new FnFramePointer(id);
				searchIntent = makeDetailIntent(FnFrameActivity.class);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYACTION, ProviderArgs.ARG_QUERYACTION_FNFRAME);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, framePointer);
			}
			else if (query.startsWith("#fl"))
			{
				final Parcelable lexunitPointer = new FnLexUnitPointer(id);
				searchIntent = makeDetailIntent(FnLexUnitActivity.class);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYACTION, ProviderArgs.ARG_QUERYACTION_FNLEXUNIT);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, lexunitPointer);
			}
			else if (query.startsWith("#fs"))
			{
				final Parcelable sentencePointer = new FnSentencePointer(id);
				searchIntent = makeDetailIntent(FnSentenceActivity.class);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYACTION, ProviderArgs.ARG_QUERYACTION_FNSENTENCE);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, sentencePointer);
			}
			else if (query.startsWith("#fa"))
			{
				final Parcelable annoSetPointer = new FnAnnoSetPointer(id);
				searchIntent = makeDetailIntent(FnAnnoSetActivity.class);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYACTION, ProviderArgs.ARG_QUERYACTION_FNANNOSET);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, annoSetPointer);
			}
			else if (query.startsWith("#fp"))
			{
				final Parcelable patternPointer = new FnPatternPointer(id);
				searchIntent = makeDetailIntent(FnAnnoSetActivity.class);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYACTION, ProviderArgs.ARG_QUERYACTION_FNPATTERN);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, patternPointer);
			}
			else if (query.startsWith("#fv"))
			{
				final Parcelable valenceunitPointer = new FnValenceUnitPointer(id);
				searchIntent = makeDetailIntent(FnAnnoSetActivity.class);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYACTION, ProviderArgs.ARG_QUERYACTION_FNVALENCEUNIT);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, valenceunitPointer);
			}

			// predicate matrix
			else if (query.startsWith("#mr"))
			{
				final Parcelable rolePointer = new PmRolePointer(id);
				searchIntent = makeDetailIntent(PredicateMatrixActivity.class);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYACTION, ProviderArgs.ARG_QUERYACTION_PMROLE);
				searchIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, rolePointer);
			}
			else
			{
				return;
			}
		}
		else
		{
			// search for string
			searchIntent = makeSelectorIntent();
			searchIntent.putExtra(ProviderArgs.ARG_QUERYSTRING, query);
			searchIntent.putExtra(ProviderArgs.ARG_QUERYRECURSE, recurse);
		}
		Log.d(BrowseFragment.TAG, "SEARCH " + searchIntent);
		startActivity(searchIntent);
	}

	// I N T E N T F A C T O R Y

	/**
	 * Make selector intent as per settings
	 *
	 * @return intent
	 */
	private Intent makeSelectorIntent()
	{
		// activity
		final Activity activity = getActivity();

		// intent
		Class<?> intentClass = null;

		// type
		final Settings.Selector selectorType = Settings.getSelectorPref(activity);
		switch (selectorType)
		{
			case SELECTOR:
				intentClass = SelectorActivity.class;
				break;
			case XSELECTOR:
				intentClass = XSelectorActivity.class;
				break;
		}

		// mode
		final Settings.SelectorViewMode selectorMode = Settings.getSelectorViewModePref(activity);
		Intent intent = null;
		switch (selectorMode)
		{
			case VIEW:
				intent = new Intent(getActivity(), intentClass);
				break;

			case WEB:
				intent = new Intent(getActivity(), WebActivity.class);
				break;
		}
		return intent;
	}

	/**
	 * Make detail intent as per settings
	 *
	 * @param intentClass if WebActivity is not to be used
	 * @return intent
	 */
	private Intent makeDetailIntent(final Class<?> intentClass)
	{
		// activity
		final Activity activity = getActivity();

		// intent
		Intent intent = null;

		// mode
		final Settings.DetailViewMode detailMode = Settings.getDetailViewModePref(activity);
		switch (detailMode)
		{
			case VIEW:
				intent = new Intent(getActivity(), intentClass);
				break;

			case WEB:
				intent = new Intent(getActivity(), WebActivity.class);
				break;
		}
		return intent;
	}
}
