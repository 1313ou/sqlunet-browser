package org.sqlunet.browser;

import org.sqlunet.browser.config.ManagementActivity;
import org.sqlunet.browser.config.SettingsActivity;
import org.sqlunet.browser.config.SetupActivity;
import org.sqlunet.browser.config.SetupSqlActivity;
import org.sqlunet.browser.config.Status;
import org.sqlunet.browser.config.StorageActivity;
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
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.verbnet.VnClassPointer;
import org.sqlunet.verbnet.browser.VnClassActivity;
import org.sqlunet.wordnet.SynsetPointer;
import org.sqlunet.wordnet.browser.SynsetActivity;
import org.sqlunet.wordnet.provider.WordNetContract.AdjPositionTypes;
import org.sqlunet.wordnet.provider.WordNetContract.LexDomains;
import org.sqlunet.wordnet.provider.WordNetContract.LinkTypes;
import org.sqlunet.wordnet.provider.WordNetContract.PosTypes;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 * Main activity
 *
 * @author Bernard Bou
 */
public class MainActivity extends Activity
{
	static private final String TAG = "SqlUNet Main"; //$NON-NLS-1$

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

	/**
	 * Selector mode state
	 */
	private static final String STATE_SELECTED_SELECTOR_MODE = "org.sqlunet.browser.selector.selected"; //$NON-NLS-1$

	// C R E A T I O N

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// settings
		Settings.initialize(this);

		// info
		Log.d(MainActivity.TAG, "DATABASE=" + StorageSettings.getDatabasePath(getBaseContext())); //$NON-NLS-1$
		// Log.d(MainActivity.TAG, "DATADIR=" + StorageSettings.getDataDir(getBaseContext())); //$NON-NLS-1$
		// Log.d(MainActivity.TAG, "DOWNLOADDBFROM=" + StorageSettings.getDbDownloadSource(getBaseContext())); //$NON-NLS-1$
		// Log.d(MainActivity.TAG, "DOWNLOADSQLFROM=" + StorageSettings.getSqlDownloadSource(getBaseContext())); //$NON-NLS-1$
		// Log.d(MainActivity.TAG, "DOWNLOADSQLTO=" + StorageSettings.getSqlDownloadTarget(getBaseContext())); //$NON-NLS-1$
		// Log.d(MainActivity.TAG, "SQLFROM=" + StorageSettings.getSqlSource(getBaseContext())); //$NON-NLS-1$

		// layout
		setContentView(R.layout.activity_main);

		// get views from handles
		this.statusView = (TextView) findViewById(R.id.statusView);

		// show the Up button in the action bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);

