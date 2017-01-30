package org.sqlunet.browser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.sqlunet.framenet.provider.FrameNetContract.Lookup_FnSentences_X;
import org.sqlunet.propbank.provider.PropBankContract.Lookup_PbExamples_X;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.settings.Settings;
import org.sqlunet.verbnet.provider.VerbNetContract.Lookup_VnExamples_X;
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Definitions;
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Samples;
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Words;

/**
 * Text search activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TextSearchFragment extends BaseSearchFragment
{
	static private final String TAG = "TextSearchActivity";

	// C R E A T I O N

	/**
	 * Constructor
	 */
	public TextSearchFragment()
	{
		this.layoutId = R.layout.fragment_textsearch;
		this.menuId = R.menu.text_search;
		this.colorId = R.color.textsearch_action_bar_color;
		this.spinnerLabels = R.array.textsearch_modes;
		this.spinnerIcons = R.array.textsearch_icons;
	}

	// S P I N N E R

	@Override
	protected void setupSpinner(final Spinner spinner)
	{
		super.setupSpinner(spinner);

		// spinner listener
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position, final long id)
			{
				Settings.setSearchModePref(TextSearchFragment.this.getActivity(), position);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parentView)
			{
				//
			}
		});

		// spinner position
		final int position = Settings.getSearchModePref(getActivity());
		spinner.setSelection(position);
	}

	// S E A R C H

	/**
	 * Handle query
	 *
	 * @param query query
	 */
	@Override
	public void search(final String query)
	{
		// log
		Log.d(TextSearchFragment.TAG, "TEXT SEARCH " + query);

		// set
		this.query = query;

		// type
		final int typePosition = this.spinner.getSelectedItemPosition();

		// status
		// final CharSequence[] textSearches = getResources().getTextArray(R.array.textsearch_modes);

		// as per selected mode
		String searchUri;
		String id;
		String target;
		String[] columns;
		String[] hiddenColumns;
		String database = null;
		switch (typePosition)
		{
			case 0:
				searchUri = Lookup_Words.CONTENT_URI;
				id = Lookup_Words.WORDID;
				target = Lookup_Words.LEMMA;
				columns = new String[]{Lookup_Words.LEMMA};
				hiddenColumns = new String[]{Lookup_Words.WORDID};
				break;
			case 1:
				searchUri = Lookup_Definitions.CONTENT_URI;
				id = Lookup_Definitions.SYNSETID;
				target = Lookup_Definitions.DEFINITION;
				columns = new String[]{Lookup_Definitions.DEFINITION};
				hiddenColumns = new String[]{Lookup_Definitions.SYNSETID};
				database = "wn";
				break;
			case 2:
				searchUri = Lookup_Samples.CONTENT_URI;
				id = Lookup_Samples.SYNSETID;
				target = Lookup_Samples.SAMPLE;
				columns = new String[]{Lookup_Samples.SAMPLE};
				hiddenColumns = new String[]{Lookup_Samples.SYNSETID};
				database = "wn";
				break;
			case 3:
				searchUri = Lookup_VnExamples_X.CONTENT_URI;
				id = Lookup_VnExamples_X.EXAMPLEID;
				target = Lookup_VnExamples_X.EXAMPLE;
				columns = new String[]{Lookup_VnExamples_X.EXAMPLE};
				hiddenColumns = new String[]{ //
						"GROUP_CONCAT(class || '@' || classid) AS " + Lookup_VnExamples_X.CLASSES};
				database = "vn";
				break;
			case 4:
				searchUri = Lookup_PbExamples_X.CONTENT_URI;
				id = Lookup_PbExamples_X.EXAMPLEID;
				target = Lookup_PbExamples_X.TEXT;
				columns = new String[]{Lookup_PbExamples_X.TEXT};
				hiddenColumns = new String[]{ //
						"GROUP_CONCAT(rolesetname ||'@'||rolesetid) AS " + Lookup_PbExamples_X.ROLESETS};
				database = "pb";
				break;
			case 5:
				searchUri = Lookup_FnSentences_X.CONTENT_URI;
				id = Lookup_FnSentences_X.SENTENCEID;
				target = Lookup_FnSentences_X.TEXT;
				columns = new String[]{Lookup_FnSentences_X.TEXT};
				hiddenColumns = new String[]{Lookup_FnSentences_X.SENTENCEID, //
						"GROUP_CONCAT(DISTINCT  frame || '@' || frameid) AS " + Lookup_FnSentences_X.FRAMES, //
						"GROUP_CONCAT(DISTINCT  lexunit || '@' || luid) AS " + Lookup_FnSentences_X.LEXUNITS};
				database = "fn";
				break;
			default:
				return;
		}

		// parameters
		final Bundle args = new Bundle();
		args.putString(ProviderArgs.ARG_QUERYURI, searchUri);
		args.putString(ProviderArgs.ARG_QUERYID, id);
		args.putStringArray(ProviderArgs.ARG_QUERYITEMS, columns);
		args.putStringArray(ProviderArgs.ARG_QUERYHIDDENITEMS, hiddenColumns);
		args.putString(ProviderArgs.ARG_QUERYFILTER, target + " MATCH ?");
		args.putString(ProviderArgs.ARG_QUERYARG, query);
		args.putInt(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table1);
		if (database != null)
		{
			args.putString(ProviderArgs.ARG_QUERYDATABASE, database);
		}

		// view
		final View view = getView();

		// clear splash
		assert view != null;
		final ViewGroup container = (ViewGroup) view.findViewById(R.id.container_textsearch);
		container.removeAllViews();

		// fragment
		final Fragment fragment = new TextSearchResultFragment();
		fragment.setArguments(args);
		getChildFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_textsearch, fragment) //
				.commit();
	}
}
