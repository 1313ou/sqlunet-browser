/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.vn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.sqlunet.browser.BaseBrowse1Fragment;
import org.sqlunet.browser.BaseSearchFragment;
import org.sqlunet.browser.BrowseSplashFragment;
import org.sqlunet.browser.SplashFragment;
import org.sqlunet.browser.vn.Settings.Selector;
import org.sqlunet.browser.vn.xselector.XBrowse1Fragment;
import org.sqlunet.browser.vn.web.WebActivity;
import org.sqlunet.browser.vn.web.WebFragment;
import org.sqlunet.browser.vn.xselector.XBrowse1Activity;
import org.sqlunet.history.History;
import org.sqlunet.propbank.PbRoleSetPointer;
import org.sqlunet.propbank.browser.PbRoleSetActivity;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.verbnet.VnClassPointer;
import org.sqlunet.verbnet.browser.VnClassActivity;
import org.sqlunet.wordnet.SynsetPointer;
import org.sqlunet.wordnet.WordPointer;
import org.sqlunet.wordnet.browser.SynsetActivity;
import org.sqlunet.wordnet.browser.WordActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

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
	}

	@Override
	public void onStop()
	{
		super.onStop();

		// remove data fragments and replace with splash before onSaveInstanceState takes place (between -3 and -4)
		beforeSaving(new BrowseSplashFragment(), SplashFragment.FRAGMENT_TAG, R.id.container_browse, BaseBrowse1Fragment.FRAGMENT_TAG);
	}

	// M E N U

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		return false;
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

		// super
		super.search(query);

		// log
		Log.d(TAG, "Browse '" + query + '\'');

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

		// menuDispatch as per query prefix
		@SuppressWarnings("TooBroadScope") Fragment fragment;
		Intent targetIntent = null;
		Bundle args = new Bundle();
		if (query.matches("#\\p{Lower}\\p{Lower}\\d+"))
		{
			final long id = Long.parseLong(query.substring(3));

			// parameters
			final Bundle parameters = org.sqlunet.wordnet.settings.Settings.getRenderParametersPref(requireContext());

			// wordnet
			if (query.startsWith("#ws"))
			{
				final Parcelable synsetPointer = new SynsetPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, synsetPointer);
				args.putInt(ProviderArgs.ARG_QUERYRECURSE, 0);
				args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters);

				targetIntent = makeDetailIntent(SynsetActivity.class);
			}
			else if (query.startsWith("#ww"))
			{
				final Parcelable wordPointer = new WordPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_WORD);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, wordPointer);
				args.putInt(ProviderArgs.ARG_QUERYRECURSE, 0);
				args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters);

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

			else
			{
				return;
			}
		}
		{
			// search for string
			args.putString(ProviderArgs.ARG_QUERYSTRING, query);

			//targetIntent = makeSelectorIntent();
			fragment = makeOverviewFragment();
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
					.replace(R.id.container_browse, fragment, BaseBrowse1Fragment.FRAGMENT_TAG) //
					.addToBackStack(BaseBrowse1Fragment.FRAGMENT_TAG).commit();
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
	 * Browse1/Web fragment factory
	 *
	 * @return fragment
	 */
	@Nullable
	private Fragment makeOverviewFragment()
	{
		// context
		final Context context = requireContext();

		// type
		final Selector selectorType = Settings.getXSelectorPref(context);

		// mode
		final Settings.SelectorViewMode selectorMode = Settings.getSelectorViewModePref(context);

		switch (selectorMode)
		{
			case VIEW:
				if (selectorType == Selector.XSELECTOR)
				{
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
	private Intent makeSelectorIntent()
	{
		// context
		final Context context = requireContext();

		// intent
		Intent intent = null;

		// type
		final Selector selectorType = Settings.getXSelectorPref(context);

		// mode
		final Settings.SelectorViewMode selectorMode = Settings.getSelectorViewModePref(context);

		switch (selectorMode)
		{
			case VIEW:
				Class<?> intentClass = null;
				if (selectorType == Selector.XSELECTOR)
				{
					intentClass = XBrowse1Activity.class;
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
