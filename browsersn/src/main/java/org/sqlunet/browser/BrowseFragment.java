/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import org.sqlunet.browser.config.TableActivity;
import org.sqlunet.browser.selector.Browse1Activity;
import org.sqlunet.browser.selector.Browse1Fragment;
import org.sqlunet.browser.selector.SnBrowse1Fragment;
import org.sqlunet.browser.sn.R;
import org.sqlunet.browser.sn.Settings;
import org.sqlunet.browser.web.WebActivity;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.browser.xselector.XBrowse1Activity;
import org.sqlunet.browser.xselector.XBrowse1Fragment;
import org.sqlunet.history.History;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.syntagnet.SnCollocationPointer;
import org.sqlunet.syntagnet.browser.CollocationActivity;
import org.sqlunet.wordnet.SenseKeyPointer;
import org.sqlunet.wordnet.SynsetPointer;
import org.sqlunet.wordnet.WordPointer;
import org.sqlunet.wordnet.browser.SenseKeyActivity;
import org.sqlunet.wordnet.browser.SynsetActivity;
import org.sqlunet.wordnet.browser.WordActivity;
import org.sqlunet.wordnet.provider.WordNetContract.AdjPositions;
import org.sqlunet.wordnet.provider.WordNetContract.Domains;
import org.sqlunet.wordnet.provider.WordNetContract.Relations;
import org.sqlunet.wordnet.provider.WordNetContract.Poses;
import org.sqlunet.wordnet.provider.WordNetProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

