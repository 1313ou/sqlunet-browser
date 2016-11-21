package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.sqlunet.browser.config.TableActivity;
import org.sqlunet.browser.selector.Browse1Activity;
import org.sqlunet.browser.selector.Browse1Fragment;
import org.sqlunet.browser.web.WebActivity;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.browser.xselector.XBrowse1Activity;
import org.sqlunet.browser.xselector.XBrowse1Fragment;
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
public class BrowseFragment extends Fragment implements SearchListener
{
	static private final String TAG = "Browse1Fragment";

	/**
	 * Selector mode state
	 */
	static private final String STATE_SPINNER = "selected_selector_mode";

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

		// status view
		this.statusView = (TextView) view.findViewById(R.id.statusView);

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
		inflater.inflate(R.menu.browse, menu);

		// set up search view
		setupSearch(menu);
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

	// A C T I O N B A R

	/**
	 * Set up action bar
	 *
	 * @return spinner
	 */
	private Spinner setupActionBar(final LayoutInflater inflater)
	{
		// activity
		final Activity activity = getActivity();

		// action bar
		final ActionBar actionBar = activity.getActionBar();
		assert actionBar != null;

		// color
		// color
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.browse_action_bar_color, activity.getTheme())));
		}
		else
		{
			//noinspection deprecation
			actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.browse_action_bar_color)));
		}

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
		final MenuItem searchMenuItem = menu.findItem(R.id.searchView);

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
	 * Set up type bar spinner
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
				textView.setCompoundDrawablesWithIntrinsicBounds(0, position == 0 ? R.drawable.ic_selector : R.drawable.ic_xselector, 0, 0);

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
				textView.setCompoundDrawablesWithIntrinsicBounds(position == 0 ? R.drawable.ic_selector : R.drawable.ic_xselector, 0, 0, 0);

				return view;
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

						// Log.d(Browse1Fragment.TAG, selectorMode.name());
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
		if (this.spinner != null)
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
	@Override
	public void suggest(final String query)
	{
		this.statusView.setText("view: '" + query + "'");
		this.searchView.setQuery(query, true); // true=submit
	}

	// S E A R C H

	/**
	 * Handle search
	 *
	 * @param query query
	 */
	@Override
	@SuppressWarnings("boxing")
	public void search(final String query)
	{
		this.statusView.setText("search: '" + query + "'");

		// recurse
		final boolean recurse = Settings.getRecursePref(getActivity());

		// dispatch as per query prefix
		Fragment fragment = null;
		Intent targetIntent = null;
		Bundle args = new Bundle();
		if (query.matches("#\\p{Lower}\\p{Lower}\\d+"))
		{
			final long id = Long.valueOf(query.substring(3));

			// wordnet
			if (query.startsWith("#ws"))
			{
				final Parcelable synsetPointer = new SynsetPointer(id, null);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, synsetPointer);
				args.putBoolean(ProviderArgs.ARG_QUERYRECURSE, recurse);

				targetIntent = makeDetailIntent(SynsetActivity.class);
			}

			// verbnet
			else if (query.startsWith("#vc"))
			{
				final Parcelable framePointer = new VnClassPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_VNCLASS);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, framePointer);

				targetIntent = makeDetailIntent(VnClassActivity.class);
			}

			// propbank
			else if (query.startsWith("#pr"))
			{
				final Parcelable roleSetPointer = new PbRoleSetPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_PBROLESET);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, roleSetPointer);

				targetIntent = makeDetailIntent(PbRoleSetActivity.class);
			}

			// framenet
			else if (query.startsWith("#ff"))
			{
				final Parcelable framePointer = new FnFramePointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNFRAME);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, framePointer);

				targetIntent = makeDetailIntent(FnFrameActivity.class);
			}
			else if (query.startsWith("#fl"))
			{
				final Parcelable lexunitPointer = new FnLexUnitPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNLEXUNIT);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, lexunitPointer);

				targetIntent = makeDetailIntent(FnLexUnitActivity.class);
			}
			else if (query.startsWith("#fs"))
			{
				final Parcelable sentencePointer = new FnSentencePointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNSENTENCE);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, sentencePointer);

				targetIntent = makeDetailIntent(FnSentenceActivity.class);
			}
			else if (query.startsWith("#fa"))
			{
				final Parcelable annoSetPointer = new FnAnnoSetPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNANNOSET);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, annoSetPointer);

				targetIntent = makeDetailIntent(FnAnnoSetActivity.class);
			}
			else if (query.startsWith("#fp"))
			{
				final Parcelable patternPointer = new FnPatternPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNPATTERN);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, patternPointer);

				targetIntent = makeDetailIntent(FnAnnoSetActivity.class);
			}
			else if (query.startsWith("#fv"))
			{
				final Parcelable valenceunitPointer = new FnValenceUnitPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNVALENCEUNIT);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, valenceunitPointer);

				targetIntent = makeDetailIntent(FnAnnoSetActivity.class);
			}

			// predicate matrix
			else if (query.startsWith("#mr"))
			{
				final Parcelable rolePointer = new PmRolePointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_PMROLE);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, rolePointer);

				targetIntent = makeDetailIntent(PredicateMatrixActivity.class);
			}
			else
			{
				return;
			}
		}
		else
		{
			// search for string
			args.putString(ProviderArgs.ARG_QUERYSTRING, query);
			args.putBoolean(ProviderArgs.ARG_QUERYRECURSE, recurse);

			//targetIntent = makeSelectorIntent();
			fragment = makeSelectorFragment();
		}

		// dispatch
		Log.d(BrowseFragment.TAG, "SEARCH " + args);
		if (targetIntent != null)
		{
			targetIntent.putExtras(args);
			startActivity(targetIntent);
			return;
		}

		if (fragment != null)
		{
			fragment.setArguments(args);

			// view
			final View view = getView();

			// clear splash
			assert view != null;
			final ViewGroup container = (ViewGroup) view.findViewById(R.id.container_browse);
			container.removeAllViews();

			// fragment
			fragment.setArguments(args);
			getFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container_browse, fragment) //
					.commit();

		}
	}

	// I N T E N T / F R A G M E N T F A C T O R Y

	private Fragment makeSelectorFragment()
	{
		// activity
		final Activity activity = getActivity();

		// type
		final Settings.Selector selectorType = Settings.getSelectorPref(activity);

		// mode
		final Settings.SelectorViewMode selectorMode = Settings.getSelectorViewModePref(activity);

		switch (selectorMode)
		{
			case VIEW:
				switch (selectorType)
				{
					case SELECTOR:
						return new Browse1Fragment();
					case XSELECTOR:
						return new XBrowse1Fragment();
				}
				break;

			case WEB:
				return new WebFragment();
		}

		return null;
	}

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
		Intent intent = null;

		// type
		final Settings.Selector selectorType = Settings.getSelectorPref(activity);

		// mode
		final Settings.SelectorViewMode selectorMode = Settings.getSelectorViewModePref(activity);

		switch (selectorMode)
		{
			case VIEW:
				Class<?> intentClass = null;
				switch (selectorType)
				{
					case SELECTOR:
						intentClass = Browse1Activity.class;
						break;
					case XSELECTOR:
						intentClass = XBrowse1Activity.class;
						break;
				}
				intent = new Intent(getActivity(), intentClass);
				break;

			case WEB:
				intent = new Intent(getActivity(), WebActivity.class);
				break;
		}
		intent.setAction(ProviderArgs.ACTION_QUERY);

		return intent;
	}

	/**
	 * Make detail intent as per settings
	 *
	 * @param intentClass intent class if WebActivity is not to be used
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
		intent.setAction(ProviderArgs.ACTION_QUERY);

		return intent;
	}
}