		// set up the action bar to show a custom layout
		final View actionBarView = getLayoutInflater().inflate(R.layout.actionbar_custom, null);
		actionBar.setCustomView(actionBarView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		actionBar.setDisplayShowTitleEnabled(false);

		// adapter range
		final CharSequence[] modes = getResources().getTextArray(R.array.selectors);

		// adapter
		/*
	  Selector mode adapter
	 */
		SpinnerAdapter adapter = new ArrayAdapter<CharSequence>(this, R.layout.actionbar_item_selectors, modes) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return getCustomView(position, convertView, parent, R.layout.actionbar_item_selectors);
			}

			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent) {
				return getCustomView(position, convertView, parent, R.layout.actionbar_item_selectors_dropdown);
			}

			private View getCustomView(int position, @SuppressWarnings("UnusedParameters") View convertView, ViewGroup parent, int layoutId) {
				final LayoutInflater inflater = getLayoutInflater();
				final View row = inflater.inflate(layoutId, parent, false);
				final ImageView icon = (ImageView) row.findViewById(R.id.icon);
				icon.setImageResource(position == 0 ? R.drawable.ic_selector : R.drawable.ic_xselector);

				final TextView label = (TextView) row.findViewById(R.id.selector);
				if (label != null) {
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
			@SuppressWarnings("synthetic-access")
			@Override
			public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position, final long id)
			{
				final Settings.Selector mode = Settings.Selector.values()[position];
				mode.setPref(MainActivity.this);

				// Log.d(MainActivity.TAG, mode.name());
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
		final Settings.Selector mode = Settings.Selector.getPref(this);
		if (mode != null)
		{
			this.spinner.setSelection(mode.ordinal());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(final Bundle savedInstanceState)
	{
		if (this.spinner == null)
			return;

		// serialize the current dropdown position
		final int position = this.spinner.getSelectedItemPosition();
		savedInstanceState.putInt(MainActivity.STATE_SELECTED_SELECTOR_MODE, position);

		// always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	public void onRestoreInstanceState(final Bundle savedInstanceState)
	{
		if (this.spinner == null)
			return;

		// always call the superclass so it can restore the view hierarchy
		super.onRestoreInstanceState(savedInstanceState);

		// restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(MainActivity.STATE_SELECTED_SELECTOR_MODE))
		{
			this.spinner.setSelection(savedInstanceState.getInt(MainActivity.STATE_SELECTED_SELECTOR_MODE));
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// check hook
		boolean canRun = Status.canRun(getBaseContext());
		if (!canRun)
		{
			final Intent intent = new Intent(this, StatusActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
			startActivity(intent);
			finish();
		}
	}

	// I N T E N T H A N D L I N G

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(final Intent intent)
	{
		Log.d(MainActivity.TAG, "NewIntent " + intent); //$NON-NLS-1$
		handleIntent(intent);
	}

	/**
	 * Handle intent
	 *
	 * @param intent
	 *            intent
	 */
	private void handleIntent(final Intent intent)
	{
		final String action = intent.getAction();
		final String query = intent.getStringExtra(SearchManager.QUERY);

		// search action
		if (Intent.ACTION_SEARCH.equals(action))
		{
			this.statusView.setText("search: '" + query + "'"); //$NON-NLS-1$ //$NON-NLS-2$
			handleSearch(query);
		}

		// view action
		else if (Intent.ACTION_VIEW.equals(action))
		{
			this.statusView.setText("query: '" + query + "'"); //$NON-NLS-1$ //$NON-NLS-2$
			this.searchView.setQuery(query, true); // submit
		}
	}

	// I N T E N T F A C T O R Y

	/**
	 * Make selector intent as per settings
	 *
	 * @return intent
	 */
	private Intent makeSelectorIntent()
	{
		Intent intent = null;
		Class<?> intentClass = null;

		// mode
		final Settings.Selector type = Settings.getXnModePref(this);
		switch (type)
		{
		case SELECTOR:
			intentClass = SelectorActivity.class;
			break;
		case XSELECTOR:
			intentClass = XSelectorActivity.class;
			break;
		default:
			break;
		}

		// mode
		final Settings.SelectorMode mode = Settings.getSelectorModePref(this);
		switch (mode)
		{
		case VIEW:
			intent = new Intent(this, intentClass);
			break;

		case WEB:
			intent = new Intent(this, WebActivity.class);
			break;

		default:
			break;
		}
		return intent;
	}

	/**
	 * Make detail intent as per settings
	 *
	 * @param intentClass
	 *            if WebActivity is not to be used
	 * @return intent
	 */
	private Intent makeDetailIntent(final Class<?> intentClass)
	{
		Intent intent = null;

		// mode
		final Settings.DetailMode mode = Settings.getDetailModePref(this);
		switch (mode)
		{
		case VIEW:
			intent = new Intent(this, intentClass);
			break;

		case WEB:
			intent = new Intent(this, WebActivity.class);
			break;

		default:
			break;
		}
		return intent;
	}

	/**
	 * Make restart intent
	 *
	 * @return restart intent
	 */
	private Intent makeRestartIntent()
	{
		final Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}

	// M E N U

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		// set up search view
		this.searchView = (SearchView) menu.findItem(R.id.searchView).getActionView();
		setupSearchView(this.searchView);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		Intent intent;

		// handle item selection
		switch (item.getItemId())
		{

		// settings

		case R.id.action_settings:
			intent = new Intent(this, SettingsActivity.class);
			break;

		// tables

		case R.id.action_table_lexdomains:
			intent = new Intent(this, TableActivity.class);
			intent.putExtra(SqlUNetContract.ARG_QUERYURI, LexDomains.CONTENT_URI);
			intent.putExtra(SqlUNetContract.ARG_QUERYID, LexDomains.LEXDOMAINID);
			intent.putExtra(SqlUNetContract.ARG_QUERYITEMS, new String[] { LexDomains.LEXDOMAINID, LexDomains.LEXDOMAIN });
			intent.putExtra(SqlUNetContract.ARG_QUERYXITEMS, new String[] { LexDomains.POS });
			intent.putExtra(SqlUNetContract.ARG_QUERYLAYOUT, R.layout.item_table2);
			break;

		case R.id.action_table_postypes:
			intent = new Intent(this, TableActivity.class);
			intent.putExtra(SqlUNetContract.ARG_QUERYURI, PosTypes.CONTENT_URI);
			intent.putExtra(SqlUNetContract.ARG_QUERYID, PosTypes.POS);
			intent.putExtra(SqlUNetContract.ARG_QUERYITEMS, new String[] { PosTypes.POS, PosTypes.POSNAME });
			intent.putExtra(SqlUNetContract.ARG_QUERYLAYOUT, R.layout.item_table2);
			break;

		case R.id.action_table_adjpositiontypes:
			intent = new Intent(this, TableActivity.class);
			intent.putExtra(SqlUNetContract.ARG_QUERYURI, AdjPositionTypes.CONTENT_URI);
			intent.putExtra(SqlUNetContract.ARG_QUERYID, AdjPositionTypes.POSITION);
			intent.putExtra(SqlUNetContract.ARG_QUERYITEMS, new String[] { AdjPositionTypes.POSITION, AdjPositionTypes.POSITIONNAME });
			intent.putExtra(SqlUNetContract.ARG_QUERYLAYOUT, R.layout.item_table2);
			break;

		case R.id.action_table_linktypes:
			intent = new Intent(this, TableActivity.class);
			intent.putExtra(SqlUNetContract.ARG_QUERYURI, LinkTypes.CONTENT_URI);
			intent.putExtra(SqlUNetContract.ARG_QUERYID, LinkTypes.LINKID);
			intent.putExtra(SqlUNetContract.ARG_QUERYITEMS, new String[] { LinkTypes.LINKID, LinkTypes.LINK, LinkTypes.RECURSESSELECT });
			intent.putExtra(SqlUNetContract.ARG_QUERYSORT, LinkTypes.LINKID + " ASC"); //$NON-NLS-1$
			intent.putExtra(SqlUNetContract.ARG_QUERYLAYOUT, R.layout.item_table3);
			break;

		// search

		case R.id.action_text_search:
			intent = new Intent(this, TextSearchActivity.class);
			break;

		case R.id.action_pm_search:
			intent = new Intent(this, PredicateMatrixActivity.class);
			break;

		// database

		case R.id.action_storage:
			intent = new Intent(this, StorageActivity.class);
			break;

		case R.id.action_setup:
			intent = new Intent(this, SetupActivity.class);
			break;

		case R.id.action_setup_sql:
			intent = new Intent(this, SetupSqlActivity.class);
			break;

		case R.id.action_status:
			intent = new Intent(this, StatusActivity.class);
			break;

		case R.id.action_management:
			intent = new Intent(this, ManagementActivity.class);
			break;

		// guide

		case R.id.action_help:
			intent = new Intent(this, HelpActivity.class);
			break;

		case R.id.action_about:
			intent = new Intent(this, AboutActivity.class);
			break;

		// lifecycle

		case R.id.action_restart:
			finish();
			intent = makeRestartIntent();
			break;

		case R.id.action_quit:
			quit();
			return true;

		case R.id.action_appsettings:
			Settings.applicationSettings(this, "org.sqlunet.browser"); //$NON-NLS-1$
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

		// start activity
		startActivity(intent);
		return true;
	}

	// S E A R C H V I E W

	/**
	 * Associate the searchable configuration with the searchView (associate the searchable configuration with the searchView)
	 *
	 * @param searchView
	 *            searchView
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
				searchView.setQuery("", false); //$NON-NLS-1$
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

	// S E A R C H

	/**
	 * Handle search
	 *
	 * @param query
	 *            query
	 */
	@SuppressWarnings("boxing")
	private void handleSearch(final String query)
	{
		// recurse
		final boolean recurse = Settings.getRecursePref(this);

		// dispatch as per query prefix
		Intent searchIntent;
		if (query.startsWith("#")) //$NON-NLS-1$
		{
			// wordnet
			if (query.startsWith("#ws")) //$NON-NLS-1$
			{
				final long synsetid = Long.valueOf(query.substring(3));
				final SynsetPointer synsetPointer = new SynsetPointer();
				synsetPointer.setSynset(synsetid, null);
				searchIntent = makeDetailIntent(SynsetActivity.class);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_SYNSET);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, synsetPointer);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYRECURSE, recurse);
			}

			// verbnet
			else if (query.startsWith("#vc")) //$NON-NLS-1$
			{
				final long classid = Long.valueOf(query.substring(3));
				final VnClassPointer framePointer = new VnClassPointer();
				framePointer.classid = classid;
				searchIntent = makeDetailIntent(VnClassActivity.class);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_VNCLASS);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, framePointer);
			}

			// propbank
			else if (query.startsWith("#pr")) //$NON-NLS-1$
			{
				final long rolesetid = Long.valueOf(query.substring(3));
				final PbRoleSetPointer rolesetPointer = new PbRoleSetPointer();
				rolesetPointer.rolesetid = rolesetid;
				searchIntent = makeDetailIntent(PbRoleSetActivity.class);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_PBROLESET);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, rolesetPointer);
			}

			// framenet
			else if (query.startsWith("#ff")) //$NON-NLS-1$
			{
				final long frameid = Long.valueOf(query.substring(3));
				final FnFramePointer framePointer = new FnFramePointer();
				framePointer.frameid = frameid;
				searchIntent = makeDetailIntent(FnFrameActivity.class);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_FNFRAME);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, framePointer);
			}
			else if (query.startsWith("#fl")) //$NON-NLS-1$
			{
				final long luid = Long.valueOf(query.substring(3));
				final FnLexUnitPointer lexunitPointer = new FnLexUnitPointer();
				lexunitPointer.luid = luid;
				searchIntent = makeDetailIntent(FnLexUnitActivity.class);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_FNLEXUNIT);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, lexunitPointer);
			}
			else if (query.startsWith("#fs")) //$NON-NLS-1$
			{
				final long sentenceid = Long.valueOf(query.substring(3));
				final FnSentencePointer sentencePointer = new FnSentencePointer(sentenceid);
				searchIntent = makeDetailIntent(FnSentenceActivity.class);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_FNSENTENCE);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, sentencePointer);
			}
			else if (query.startsWith("#fa")) //$NON-NLS-1$
			{
				final long annosetid = Long.valueOf(query.substring(3));
				final FnAnnoSetPointer annosetPointer = new FnAnnoSetPointer();
				annosetPointer.annosetid = annosetid;
				searchIntent = makeDetailIntent(FnAnnoSetActivity.class);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_FNANNOSET);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, annosetPointer);
			}
			else if (query.startsWith("#fp")) //$NON-NLS-1$
			{
				final long patternid = Long.valueOf(query.substring(3));
				final FnPatternPointer patternPointer = new FnPatternPointer();
				patternPointer.id = patternid;
				searchIntent = makeDetailIntent(FnAnnoSetActivity.class);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_FNPATTERN);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, patternPointer);
			}
			else if (query.startsWith("#fv")) //$NON-NLS-1$
			{
				final long valenceunitid = Long.valueOf(query.substring(3));
				final FnValenceUnitPointer valenceunitPointer = new FnValenceUnitPointer();
				valenceunitPointer.id = valenceunitid;
				searchIntent = makeDetailIntent(FnAnnoSetActivity.class);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_FNVALENCEUNIT);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, valenceunitPointer);
			}

			// predicate matrix
			else if (query.startsWith("#mr")) //$NON-NLS-1$
			{
				final long pmroleid = Long.valueOf(query.substring(3));
				final PmRolePointer rolePointer = new PmRolePointer();
				rolePointer.roleid = pmroleid;
				searchIntent = makeDetailIntent(PredicateMatrixActivity.class);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_PMROLE);
				searchIntent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, rolePointer);
			}
			else
				return;
		}
		else
		{
			// search for string
			searchIntent = makeSelectorIntent();
			searchIntent.putExtra(SqlUNetContract.ARG_QUERYSTRING, query);
			searchIntent.putExtra(SqlUNetContract.ARG_QUERYRECURSE, recurse);
		}
		Log.d(MainActivity.TAG, "SEARCH " + searchIntent); //$NON-NLS-1$
		startActivity(searchIntent);
	}

	// H E L P E R S

	/**
	 * Quit
	 */
	private void quit()
	{
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
