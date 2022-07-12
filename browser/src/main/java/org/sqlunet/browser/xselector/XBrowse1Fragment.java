/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.xselector;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.BaseBrowse1Fragment;
import org.sqlunet.browser.Browse2Activity;
import org.sqlunet.browser.Browse2Fragment;
import org.sqlunet.browser.R;
import org.sqlunet.browser.Selectors;
import org.sqlunet.browser.xn.Settings;
import org.sqlunet.provider.ProviderArgs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * X selector fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class XBrowse1Fragment extends BaseBrowse1Fragment implements XSelectorsFragment.Listener
{
	static private final String TAG = "XBrowse1F";

	// C R E A T I O N

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		Log.d(TAG, "lifecycle: onCreate");

		// view
		final View view = inflater.inflate(Settings.getPaneLayout(R.layout.fragment_xbrowse_first, R.layout.fragment_xbrowse1, R.layout.fragment_xbrowse1_browse2), container, false);
		boolean isTwoPane = isTwoPane(view);

		// manager
		final FragmentManager manager = getChildFragmentManager();

		// selector fragment
		XSelectorsFragment selectorsFragment;
		selectorsFragment = (XSelectorsFragment) manager.findFragmentByTag("browse1");
		if (selectorsFragment == null)
		{
			selectorsFragment = new XSelectorsFragment();
			selectorsFragment.setArguments(getArguments());
		}
		Bundle args1 = selectorsFragment.getArguments();
		if (args1 == null)
		{
			args1 = new Bundle();
		}
		args1.putBoolean(Selectors.IS_TWO_PANE, isTwoPane);
		selectorsFragment.setListener(this);
		Log.d(TAG, "create 'browse1' fragment");
		manager.beginTransaction() //
				.setReorderingAllowed(true) //
				.replace(R.id.container_xselectors, selectorsFragment, "browse1") //
				.commit();

		// two-pane specific set up
		if (isTwoPane)
		{
			// in two-pane mode, list items should be given the 'activated' state when touched.
			selectorsFragment.setActivateOnItemClick(true);

			// detail fragment (rigid layout)
			Fragment browse2Fragment;
			browse2Fragment = manager.findFragmentByTag("browse2");
			if (browse2Fragment == null)
			{
				browse2Fragment = new Browse2Fragment();
				final Bundle args2 = new Bundle();
				args2.putBoolean(Browse2Fragment.ARG_ALT, false);
				browse2Fragment.setArguments(args2);
			}
			Log.d(TAG, "create 'browse2' fragment");
			manager.beginTransaction() //
					.setReorderingAllowed(true) //
					.replace(R.id.container_browse2, browse2Fragment, "browse2") //
					.commit();
		}

		return view;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		Log.d(TAG, "lifecycle: onDestroy");
	}

	public void destroyFragments()
	{
		super.onDestroy();
		// remove fragments so that they will not be restored
		final FragmentManager manager = getChildFragmentManager();
		Fragment selectorsFragment = manager.findFragmentByTag("browse1");
		Fragment browse2Fragment = manager.findFragmentByTag("browse2");
		FragmentTransaction transaction = manager.beginTransaction().setReorderingAllowed(true);
		if (selectorsFragment != null)
		{
			Log.d(TAG, "destroy 'browse1' fragment");
			transaction.remove(selectorsFragment);
		}
		if (browse2Fragment != null)
		{
			Log.d(TAG, "destroy 'browse2' fragment");
			transaction.remove(browse2Fragment);
		}
		transaction.commit();
	}

	// I T E M S E L E C T I O N H A N D L I N G

	/**
	 * Callback method from {@link XSelectorsFragment.Listener} indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(@NonNull final XSelectorPointer pointer, final String word, final String cased, final String pronunciation, final String pos)
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
			final int recurse = Settings.getRecursePref(requireContext());
			final Bundle args = new Bundle();
			args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);
			args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);
			args.putString(ProviderArgs.ARG_HINTWORD, word);
			args.putString(ProviderArgs.ARG_HINTCASED, cased);
			args.putString(ProviderArgs.ARG_HINTPRONUNCIATION, pronunciation);
			args.putString(ProviderArgs.ARG_HINTPOS, pos);
			args.putBoolean(Browse2Fragment.ARG_ALT, pointer.getXGroup() != 0);
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
