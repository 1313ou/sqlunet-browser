/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.sn.xselector;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.BaseBrowse1Fragment;
import org.sqlunet.browser.BaseBrowse2Fragment;
import org.sqlunet.browser.BaseSelectorsFragment;
import org.sqlunet.browser.Browse2Activity;
import org.sqlunet.browser.sn.Browse2Fragment;
import org.sqlunet.browser.Selectors;
import org.sqlunet.browser.selector.CollocationSelectorPointer;
import org.sqlunet.browser.selector.SelectorPointer;
import org.sqlunet.browser.sn.selector.SelectorsFragment;
import org.sqlunet.browser.sn.selector.SnSelectorsFragment;
import org.sqlunet.browser.sn.R;
import org.sqlunet.browser.sn.Settings;
import org.sqlunet.provider.ProviderArgs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * X selector fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class XBrowse1Fragment extends BaseBrowse1Fragment implements SelectorsFragment.Listener, SnSelectorsFragment.Listener
{
	// C R E A T I O N

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		return inflater.inflate(Settings.getPaneLayout(R.layout.fragment_xbrowse_first, R.layout.fragment_xbrowse1, R.layout.fragment_xbrowse1_browse2), container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		boolean isTwoPane = isTwoPane(view);

		// manager
		assert isAdded();
		final FragmentManager manager = getChildFragmentManager();

		// x selector fragment
		// transaction on selectors pane
		XSelectorsFragment xSelectorsFragment;
		//xSelectorsFragment = (XSelectorsFragment) manager.findFragmentByTag(BaseSelectorsFragment.FRAGMENT_TAG);
		//if (xSelectorsFragment == null)
		{
			xSelectorsFragment = new XSelectorsFragment();
			xSelectorsFragment.setArguments(getArguments());
		}
		Bundle args1 = xSelectorsFragment.getArguments();
		if (args1 == null)
		{
			args1 = new Bundle();
		}
		args1.putBoolean(Selectors.IS_TWO_PANE, isTwoPane);
		xSelectorsFragment.setListener(this, this);
		manager.beginTransaction() //
				.replace(R.id.container_xselectors, xSelectorsFragment, BaseSelectorsFragment.FRAGMENT_TAG) //
				// .addToBackStack(BaseSelectorsFragment.FRAGMENT_TAG) //
				.commit();

		// two-pane specific set up
		if (isTwoPane)
		{
			// in two-pane mode, list items should be given the 'activated' state when touched.
			// xSelectorsFragment.setActivateOnItemClick(true);

			// detail fragment (rigid layout)
			Fragment browse2Fragment;
			// browse2Fragment = manager.findFragmentByTag(BaseBrowse2Fragment.FRAGMENT_TAG);
			// if (browse2Fragment == null)
			{
				browse2Fragment = new Browse2Fragment();
				final Bundle args2 = new Bundle();
				args2.putBoolean(Browse2Fragment.ARG_ALT, false);
				browse2Fragment.setArguments(args2);
			}
			manager.beginTransaction() //
					.setReorderingAllowed(true) //
					.replace(R.id.container_browse2, browse2Fragment, BaseBrowse2Fragment.FRAGMENT_TAG) //
					// .addToBackStack(BaseBrowse2Fragment.FRAGMENT_TAG) //
					.commit();
		}
	}

	// I T E M S E L E C T I O N H A N D L I N G

	/**
	 * Callback method from {@link SelectorsFragment.Listener} indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(@NonNull final SelectorPointer pointer, final String word, final String cased, final String pronunciation, final String pos)
	{
		final View view = getView();
		assert view != null;
		if (isTwoPane(view))
		{
			// in two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using a fragment transaction.
			if (!isAdded())
			{
				return;
			}
			final Browse2Fragment fragment = (Browse2Fragment) getChildFragmentManager().findFragmentById(R.id.container_browse2);
			assert fragment != null;
			fragment.search(pointer, word, cased, pronunciation, pos);
		}
		else
		{
			// in single-pane mode, simply start the detail activity for the selected item ID.
			final int recurse = org.sqlunet.wordnet.settings.Settings.getRecursePref(requireContext());
			final Bundle parameters = org.sqlunet.wordnet.settings.Settings.getRenderParametersPref(requireContext());

			final Bundle args = new Bundle();
			args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);
			args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);
			args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters);
			args.putString(ProviderArgs.ARG_HINTWORD, word);
			args.putString(ProviderArgs.ARG_HINTCASED, cased);
			args.putString(ProviderArgs.ARG_HINTPRONUNCIATION, pronunciation);
			args.putString(ProviderArgs.ARG_HINTPOS, pos);
			final Intent intent = new Intent(requireContext(), Browse2Activity.class);
			intent.putExtras(args);
			startActivity(intent);
		}
	}

	@Override
	public void onItemSelected(final CollocationSelectorPointer pointer)
	{
		final View view = getView();
		assert view != null;
		if (isTwoPane(view))
		{
			// in two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using a fragment transaction.
			if (!isAdded())
			{
				return;
			}
			final Browse2Fragment fragment = (Browse2Fragment) getChildFragmentManager().findFragmentById(R.id.container_browse2);
			assert fragment != null;
			fragment.search(pointer, null, null, null, null);
		}
		else
		{
			// in single-pane mode, simply start the detail activity for the selected item ID.
			final Bundle args = new Bundle();
			args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);
			args.putBoolean(Browse2Fragment.ARG_ALT, true);
			final Intent intent = new Intent(requireContext(), Browse2Activity.class);
			intent.putExtras(args);
			startActivity(intent);
		}
	}

	// V I E W   D E T E C T I O N

	/**
	 * Whether view is two-pane
	 *
	 * @param view view
	 * @return true if view is two-pane
	 */
	private boolean isTwoPane(@NonNull final View view)
	{
		// the detail view will be present only in the large-screen layouts
		// if this view is present, then the activity should be in two-pane mode.
		return view.findViewById(R.id.detail) != null;
	}
}
