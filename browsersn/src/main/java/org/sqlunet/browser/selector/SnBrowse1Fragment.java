/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.selector;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.R;
import org.sqlunet.browser.SnBrowse2Activity;
import org.sqlunet.browser.SnBrowse2Fragment;
import org.sqlunet.browser.xn.Settings;
import org.sqlunet.provider.ProviderArgs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Selector fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SnBrowse1Fragment extends Fragment implements SnSelectorsFragment.Listener
{
	public static final String IS_TWO_PANE = "is_two_pane";

	/**
	 * Sn Selectors fragment
	 */
	@Nullable
	private SnSelectorsFragment selectorsFragment;

	// C R E A T I O N

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_snbrowse_first, container, false);

		// retain instance
		setRetainInstance(true);

		// manager
		final FragmentManager manager = getChildFragmentManager();

		// selector fragment
		this.selectorsFragment = (SnSelectorsFragment) manager.findFragmentByTag("snbrowse1");
		if (this.selectorsFragment == null)
		{
			this.selectorsFragment = new SnSelectorsFragment();
			Bundle args = getArguments();
			boolean isTwoPane = isTwoPane(view);
			args.putBoolean(IS_TWO_PANE, isTwoPane);
			this.selectorsFragment.setArguments(args);
			this.selectorsFragment.setListener(this);
		}

		// transaction on selectors pane
		manager.beginTransaction() //
				.replace(R.id.container_selectors, this.selectorsFragment, "snbrowse1") //
				.commit();

		// two-pane specific set up
		if (isTwoPane(view))
		{
			// detail fragment (rigid layout)
			Fragment snBrowse2Fragment = manager.findFragmentByTag("snbrowse2");
			if (snBrowse2Fragment == null)
			{
				snBrowse2Fragment = new SnBrowse2Fragment();
				final Bundle args = new Bundle();
				args.putBoolean(SnBrowse2Fragment.ARG_ALT, false);
				snBrowse2Fragment.setArguments(args);
			}
			manager.beginTransaction() //
					.replace(R.id.container_browse2, snBrowse2Fragment, "snbrowse2") //
					.commit();
		}

		return view;
	}

	// I T E M S E L E C T I O N H A N D L I N G

	/**
	 * Callback method from {@link SnSelectorsFragment.Listener} indicating that the item with the given ID was selected.
	 */

	@Override
	public void onItemSelected(final CollocationSelectorPointer pointer)
	{
		final View view = getView();
		assert view != null;
		if (isTwoPane(view))
		{
			// in two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using a fragment transaction.
			final SnBrowse2Fragment fragment = (SnBrowse2Fragment) getChildFragmentManager().findFragmentById(R.id.container_browse2);
			assert fragment != null;
			fragment.search(pointer, null);
		}
		else
		{
			// in single-pane mode, simply start the detail activity for the selected item ID.
			final int recurse = Settings.getRecursePref(getContext());
			final Bundle args = new Bundle();
			args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);
			args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);

			final Intent intent = new Intent(requireContext(), SnBrowse2Activity.class);
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
