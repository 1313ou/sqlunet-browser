/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.sqlunet.Word;
import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.predicatematrix.browser.PredicateMatrixFragment;
import org.sqlunet.predicatematrix.settings.Settings;
import org.sqlunet.provider.ProviderArgs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

/**
 * PredicateMatrix fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BrowsePredicateMatrixFragment extends BaseSearchFragment
{
	static private final String TAG = "BrowsePmF";
	static public final String FRAGMENT_TAG = "predicatematrix";

	/**
	 * Saved query
	 */
	static private final String STATE_QUERY = "query";

	/**
	 * Saved pointer
	 */
	static private final String STATE_POINTER = "pointer";

	/**
	 * Query
	 */
	@Nullable
	private String query;

	/**
	 * Pointer
	 */
	@Nullable
	private PmRolePointer pointer;

	// C R E A T I O N

	/**
	 * Constructor
	 */
	public BrowsePredicateMatrixFragment()
	{
		this.layoutId = R.layout.fragment_browse_predicatematrix;
		this.menuId = R.menu.predicate_matrix;
		this.colorAttrId = R.attr.colorPrimaryVariant;
		this.spinnerLabels = R.array.predicatematrix_modes;
		this.spinnerIcons = R.array.predicatematrix_icons;
	}

	// R E S T O R E

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		// view
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		// restore data
		if (savedInstanceState != null)
		{
			this.query = savedInstanceState.getString(STATE_QUERY);
			this.pointer = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU ? savedInstanceState.getParcelable(STATE_POINTER, PmRolePointer.class) : savedInstanceState.getParcelable(STATE_POINTER);
		}
		else
		{
			// splash fragment
			final Fragment fragment = new BrowsePredicateMatrixSplashFragment();
			assert isAdded();
			getChildFragmentManager() //
					.beginTransaction() //
					.setReorderingAllowed(true) //
					.replace(R.id.container_predicatematrix, fragment, SplashFragment.FRAGMENT_TAG) //
					// .addToBackStack(SplashFragment.FRAGMENT_TAG) //
					.commit();
		}

		return view;
	}

	// S A V E

	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		// always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(outState);

		// save data
		if (this.query != null)
		{
			outState.putString(BrowsePredicateMatrixFragment.STATE_QUERY, this.query);
		}
		if (this.pointer != null)
		{
			outState.putParcelable(BrowsePredicateMatrixFragment.STATE_POINTER, this.pointer);
		}
	}

	// S P I N N E R

	@Override
	protected void setupSpinner(@NonNull final Spinner spinner)
	{
		spinner.setVisibility(View.VISIBLE);

		// apply spinner adapter
		spinner.setAdapter(getSpinnerAdapter());

		// saved mode
		final Settings.PMMode mode = Settings.PMMode.getPref(requireContext());
		//noinspection ConstantValue
		if (mode != null)
		{
			// no listener yet
			spinner.setOnItemSelectedListener(null);
			spinner.setSelection(mode.ordinal());
		}

		// spinner listener
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position, final long id)
			{
				final Settings.PMMode mode = Settings.PMMode.values()[position];
				final boolean hasChanged = mode.setPref(requireContext());
				Log.d(TAG, "mode=" + mode.name() + " has changed=" + hasChanged);

				// restart
				if (hasChanged)
				{
					if (BrowsePredicateMatrixFragment.this.pointer != null)
					{
						search(BrowsePredicateMatrixFragment.this.pointer);
					}
					else if (BrowsePredicateMatrixFragment.this.query != null)
					{
						search(BrowsePredicateMatrixFragment.this.query);
					}
				}
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parentView)
			{
				//
			}
		});
	}

	// S E A R C H

	/**
	 * Handle search
	 *
	 * @param pointer query pointer
	 */
	public void search(@Nullable final PmRolePointer pointer)
	{
		if (pointer == null)
		{
			return;
		}

		// log
		Log.d(TAG, "Search PM " + pointer);

		// subtitle
		final Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
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

		// set
		this.pointer = pointer;
		this.query = null;

		// arguments
		final Bundle args = new Bundle();
		args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);
		args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_PM);

		// fragment
		final Fragment fragment = new PredicateMatrixFragment();
		fragment.setArguments(args);
		if (!isAdded())
		{
			return;
		}
		getChildFragmentManager() //
				.beginTransaction() //
				.setReorderingAllowed(true) //
				.replace(R.id.container_predicatematrix, fragment, BrowsePredicateMatrixFragment.FRAGMENT_TAG) //
				.addToBackStack(BrowsePredicateMatrixFragment.FRAGMENT_TAG) //
				.commit();
	}

	/**
	 * Handle search
	 *
	 * @param query0 query string
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
		Log.d(TAG, "Search PM " + query);

		// subtitle
		final Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
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

		// set
		this.query = query;
		this.pointer = null;

		// pointer
		Parcelable pointer;
		if (query.startsWith("#mr")) //
		{
			final long roleId = Long.parseLong(query.substring(3));
			pointer = new PmRolePointer(roleId);
		}
		else
		{
			pointer = new Word(query);
		}

		// arguments
		final Bundle args = new Bundle();
		args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);
		args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_PM);

		// fragment
		final Fragment fragment = new PredicateMatrixFragment();
		fragment.setArguments(args);
		if (!isAdded())
		{
			return;
		}
		getChildFragmentManager() //
				.beginTransaction() //
				.setReorderingAllowed(true) //
				.replace(R.id.container_predicatematrix, fragment, BrowsePredicateMatrixFragment.FRAGMENT_TAG) //
				.addToBackStack(BrowsePredicateMatrixFragment.FRAGMENT_TAG) //
				.commit();
	}

	@Override
	protected boolean triggerFocusSearch()
	{
		if (!isAdded())
		{
			return false;
		}
		Fragment active = getChildFragmentManager().findFragmentById(R.id.container_predicatematrix);
		return active != null && SplashFragment.FRAGMENT_TAG.equals(active.getTag());
	}
}
