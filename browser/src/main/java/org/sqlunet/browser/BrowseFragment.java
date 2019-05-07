package org.sqlunet.browser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import org.sqlunet.browser.config.TableActivity;
import org.sqlunet.browser.selector.Browse1Activity;
import org.sqlunet.browser.selector.Browse1Fragment;
import org.sqlunet.browser.web.WebActivity;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.browser.xn.Settings;
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
import org.sqlunet.verbnet.VnClassPointer;
import org.sqlunet.verbnet.browser.VnClassActivity;
import org.sqlunet.wordnet.SenseKeyPointer;
import org.sqlunet.wordnet.SynsetPointer;
import org.sqlunet.wordnet.WordPointer;
import org.sqlunet.wordnet.browser.SenseKeyActivity;
import org.sqlunet.wordnet.browser.SynsetActivity;
import org.sqlunet.wordnet.browser.WordActivity;
import org.sqlunet.wordnet.provider.WordNetContract.AdjPositionTypes;
import org.sqlunet.wordnet.provider.WordNetContract.LexDomains;
import org.sqlunet.wordnet.provider.WordNetContract.LinkTypes;
import org.sqlunet.wordnet.provider.WordNetContract.PosTypes;
import org.sqlunet.wordnet.provider.WordNetProvider;

/**
 * Browse fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BrowseFragment extends BaseSearchFragment
{
	static private final String TAG = "BrowseFragment";

	// C R E A T I O N

	/**
	 * Constructor
	 */
	public BrowseFragment()
	{
		this.layoutId = R.layout.fragment_browse;
		this.menuId = R.menu.browse;
		this.colorId = R.color.browse_actionbar_color;
		this.spinnerLabels = R.array.selectors_names;
		this.spinnerIcons = R.array.selectors_icons;
		this.titleId = R.string.title_browse_section;
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		if (savedInstanceState == null)
		{
			// splash fragment
			final Fragment fragment = new BrowseSplashFragment();
			getChildFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container_browse, fragment) //
					.commit();
		}

		return view;
	}

	// S P I N N E R

	@Override
	protected void setupSpinner(@NonNull final Context context)
	{
		super.setupSpinner(context);

		// spinner listener
		this.spinner.setOnItemSelectedListener( //
				new OnItemSelectedListener()
				{
					@Override
					public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position, final long id)
					{
						final Settings.Selector selectorMode = Settings.Selector.values()[position];
						selectorMode.setPref(context);
					}

					@Override
					public void onNothingSelected(final AdapterView<?> parentView)
					{
						//
					}
				});

		// saved selector mode
		final Settings.Selector selectorMode = Settings.Selector.getPref(context);
		if (selectorMode != null)
		{
			this.spinner.setSelection(selectorMode.ordinal());
		}
	}

	// M E N U

	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		// activity
		final Context context = requireContext();

		// intent
		Intent intent;

		// handle item selection
		switch (item.getItemId())
		{
			case R.id.action_table_lexdomains:
				intent = new Intent(context, TableActivity.class);
				intent.putExtra(ProviderArgs.ARG_QUERYURI, WordNetProvider.makeUri(LexDomains.CONTENT_URI_TABLE));
				intent.putExtra(ProviderArgs.ARG_QUERYID, LexDomains.LEXDOMAINID);
				intent.putExtra(ProviderArgs.ARG_QUERYITEMS, new String[]{LexDomains.LEXDOMAINID, LexDomains.LEXDOMAIN, LexDomains.POS});
				intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table3);
				break;

			case R.id.action_table_postypes:
				intent = new Intent(context, TableActivity.class);
				intent.putExtra(ProviderArgs.ARG_QUERYURI, WordNetProvider.makeUri(PosTypes.CONTENT_URI_TABLE));
				intent.putExtra(ProviderArgs.ARG_QUERYID, PosTypes.POS);
				intent.putExtra(ProviderArgs.ARG_QUERYITEMS, new String[]{PosTypes.POS, PosTypes.POSNAME});
				intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table2);
				break;

			case R.id.action_table_adjpositiontypes:
				intent = new Intent(context, TableActivity.class);
				intent.putExtra(ProviderArgs.ARG_QUERYURI, WordNetProvider.makeUri(AdjPositionTypes.CONTENT_URI_TABLE));
				intent.putExtra(ProviderArgs.ARG_QUERYID, AdjPositionTypes.POSITION);
				intent.putExtra(ProviderArgs.ARG_QUERYITEMS, new String[]{AdjPositionTypes.POSITION, AdjPositionTypes.POSITIONNAME});
				intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table2);
				break;

			case R.id.action_table_linktypes:
				intent = new Intent(context, TableActivity.class);
				intent.putExtra(ProviderArgs.ARG_QUERYURI, WordNetProvider.makeUri(LinkTypes.CONTENT_URI_TABLE));
				intent.putExtra(ProviderArgs.ARG_QUERYID, LinkTypes.LINKID);
				intent.putExtra(ProviderArgs.ARG_QUERYITEMS, new String[]{LinkTypes.LINKID, LinkTypes.LINK, LinkTypes.RECURSESSELECT});
				intent.putExtra(ProviderArgs.ARG_QUERYSORT, LinkTypes.LINKID + " ASC");
				intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table3);
				break;

			default:
				return false;
		}

		// start activity
		startActivity(intent);
		return true;
	}

	// S E A R C H

	/**
	 * Handle search
	 *
	 * @param query0 query
	 */
	@Override
	public void search(@Nullable final String query0)
	{
		if (query0 == null)
		{
			return;
		}
		final String query = query0.trim();
		if (query.isEmpty())
		{
			return;
		}

		// log
		Log.d(BrowseFragment.TAG, "BROWSE " + query);

		// subtitle
		final AppCompatActivity activity = (AppCompatActivity) requireActivity();
		final ActionBar actionBar = activity.getSupportActionBar();
		assert actionBar != null;
		actionBar.setSubtitle(query);

		/*
		// copy to target view
		final View view = getView();
		if (view != null)
		{
			final TextView targetView = (TextView) view.findViewById(R.id.targetView);
			if (targetView != null)
			{
				targetView.setText(query);
			}
		}
		*/

		// recurse
		final int recurse = Settings.getRecursePref(requireContext());

		// menuDispatch as per query prefix
		@SuppressWarnings("TooBroadScope") Fragment fragment = null;
		Intent targetIntent = null;
		Bundle args = new Bundle();
		if (query.matches("#\\p{Lower}\\p{Lower}\\d+"))
		{
			final long id = Long.valueOf(query.substring(3));

			// wordnet
			if (query.startsWith("#ws"))
			{
				final Parcelable synsetPointer = new SynsetPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, synsetPointer);
				args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);

				targetIntent = makeDetailIntent(SynsetActivity.class);
			}
			else if (query.startsWith("#ww"))
			{
				final Parcelable wordPointer = new WordPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_WORD);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, wordPointer);
				args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);

				targetIntent = makeDetailIntent(WordActivity.class);
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

				targetIntent = makeDetailIntent(BrowsePredicateMatrixActivity.class);
			}
			else
			{
				return;
			}
		}
		if (query.matches("#\\p{Lower}\\p{Lower}[\\w:%]+"))
		{
			final String id = query.substring(3);
			if (query.startsWith("#wk"))
			{
				final Parcelable senseKeyPointer = new SenseKeyPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SENSE);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, senseKeyPointer);
				args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);

				targetIntent = makeDetailIntent(SenseKeyActivity.class);
			}
		}
		else
		{
			// search for string
			args.putString(ProviderArgs.ARG_QUERYSTRING, query);
			args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);

			//targetIntent = makeSelectorIntent();
			fragment = makeSelectorFragment();
			//fragment = new Test1Fragment();
		}

		// menuDispatch
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

			// fragment
			fragment.setArguments(args);

			// transaction
			getChildFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container_browse, fragment) //
					.commit();

		}
	}

	// I N T E N T / F R A G M E N T   F A C T O R Y

	/**
	 * Fragment factory
	 *
	 * @return fragment
	 */
	private Fragment makeSelectorFragment()
	{
		// context
		final Context context = requireContext();

		// type
		final Settings.Selector selectorType = Settings.getXSelectorPref(context);

		// mode
		final Settings.SelectorViewMode selectorMode = Settings.getSelectorViewModePref(context);

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
	@NonNull
	@SuppressWarnings("unused")
	private Intent makeSelectorIntent()
	{
		// context
		final Context context = requireContext();

		// intent
		Intent intent = null;

		// type
		final Settings.Selector selectorType = Settings.getXSelectorPref(context);

		// mode
		final Settings.SelectorViewMode selectorMode = Settings.getSelectorViewModePref(context);

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
				intent = new Intent(requireContext(), intentClass);
				break;

			case WEB:
				intent = new Intent(requireContext(), WebActivity.class);
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
	@NonNull
	private Intent makeDetailIntent(final Class<?> intentClass)
	{
		// context
		final Context context = requireContext();

		// intent
		Intent intent = null;

		// mode
		final Settings.DetailViewMode detailMode = Settings.getDetailViewModePref(context);
		switch (detailMode)
		{
			case VIEW:
				intent = new Intent(context, intentClass);
				break;

			case WEB:
				intent = new Intent(context, WebActivity.class);
				break;
		}
		intent.setAction(ProviderArgs.ACTION_QUERY);

		return intent;
	}
}
