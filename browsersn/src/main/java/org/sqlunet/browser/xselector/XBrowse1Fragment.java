/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.xselector;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.Browse2Activity;
import org.sqlunet.browser.Browse2Fragment;
import org.sqlunet.browser.sn.R;
import org.sqlunet.browser.Selectors;
import org.sqlunet.browser.selector.CollocationSelectorPointer;
import org.sqlunet.browser.selector.SelectorPointer;
import org.sqlunet.browser.selector.SelectorsFragment;
import org.sqlunet.browser.selector.SnSelectorsFragment;
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
public class XBrowse1Fragment extends Fragment implements SelectorsFragment.Listener, SnSelectorsFragment.Listener
{
	/**
	 * Selectors fragment
	 */
	@SuppressWarnings("FieldCanBeLocal")
	@Nullable
	private XSelectorsFragment xSelectorsFragment;

	// C R E A T I O N

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(Settings.getPaneLayout(R.layout.fragment_xbrowse_first, R.layout.fragment_xbrowse1, R.layout.fragment_xbrowse1_browse2), container, false);

		// manager
		final FragmentManager manager = getChildFragmentManager();

		// x selector fragment
		this.xSelectorsFragment = (XSelectorsFragment) manager.findFragmentByTag("browse1");
		if (this.xSelectorsFragment == null)
		{
			this.xSelectorsFragment = new XSelectorsFragment();
			Bundle args = getArguments();
			if (args == null)
			{
				args = new Bundle();
			}
			boolean isTwoPane = isTwoPane(view);
			args.putBoolean(Selectors.IS_TWO_PANE, isTwoPane);
			this.xSelectorsFragment.setArguments(args);
		}
		this.xSelectorsFragment.setListener(this, this);

		// transaction on selectors pane
		manager.beginTransaction() //
				.replace(R.id.container_xselectors, this.xSelectorsFragment, "browse1") //
				.commit();

		// two-pane specific set up
		if (isTwoPane(view))
		{
			// detail fragment (rigid layout)
			Fragment browse2Fragment = manager.findFragmentByTag("browse2");
			if (browse2Fragment == null)
			{
				browse2Fragment = new Browse2Fragment();
				Bundle args = new Bundle();
				args.putBoolean(Browse2Fragment.ARG_ALT, false);
				browse2Fragment.setArguments(args);
			}
			manager.beginTransaction() //
					.replace(R.id.container_browse2, browse2Fragment, "browse2") //
					.commit();
		}

		return view;
	}

	// I T E M S E L E C T I O N H A N D L I N G

	/**
	 * Callback method from {@link SelectorsFragment.Listener} indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(@NonNull final SelectorPointer pointer, final String word, final String cased, final String pos)
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
			fragment.search(pointer, pos);
		}
		else
		{
			// in single-pane mode, simply start the detail activity for the selected item ID.
			final int recurse = Settings.getRecursePref(requireContext());
			final Bundle args = new Bundle();
			args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);
			args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);
			args.putString(ProviderArgs.ARG_HINTWORD, word);
			args.putString(ProviderArgs.ARG_HINTCASED, cased);
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
			fragment.search(pointer, null);
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
