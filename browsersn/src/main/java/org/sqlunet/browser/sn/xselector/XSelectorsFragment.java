/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.sn.xselector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.BaseSelectorsFragment;
import org.sqlunet.browser.sn.R;
import org.sqlunet.browser.sn.selector.CollocationSelectorPointer;
import org.sqlunet.browser.sn.selector.SelectorPointer;
import org.sqlunet.browser.sn.selector.SelectorsFragment;
import org.sqlunet.browser.sn.selector.SnSelectorsFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * X selectors fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class XSelectorsFragment extends BaseSelectorsFragment implements SelectorsFragment.Listener, SnSelectorsFragment.Listener
{
	// static private final String TAG = "XSelectorsF";

	/**
	 * Wn listener
	 */
	private SelectorsFragment.Listener wnListener;

	/**
	 * Sn collocation listener
	 */
	private SnSelectorsFragment.Listener snListener;

	// V I E W

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_xselectors, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// Check that the activity is using the layout version with the fragment_container FrameLayout
		if (view.findViewById(R.id.wnselectors) != null)
		{
			// However, if we're being restored from a previous state, then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null)
			{
				return;
			}

			// Create a new Fragment to be placed in the activity layout
			SelectorsFragment fragment = new SelectorsFragment();

			// Pass the arguments
			fragment.setArguments(getArguments());

			// Listeners
			fragment.setListeners(this.wnListener, this);

			// Add the fragment to the fragment container layout
			assert isAdded();
			getChildFragmentManager() //
					.beginTransaction() //
					.setReorderingAllowed(true) //
					.add(R.id.wnselectors, fragment, "wn" + BaseSelectorsFragment.FRAGMENT_TAG) //
					.commit();
		}

		if (view.findViewById(R.id.snselectors) != null)
		{
			// However, if we're being restored from a previous state, then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null)
			{
				return;
			}

			// Create a new Fragment to be placed in the activity layout
			SnSelectorsFragment fragment = new SnSelectorsFragment();

			// Pass the arguments
			fragment.setArguments(getArguments());

			// Listener
			fragment.setListeners(this.snListener, this);

			// Add the fragment to the fragment container layout
			assert isAdded();
			getChildFragmentManager() //
					.beginTransaction() //
					.setReorderingAllowed(true) //
					.add(R.id.snselectors, fragment, "sn" + BaseSelectorsFragment.FRAGMENT_TAG) //
					.commit();
		}
	}

	// L I S T E N E R S

	/**
	 * Set listener
	 *
	 * @param listener1 wordnet listener
	 * @param listener2 syntagnet listener
	 */
	public void setListener(final SelectorsFragment.Listener listener1, final SnSelectorsFragment.Listener listener2)
	{
		this.wnListener = listener1;
		this.snListener = listener2;
	}

	@Override
	public void onItemSelected(final SelectorPointer pointer, final String word, final String cased, final String pronunciation, final String pos)
	{
		if (!isAdded())
		{
			return;
		}
		SnSelectorsFragment f = (SnSelectorsFragment) getChildFragmentManager().findFragmentByTag("sn" + BaseSelectorsFragment.FRAGMENT_TAG);
		if (f != null)
		{
			f.deactivate();
		}
	}

	@Override
	public void onItemSelected(final CollocationSelectorPointer pointer)
	{
		if (!isAdded())
		{
			return;
		}
		SelectorsFragment f = (SelectorsFragment) getChildFragmentManager().findFragmentByTag("wn" + BaseSelectorsFragment.FRAGMENT_TAG);
		if (f != null)
		{
			f.deactivate();
		}
	}
}
