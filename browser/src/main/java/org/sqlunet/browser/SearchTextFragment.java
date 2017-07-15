package org.sqlunet.browser;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import org.sqlunet.browser.xn.Settings;
import org.sqlunet.framenet.provider.FrameNetContract;
import org.sqlunet.framenet.provider.FrameNetProvider;
import org.sqlunet.propbank.provider.PropBankContract;
import org.sqlunet.propbank.provider.PropBankProvider;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.verbnet.provider.VerbNetContract;
import org.sqlunet.verbnet.provider.VerbNetProvider;
import org.sqlunet.wordnet.provider.WordNetContract;
import org.sqlunet.wordnet.provider.WordNetProvider;

/**
 * Text search activity
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
		this.layoutId = R.layout.fragment_search_text;
		this.menuId = R.menu.text_search;
		this.colorId = R.color.textsearch_action_bar_color;
		this.spinnerLabels = R.array.textsearch_modes;
		this.spinnerIcons = R.array.textsearch_icons;
		this.titleId = R.string.title_textsearch_section;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		if (savedInstanceState == null)
		{
			// splash fragment
			final Fragment fragment = new SearchTextSplashFragment();
			getChildFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container_textsearch, fragment) //
					.commit();
		}

		return view;
	}

	// S P I N N E R

	@Override
	protected void setupSpinner(final Context context)
	{
		super.setupSpinner(context);

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
	 * @param query query
	 */
	@Override
	public void search(final String query)
	{
		// log
		Log.d(SearchTextFragment.TAG, "TEXT SEARCH " + query);

		// view
		final View view = getView();

		// copy to target view
		if (view != null)
		{
			final TextView targetView = (TextView) view.findViewById(R.id.targetView);
			if (targetView != null)
			{
				targetView.setText(query);
			}
		}

		// type
		final int typePosition = this.spinner.getSelectedItemPosition();

		// status
		// final CharSequence[] textSearches = getResources().getTextArray(R.array.textsearch_modes);

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
				searchUri = WordNetProvider.makeUri(WordNetContract.Lookup_Words.CONTENT_URI_TABLE);
				id = WordNetContract.Lookup_Words.WORDID;
				idType = "lemma";
				target = WordNetContract.Lookup_Words.LEMMA;
				columns = new String[]{WordNetContract.Lookup_Words.LEMMA};
				hiddenColumns = new String[]{WordNetContract.Lookup_Words.WORDID};
				database = "wn";
				break;
			case 1:
				searchUri = WordNetProvider.makeUri(WordNetContract.Lookup_Definitions.CONTENT_URI_TABLE);
				id = WordNetContract.Lookup_Definitions.SYNSETID;
				idType = "synset";
				target = WordNetContract.Lookup_Definitions.DEFINITION;
				columns = new String[]{WordNetContract.Lookup_Definitions.DEFINITION};
				hiddenColumns = new String[]{WordNetContract.Lookup_Definitions.SYNSETID};
				database = "wn";
				break;
			case 2:
				searchUri = WordNetProvider.makeUri(WordNetContract.Lookup_Samples.CONTENT_URI_TABLE);
				id = WordNetContract.Lookup_Samples.SYNSETID;
				idType = "synset";
				target = WordNetContract.Lookup_Samples.SAMPLE;
				columns = new String[]{WordNetContract.Lookup_Samples.SAMPLE};
				hiddenColumns = new String[]{WordNetContract.Lookup_Samples.SYNSETID};
				database = "wn";
				break;
			case 3:
				searchUri = VerbNetProvider.makeUri(VerbNetContract.Lookup_VnExamples_X.CONTENT_URI_TABLE);
				id = VerbNetContract.Lookup_VnExamples_X.EXAMPLEID;
				idType = "vnexample";
				target = VerbNetContract.Lookup_VnExamples_X.EXAMPLE;
				columns = new String[]{VerbNetContract.Lookup_VnExamples_X.EXAMPLE};
				hiddenColumns = new String[]{ //
						"GROUP_CONCAT(class || '@' || classid) AS " + VerbNetContract.Lookup_VnExamples_X.CLASSES};
				database = "vn";
				break;
			case 4:
				searchUri = PropBankProvider.makeUri(PropBankContract.Lookup_PbExamples_X.CONTENT_URI_TABLE);
				id = PropBankContract.Lookup_PbExamples_X.EXAMPLEID;
				idType = "pbexample";
				target = PropBankContract.Lookup_PbExamples_X.TEXT;
				columns = new String[]{PropBankContract.Lookup_PbExamples_X.TEXT};
				hiddenColumns = new String[]{ //
						"GROUP_CONCAT(rolesetname ||'@'||rolesetid) AS " + PropBankContract.Lookup_PbExamples_X.ROLESETS};
				database = "pb";
				break;
			case 5:
				searchUri = FrameNetProvider.makeUri(FrameNetContract.Lookup_FnSentences_X.CONTENT_URI_TABLE);
				id = FrameNetContract.Lookup_FnSentences_X.SENTENCEID;
				idType = "fnsentence";
				target = FrameNetContract.Lookup_FnSentences_X.TEXT;
				columns = new String[]{FrameNetContract.Lookup_FnSentences_X.TEXT};
				hiddenColumns = new String[]{FrameNetContract.Lookup_FnSentences_X.SENTENCEID, //
						"GROUP_CONCAT(DISTINCT  frame || '@' || frameid) AS " + FrameNetContract.Lookup_FnSentences_X.FRAMES, //
						"GROUP_CONCAT(DISTINCT  lexunit || '@' || luid) AS " + FrameNetContract.Lookup_FnSentences_X.LEXUNITS};
				database = "fn";
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
		args.putInt(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_search_text);
		args.putString(ProviderArgs.ARG_QUERYDATABASE, database);

		// fragment
		final Fragment fragment = new TextFragment();
		fragment.setArguments(args);
		getChildFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_textsearch, fragment) //
				.commit();
	}
}
