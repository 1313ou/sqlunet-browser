/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.wn;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.sqlunet.browser.BaseSearchFragment;
import org.sqlunet.browser.SearchTextSplashFragment;
import org.sqlunet.browser.SplashFragment;
import org.sqlunet.browser.wn.lib.R;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.wordnet.provider.WordNetContract;
import org.sqlunet.wordnet.provider.WordNetProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Search text fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SearchTextFragment extends BaseSearchFragment
{
	static private final String TAG = "SearchTextF";

	// C R E A T I O N

	/**
	 * Constructor
	 */
	public SearchTextFragment()
	{
		this.layoutId = R.layout.fragment_searchtext;
		this.menuId = R.menu.searchtext;
		this.colorAttrId = R.attr.colorPrimaryVariant;
		this.spinnerLabels = R.array.searchtext_modes;
		this.spinnerIcons = R.array.searchtext_icons;
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		if (savedInstanceState == null)
		{
			// splash fragment
			final Fragment fragment = new SearchTextSplashFragment();
			assert isAdded();
			getChildFragmentManager() //
					.beginTransaction() //
					.setReorderingAllowed(true) //
					.replace(R.id.container_searchtext, fragment, SplashFragment.FRAGMENT_TAG) //
					//.addToBackStack(SplashFragment.FRAGMENT_TAG) //
					.commit();
		}
	}

	@Override
	public void onStop()
	{
		super.onStop();

		// remove data fragments and replace with splash before onSaveInstanceState takes place (between -3 and -4)
		beforeSaving(new SearchTextSplashFragment(), SplashFragment.FRAGMENT_TAG, R.id.container_searchtext, TextFragment.FRAGMENT_TAG);
	}

	// S P I N N E R

	@Override
	protected void acquireSpinner(@NonNull final Spinner spinner)
	{
		// to set position
		super.acquireSpinner(spinner);

		spinner.setVisibility(View.VISIBLE);

		// apply spinner adapter
		spinner.setAdapter(getSpinnerAdapter());

		// spinner listener
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position, final long id)
			{
				Settings.setSearchModePref(requireContext(), position);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parentView)
			{
				//
			}
		});

		// spinner position
		final int modePosition = Settings.getSearchModePref(requireContext());
		spinner.setSelection(modePosition);
	}

	// S E A R C H

	/**
	 * Handle query
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

		// super
		super.search(query);

		// log
		Log.d(TAG, "Search text " + query);

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

		// mode
		final int modePosition = getSearchModePosition();

		// status
		// final CharSequence[] textSearches = getResources().getTextArray(R.array.searchtext_modes);

		// as per selected mode
		String searchUri;
		String id;
		String idType;
		String target;
		String[] columns;
		String[] hiddenColumns;
		String database;
		switch (modePosition)
		{
			case 0:
				searchUri = WordNetProvider.makeUri(WordNetContract.Lookup_Definitions.URI);
				id = WordNetContract.Lookup_Definitions.SYNSETID;
				idType = "synset";
				target = WordNetContract.Lookup_Definitions.DEFINITION;
				columns = new String[]{WordNetContract.Lookup_Definitions.DEFINITION};
				hiddenColumns = new String[]{WordNetContract.Lookup_Definitions.SYNSETID};
				database = "wn";
				break;
			case 1:
				searchUri = WordNetProvider.makeUri(WordNetContract.Lookup_Samples.URI);
				id = WordNetContract.Lookup_Samples.SYNSETID;
				idType = "synset";
				target = WordNetContract.Lookup_Samples.SAMPLE;
				columns = new String[]{WordNetContract.Lookup_Samples.SAMPLE};
				hiddenColumns = new String[]{WordNetContract.Lookup_Samples.SYNSETID};
				database = "wn";
				break;
			case 2:
				searchUri = WordNetProvider.makeUri(WordNetContract.Lookup_Words.URI);
				id = WordNetContract.Lookup_Words.WORDID;
				idType = "word";
				target = WordNetContract.Lookup_Words.WORD;
				columns = new String[]{WordNetContract.Lookup_Words.WORD};
				hiddenColumns = new String[]{WordNetContract.Lookup_Words.WORDID};
				database = "wn";
				break;
			default:
				return;
		}

		// parameters
		final Bundle args = new Bundle();
		args.putString(ProviderArgs.ARG_QUERYURI, searchUri);
		args.putString(ProviderArgs.ARG_QUERYID, id);
		args.putString(ProviderArgs.ARG_QUERYIDTYPE, idType);
		args.putStringArray(ProviderArgs.ARG_QUERYITEMS, columns);
		args.putStringArray(ProviderArgs.ARG_QUERYHIDDENITEMS, hiddenColumns);
		args.putString(ProviderArgs.ARG_QUERYFILTER, target + " MATCH ?");
		args.putString(ProviderArgs.ARG_QUERYARG, query);
		args.putInt(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_searchtext);
		args.putString(ProviderArgs.ARG_QUERYDATABASE, database);

		// fragment
		final Fragment fragment = new TextFragment();
		fragment.setArguments(args);
		if (!isAdded())
		{
			return;
		}
		getChildFragmentManager() //
				.beginTransaction() //
				.setReorderingAllowed(true) //
				.replace(R.id.container_searchtext, fragment, TextFragment.FRAGMENT_TAG) //
				.addToBackStack(TextFragment.FRAGMENT_TAG) //
				.commit();
	}

	@Override
	protected boolean triggerFocusSearch()
	{
		if (!isAdded())
		{
			return false;
		}
		Fragment active = getChildFragmentManager().findFragmentById(R.id.container_searchtext);
		return active != null && SplashFragment.FRAGMENT_TAG.equals(active.getTag());
	}
}
