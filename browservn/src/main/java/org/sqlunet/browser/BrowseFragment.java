package org.sqlunet.browser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.vn.R;
import org.sqlunet.browser.vn.Settings;
import org.sqlunet.browser.vn.Settings.Selector;
import org.sqlunet.browser.web.WebActivity;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.browser.xselector.XBrowse1Activity;
import org.sqlunet.browser.xselector.XBrowse1Fragment;
import org.sqlunet.propbank.PbRoleSetPointer;
import org.sqlunet.propbank.browser.PbRoleSetActivity;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.verbnet.VnClassPointer;
import org.sqlunet.verbnet.browser.VnClassActivity;
import org.sqlunet.wordnet.SynsetPointer;
import org.sqlunet.wordnet.WordPointer;
import org.sqlunet.wordnet.browser.SynsetActivity;
import org.sqlunet.wordnet.browser.WordActivity;

/**
 * Browse fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BrowseFragment extends BaseSearchFragment
{
	static private final String TAG = "BrowseFragment";

	// C R E A T I O N

	/**
	 * Constructor
	 */
	public BrowseFragment()
	{
		this.layoutId = R.layout.fragment_browse;
		this.menuId = R.menu.browse;
		this.colorId = R.color.browse_actionbar_color;
		this.spinnerLabels = R.array.selectors_names;
		this.spinnerIcons = R.array.selectors_icons;
		this.titleId = R.string.title_browse_section;
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
					.replace(R.id.container_browse, fragment) //
					.commit();
		}

		return view;
	}

	// S P I N N E R

	@Override
	protected void setupSpinner(@NonNull final Context context)
	{
		super.setupSpinner(context);
		this.spinner.setVisibility(View.GONE);

		/*
		// spinner listener
		this.spinner.setOnItemSelectedListener( //
				new OnItemSelectedListener()
				{
					@Override
					public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position, final long id)
					{
						final Selector selectorMode = Selector.values()[position];
						selectorMode.setPref(context);
					}

					@Override
					public void onNothingSelected(final AdapterView<?> parentView)
					{
						//
					}
				});

		// saved selector mode
		final Selector selectorMode = Selector.getPref(context);
		if (selectorMode != null)
		{
			this.spinner.setSelection(selectorMode.ordinal());
		}
		*/
	}

	// M E N U

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
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

		// log
		Log.d(BrowseFragment.TAG, "BROWSE " + query);

		// subtitle
		final AppCompatActivity activity = (AppCompatActivity) getActivity();
		assert activity != null;
		final ActionBar actionBar = activity.getSupportActionBar();
		assert actionBar != null;
		actionBar.setSubtitle(query);

		/*
		// copy to target view
		final View view = getView();
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
			final long id = Long.valueOf(query.substring(3));

			// wordnet
			if (query.startsWith("#ws"))
			{
				final Parcelable synsetPointer = new SynsetPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, synsetPointer);
				args.putInt(ProviderArgs.ARG_QUERYRECURSE, 0);

				targetIntent = makeDetailIntent(SynsetActivity.class);
			}
			else if (query.startsWith("#ww"))
			{
				final Parcelable wordPointer = new WordPointer(id);
				args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_WORD);
				args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, wordPointer);
				args.putInt(ProviderArgs.ARG_QUERYRECURSE, 0);

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
			fragment = makeSelectorFragment();
			//fragment = new Test1Fragment();
		}

		// menuDispatch
		Log.d(BrowseFragment.TAG, "SEARCH " + args);
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
			getChildFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container_browse, fragment) //
					.commit();

		}
	}

	// I N T E N T / F R A G M E N T   F A C T O R Y

	/**
	 * Fragment factory
	 *
	 * @return fragment
	 */
	private Fragment makeSelectorFragment()
	{
		// activity
		final Activity activity = getActivity();

		// type
		final Selector selectorType = Settings.getXSelectorPref(activity);

		// mode
		final Settings.SelectorViewMode selectorMode = Settings.getSelectorViewModePref(activity);

		switch (selectorMode)
		{
			case VIEW:
				switch (selectorType)
				{
					case XSELECTOR:
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
	@SuppressWarnings("unused")
	private Intent makeSelectorIntent()
	{
		// activity
		final Activity activity = getActivity();

		// intent
		Intent intent = null;

		// type
		final Selector selectorType = Settings.getXSelectorPref(activity);

		// mode
		final Settings.SelectorViewMode selectorMode = Settings.getSelectorViewModePref(activity);

		switch (selectorMode)
		{
			case VIEW:
				Class<?> intentClass = null;
				switch (selectorType)
				{
					case XSELECTOR:
						intentClass = XBrowse1Activity.class;
						break;
				}
				intent = new Intent(getActivity(), intentClass);
				break;

			case WEB:
				intent = new Intent(getActivity(), WebActivity.class);
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
		// activity
		final Activity activity = getActivity();

		// intent
		Intent intent = null;

		// mode
		final Settings.DetailViewMode detailMode = Settings.getDetailViewModePref(activity);
		switch (detailMode)
		{
			case VIEW:
				intent = new Intent(getActivity(), intentClass);
				break;

			case WEB:
				intent = new Intent(getActivity(), WebActivity.class);
				break;
		}
		intent.setAction(ProviderArgs.ACTION_QUERY);

		return intent;
	}
}
