/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.xselector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.R;
import org.sqlunet.browser.selector.SelectorsFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * X selectors fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class XSelectorsFragment extends Fragment
{
	static private final String TAG = "XSelectorsF";

	/**
	 * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
	 */
	@FunctionalInterface
	public interface Listener
	{
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(XSelectorPointer pointer, String word, String cased, String pos);
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public XSelectorsFragment()
	{
	}

	// V I E W

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_xselectors, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// Check that the activity is using the layout version with the fragment_container FrameLayout
		if (view.findViewById(R.id.xwnselectors) != null)
		{
			// However, if we're being restored from a previous state, then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null)
			{
				return;
			}

			// Create a new Fragment to be placed in the activity layout
			Fragment fragment = new SelectorsFragment();

			// Pass the arguments
			fragment.setArguments(getArguments());

			// Add the fragment to the fragment container layout
			getChildFragmentManager() //
					.beginTransaction() //
					.add(R.id.xwnselectors, fragment) //
					.commit();
		}

		if (view.findViewById(R.id.xsnselectors) != null)
		{
			// However, if we're being restored from a previous state, then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null)
			{
				return;
			}

			// Create a new Fragment to be placed in the activity layout
			Fragment fragment = new SnSelectorsFragment();

			// Pass the arguments
			fragment.setArguments(getArguments());

			// Add the fragment to the fragment container layout
			getChildFragmentManager() //
					.beginTransaction() //
					.add(R.id.xsnselectors, fragment) //
					.commit();
		}
	}

	// L I S T E N E R

	/**
	 * Set listener
	 *
	 * @param listener listener
	 */
	public void setListener(final XSelectorsFragment.Listener listener)
	{
		//this.listener = listener;
	}
}
