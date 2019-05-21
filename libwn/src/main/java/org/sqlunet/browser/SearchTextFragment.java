/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.sqlunet.browser.wn.Settings;
import org.sqlunet.browser.wn.lib.R;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.wordnet.provider.WordNetContract;
import org.sqlunet.wordnet.provider.WordNetProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
		this.colorId = R.color.searchtext_actionbar_color;
		this.spinnerLabels = R.array.searchtext_modes;
		this.spinnerIcons = R.array.searchtext_icons;
		this.titleId = R.string.title_searchtext_section;
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		if (savedInstanceState == null)
		{
			// splash fragment
			final Fragment fragment = new SearchTextSplashFragment();
			getChildFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container_searchtext, fragment) //
					.commit();
		}

		return view;
	}

	// S P I N N E R

	@Override
	protected void setupSpinner(@NonNull final Context context)
	{
		super.setupSpinner(context);
		this.spinner.setVisibility(View.VISIBLE);

		// spinner listener
		this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position, final long id)
			{
				Settings.setSearchModePref(context, position);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parentView)
			{
				//
			}
		});

		// spinner position
		final int position = Settings.getSearchModePref(context);
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
		Log.d(SearchTextFragment.TAG, "SEARCH TEXT " + query);

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

		// type
		final int typePosition = this.spinner.getSelectedItemPosition();

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
		switch (typePosition)
		{
			case 0:
				searchUri = WordNetProvider.makeUri(WordNetContract.Lookup_Definitions.CONTENT_URI_TABLE);
				id = WordNetContract.Lookup_Definitions.SYNSETID;
				idType = "synset";
				target = WordNetContract.Lookup_Definitions.DEFINITION;
				columns = new String[]{WordNetContract.Lookup_Definitions.DEFINITION};
				hiddenColumns = new String[]{WordNetContract.Lookup_Definitions.SYNSETID};
				database = "wn";
				break;
			case 1:
				searchUri = WordNetProvider.makeUri(WordNetContract.Lookup_Samples.CONTENT_URI_TABLE);
				id = WordNetContract.Lookup_Samples.SYNSETID;
				idType = "synset";
				target = WordNetContract.Lookup_Samples.SAMPLE;
				columns = new String[]{WordNetContract.Lookup_Samples.SAMPLE};
				hiddenColumns = new String[]{WordNetContract.Lookup_Samples.SYNSETID};
				database = "wn";
				break;
			case 2:
				searchUri = WordNetProvider.makeUri(WordNetContract.Lookup_Words.CONTENT_URI_TABLE);
				id = WordNetContract.Lookup_Words.WORDID;
				idType = "lemma";
				target = WordNetContract.Lookup_Words.LEMMA;
				columns = new String[]{WordNetContract.Lookup_Words.LEMMA};
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
		getChildFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_searchtext, fragment) //
				.commit();
	}
}