/**
 * Browse fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BrowseFragment extends BaseSearchFragment
{
	static private final String TAG = "BrowseF";

	// C R E A T I O N

	/**
	 * Constructor
	 */
	public BrowseFragment()
	{
		this.layoutId = R.layout.fragment_browse;
		this.menuId = R.menu.browse;
		this.colorAttrId = R.attr.colorPrimary;
		this.spinnerLabels = R.array.selectors_names;
		this.spinnerIcons = R.array.selectors_icons;
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		if (savedInstanceState == null)
		{
			// splash fragment
			final Fragment fragment = new BrowseSplashFragment();
			assert isAdded();
			getChildFragmentManager() //
					.beginTransaction() //
					.setReorderingAllowed(true) //
					.replace(R.id.container_browse, fragment, SplashFragment.FRAGMENT_TAG) //
					//.addToBackStack(SplashFragment.FRAGMENT_TAG) //
					.commit();
		}

		return view;
	}

	// S P I N N E R

	@Override
	protected void setupSpinner(@NonNull final Spinner spinner)
	{
		spinner.setVisibility(View.VISIBLE);

		// apply spinner adapter
		spinner.setAdapter(getSpinnerAdapter());

		// spinner listener
		spinner.setOnItemSelectedListener( //
				new OnItemSelectedListener()
				{
					@Override
					public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position, final long id)
					{
						final Settings.Selector selectorMode = Settings.Selector.values()[position];
						selectorMode.setPref(requireContext());
					}

					@Override
					public void onNothingSelected(final AdapterView<?> parentView)
					{
						//
					}
				});

		// saved selector mode
		final Settings.Selector selectorMode = Settings.Selector.getPref(requireContext());
		spinner.setSelection(selectorMode.ordinal());
	}

	// M E N U

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		// activity
		final Context context = requireContext();

		// intent
		Intent intent;

		// handle item selection
		final int itemId = item.getItemId();
		if (R.id.action_table_domains == itemId)
		{
			intent = new Intent(context, TableActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYURI, WordNetProvider.makeUri(Domains.URI));
			intent.putExtra(ProviderArgs.ARG_QUERYID, Domains.DOMAINID);
			intent.putExtra(ProviderArgs.ARG_QUERYITEMS, new String[]{Domains.DOMAINID, Domains.DOMAIN, Domains.POSID});
			intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table3);
		}
		else if (R.id.action_table_poses == itemId)
		{
			intent = new Intent(context, TableActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYURI, WordNetProvider.makeUri(Poses.URI));
			intent.putExtra(ProviderArgs.ARG_QUERYID, Poses.POSID);
			intent.putExtra(ProviderArgs.ARG_QUERYITEMS, new String[]{Poses.POSID, Poses.POS});
			intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table2);
		}
		else if (R.id.action_table_adjpositions == itemId)
		{
			intent = new Intent(context, TableActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYURI, WordNetProvider.makeUri(AdjPositions.URI));
			intent.putExtra(ProviderArgs.ARG_QUERYID, AdjPositions.POSITIONID);
			intent.putExtra(ProviderArgs.ARG_QUERYITEMS, new String[]{AdjPositions.POSITIONID, AdjPositions.POSITION});
			intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table2);
		}
		else if (R.id.action_table_relations == itemId)
		{
			intent = new Intent(context, TableActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYURI, WordNetProvider.makeUri(Relations.URI));
			intent.putExtra(ProviderArgs.ARG_QUERYID, Relations.RELATIONID);
			intent.putExtra(ProviderArgs.ARG_QUERYITEMS, new String[]{Relations.RELATIONID, Relations.RELATION, Relations.RECURSESSELECT});
			intent.putExtra(ProviderArgs.ARG_QUERYSORT, Relations.RELATIONID + " ASC");
			intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table3);
		}
		else
		{
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
		Log.d(TAG, "Browse '" + query + '\'');

		// subtitle
		final Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
		assert toolbar != null;
		toolbar.setSubtitle(query);

		// history
		History.recordQuery(requireContext(), query);

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

		// parameters
		final int recurse = org.sqlunet.wordnet.settings.Settings.getRecursePref(requireContext());
		final Bundle parameters = org.sqlunet.wordnet.settings.Settings.getRenderParametersPref(requireContext());

		// menuDispatch as per query prefix
		@SuppressWarnings("TooBroadScope") Fragment fragment = null;
		Intent targetIntent = null;
		Bundle args = new Bundle();
		if (query.matches("#\\p{Lower}\\p{Lower}\\d+"))
		{
			final long id = Long.parseLong(query.substring(3));

			// wordnet
			if (query.startsWith("#ws"))
			{
				final Parcelable synsetPointer = new SynsetPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, synsetPointer);
				args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);
				args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters);

				targetIntent = makeDetailIntent(SynsetActivity.class);
			}
			else if (query.startsWith("#ww"))
			{
				final Parcelable wordPointer = new WordPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_WORD);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, wordPointer);
				args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);
				args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters);

				targetIntent = makeDetailIntent(WordActivity.class);
			}
			else if (query.startsWith("#sc"))
			{
				final Parcelable collocationPointer = new SnCollocationPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_COLLOCATION);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, collocationPointer);
				args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);
				args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters);

				targetIntent = makeDetailIntent(CollocationActivity.class);
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
				args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters);

				targetIntent = makeDetailIntent(SenseKeyActivity.class);
			}
		}
		else
		{
			// search for string
			args.putString(ProviderArgs.ARG_QUERYSTRING, query);
			args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);
			args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters);

			//targetIntent = makeSelectorIntent();
			fragment = makeSelectorFragment();
		}

		// menuDispatch
		Log.d(TAG, "Search " + args);
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
			if (!isAdded())
			{
				return;
			}
			getChildFragmentManager() //
					.beginTransaction() //
					.setReorderingAllowed(true) //
					.replace(R.id.container_browse, fragment, BaseSelectorsFragment.FRAGMENT_TAG) //
					.addToBackStack(BaseSelectorsFragment.FRAGMENT_TAG)
					.commit();
		}
	}

	@Override
	protected boolean triggerFocusSearch()
	{
		if (!isAdded())
		{
			return false;
		}
		final Fragment active = getChildFragmentManager().findFragmentById(R.id.container_browse);
		return active != null && SplashFragment.FRAGMENT_TAG.equals(active.getTag());
	}

	// I N T E N T / F R A G M E N T   F A C T O R Y

	/**
	 * Fragment factory
	 *
	 * @return fragment
	 */
	@NonNull
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
					case SELECTOR_ALT:
						return new SnBrowse1Fragment();
				}
				break;

			case WEB:
				return new WebFragment();
		}

		throw new IllegalArgumentException(selectorMode.toString());
	}

	/**
	 * Make selector intent as per settings
	 *
	 * @return intent
	 */
	@NonNull
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
