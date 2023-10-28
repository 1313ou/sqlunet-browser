/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.sqlunet.browser.sn.R;
import org.sqlunet.browser.sn.Settings;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.wordnet.provider.WordNetContract;
import org.sqlunet.wordnet.provider.WordNetProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

/**
 * Search text fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SearchTextFragment extends BaseSearchFragment
{
	static private final String TAG = "SearchTextF";

	static public final String FRAGMENT_TAG = "text";

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
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		final View view = super.onCreateView(inflater, container, savedInstanceState);

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

		return view;
	}

	// S P I N N E R

	@Override
	protected void setupSpinner()
	{
		this.spinner.setVisibility(View.VISIBLE);

		// apply spinner adapter
		this.spinner.setAdapter(getSpinnerAdapter());

		// spinner listener
		this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
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
		final int position = Settings.getSearchModePref(requireContext());
		this.spinner.setSelection(position);
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

		// log
		Log.d(TAG, "Search text " + query);

		// subtitle
		final Toolbar toolbar = requireActivity().findViewById(org.sqlunet.browser.common.R.id.toolbar);
		assert toolbar != null;
		toolbar.setSubtitle(query);

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
				.replace(R.id.container_searchtext, fragment, FRAGMENT_TAG) //
				.addToBackStack(FRAGMENT_TAG) //
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
