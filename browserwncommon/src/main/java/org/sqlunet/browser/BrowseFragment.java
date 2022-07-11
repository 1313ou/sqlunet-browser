/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
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

import org.sqlunet.browser.config.TableActivity;
import org.sqlunet.browser.selector.Browse1Activity;
import org.sqlunet.browser.selector.Browse1Fragment;
import org.sqlunet.browser.web.WebActivity;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.browser.wn.Settings;
import org.sqlunet.browser.wn.lib.R;
import org.sqlunet.provider.ProviderArgs;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		if (savedInstanceState == null)
		{
			// splash fragment
			final Fragment fragment = new BrowseSplashFragment();
			getChildFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container_browse, fragment, "fragment_splash") //
					.commit();
		}

		return view;
	}

	// M E N U

	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		// intent
		Intent intent;

		// handle item selection
		final int itemId = item.getItemId();
		if (itemId == R.id.action_table_domains)
		{
			intent = new Intent(requireContext(), TableActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYURI, WordNetProvider.makeUri(Domains.URI));
			intent.putExtra(ProviderArgs.ARG_QUERYID, Domains.DOMAINID);
			intent.putExtra(ProviderArgs.ARG_QUERYITEMS, new String[]{Domains.DOMAINID, Domains.DOMAIN, Domains.POSID});
			intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table3);
		}
		else if (itemId == R.id.action_table_poses)
		{
			intent = new Intent(requireContext(), TableActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYURI, WordNetProvider.makeUri(Poses.URI));
			intent.putExtra(ProviderArgs.ARG_QUERYID, Poses.POSID);
			intent.putExtra(ProviderArgs.ARG_QUERYITEMS, new String[]{Poses.POSID, Poses.POS});
			intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table2);
		}
		else if (itemId == R.id.action_table_adjpositions)
		{
			intent = new Intent(requireContext(), TableActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYURI, WordNetProvider.makeUri(AdjPositions.URI));
			intent.putExtra(ProviderArgs.ARG_QUERYID, AdjPositions.POSITIONID);
			intent.putExtra(ProviderArgs.ARG_QUERYITEMS, new String[]{AdjPositions.POSITIONID, AdjPositions.POSITION});
			intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table2);
		}
		else if (itemId == R.id.action_table_relations)
		{
			intent = new Intent(requireContext(), TableActivity.class);
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
		Log.d(BrowseFragment.TAG, "BROWSE " + query);

		// subtitle
		// final AppCompatActivity activity = (AppCompatActivity) requireActivity();
		// final ActionBar actionBar = activity.getSupportActionBar();
		// assert actionBar != null;
		// actionBar.setSubtitle(query);
		final Toolbar toolbar = requireActivity().findViewById(org.sqlunet.browser.common.R.id.toolbar_search);
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
				final int recurse = Settings.getRecursePref(requireContext());
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

				targetIntent = makeDetailIntent(WordActivity.class);
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
				final int recurse = Settings.getRecursePref(requireContext());
				final Parcelable senseKeyPointer = new SenseKeyPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SENSE);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, senseKeyPointer);
				args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);

				targetIntent = makeDetailIntent(SenseKeyActivity.class);
			}
		}
		else
		{
			final int recurse = Settings.getRecursePref(requireContext());

			// search for string
			args.putString(ProviderArgs.ARG_QUERYSTRING, query);
			args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);

			//targetIntent = makeSelectorIntent();
			fragment = makeSelectorFragment();
			//fragment = new Test1Fragment();
		}

		// menuDispatch
		Log.d(BrowseFragment.TAG, "Search " + args);
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
					.replace(R.id.container_browse, fragment) //
					.commit();

		}
	}

	@Override
	protected boolean triggerFocusSearch()
	{
		final Fragment active = getChildFragmentManager().findFragmentById(R.id.container_browse);
		return active != null && "fragment_splash".equals(active.getTag());
	}

	// I N T E N T / F R A G M E N T   F A C T O R Y

	/**
	 * Fragment factory
	 *
	 * @return fragment
	 */
	@Nullable
	private Fragment makeSelectorFragment()
	{
		// context
		final Context context = requireContext();

		// type
		final Settings.Selector selectorType = Settings.getSelectorPref(context);

		// mode
		final Settings.SelectorViewMode selectorMode = Settings.getSelectorViewModePref(context);

		switch (selectorMode)
		{
			case VIEW:
				if (selectorType == org.sqlunet.settings.Settings.Selector.SELECTOR)
				{
					return new Browse1Fragment();
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
	private Intent makeSelectorIntent()
	{
		// context
		final Context context = requireContext();

		// intent
		Intent intent = null;

		// type
		final Settings.Selector selectorType = Settings.getSelectorPref(context);

		// mode
		final Settings.SelectorViewMode selectorMode = Settings.getSelectorViewModePref(context);

		switch (selectorMode)
		{
			case VIEW:
				Class<?> intentClass = null;
				if (selectorType == org.sqlunet.settings.Settings.Selector.SELECTOR)
				{
					intentClass = Browse1Activity.class;
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
				intent = new Intent(requireContext(), WebActivity.class);
				break;
		}
		intent.setAction(ProviderArgs.ACTION_QUERY);

		return intent;
	}
}
