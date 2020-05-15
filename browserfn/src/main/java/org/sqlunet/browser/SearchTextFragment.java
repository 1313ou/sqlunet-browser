/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.fn.R;
import org.sqlunet.framenet.provider.FrameNetContract.Lookup_FnSentences_X;
import org.sqlunet.framenet.provider.FrameNetProvider;
import org.sqlunet.provider.ProviderArgs;

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
		if (modePosition == 0)
		{
			searchUri = FrameNetProvider.makeUri(Lookup_FnSentences_X.CONTENT_URI_TABLE);
			id = Lookup_FnSentences_X.SENTENCEID;
			idType = "fnsentence";
			target = Lookup_FnSentences_X.TEXT;
			columns = new String[]{Lookup_FnSentences_X.TEXT};
			hiddenColumns = new String[]{Lookup_FnSentences_X.SENTENCEID, //
					"GROUP_CONCAT(DISTINCT  frame || '@' || frameid) AS " + Lookup_FnSentences_X.FRAMES, //
					"GROUP_CONCAT(DISTINCT  lexunit || '@' || luid) AS " + Lookup_FnSentences_X.LEXUNITS};
			database = "fn";
		}
		else
		{
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
