/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.sqlunet.browser.vn.R;
import org.sqlunet.browser.vn.Settings;
import org.sqlunet.propbank.provider.PropBankContract;
import org.sqlunet.propbank.provider.PropBankProvider;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.verbnet.provider.VerbNetContract;
import org.sqlunet.verbnet.provider.VerbNetProvider;

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
		final int modePosition = Settings.getSearchModePref(requireContext());
		this.spinner.setSelection(modePosition);
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
				searchUri = VerbNetProvider.makeUri(VerbNetContract.Lookup_VnExamples_X.CONTENT_URI_TABLE);
				id = VerbNetContract.Lookup_VnExamples_X.EXAMPLEID;
				idType = "vnexample";
				target = VerbNetContract.Lookup_VnExamples_X.EXAMPLE;
				columns = new String[]{VerbNetContract.Lookup_VnExamples_X.EXAMPLE};
				hiddenColumns = new String[]{ //
						"GROUP_CONCAT(class || '@' || classid) AS " + VerbNetContract.Lookup_VnExamples_X.CLASSES};
				database = "vn";
				break;
			case 1:
				searchUri = PropBankProvider.makeUri(PropBankContract.Lookup_PbExamples_X.CONTENT_URI_TABLE);
				id = PropBankContract.Lookup_PbExamples_X.EXAMPLEID;
				idType = "pbexample";
				target = PropBankContract.Lookup_PbExamples_X.TEXT;
				columns = new String[]{PropBankContract.Lookup_PbExamples_X.TEXT};
				hiddenColumns = new String[]{ //
						"GROUP_CONCAT(rolesetname ||'@'||rolesetid) AS " + PropBankContract.Lookup_PbExamples_X.ROLESETS};
				database = "pb";
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
				.replace(R.id.container_searchtext, fragment) //
				.commit();
	}
}
