package org.sqlunet.browser;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.sqlunet.Word;
import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.predicatematrix.browser.PredicateMatrixFragment;
import org.sqlunet.predicatematrix.settings.Settings;
import org.sqlunet.provider.ProviderArgs;

/**
 * PredicateMatrix fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BrowsePredicateMatrixFragment extends BaseSearchFragment
{
	static private final String TAG = "BrowsePmFragment";

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
	private String query;

	/**
	 * Pointer
	 */
	private PmRolePointer pointer;

	// C R E A T I O N

	/**
	 * Constructor
	 */
	public BrowsePredicateMatrixFragment()
	{
		this.layoutId = R.layout.fragment_browse_predicatematrix;
		this.menuId = R.menu.predicate_matrix;
		this.colorId = R.color.predicatematrix_actionbar_color;
		this.spinnerLabels = R.array.predicatematrix_modes;
		this.spinnerIcons = R.array.predicatematrix_icons;
		this.titleId = R.string.title_predicatematrix_section;
	}

	// R E S T O R E

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		// restore data
		if (savedInstanceState != null)
		{
			this.query = savedInstanceState.getString(STATE_QUERY);
			this.pointer = savedInstanceState.getParcelable(STATE_POINTER);
		}
		else
		{
			// splash fragment
			final Fragment fragment = new BrowsePredicateMatrixSplashFragment();
			getChildFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container_predicatematrix, fragment) //
					.commit();
		}

		return view;
	}


	// S A V E

	@Override
	public void onSaveInstanceState(final Bundle outState)
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
	protected void setupSpinner(final Context context)
	{
		super.setupSpinner(context);

		// saved mode
		final Settings.PMMode mode = Settings.PMMode.getPref(context);
		if (mode != null)
		{
			// no listener yet
			this.spinner.setOnItemSelectedListener(null);
			this.spinner.setSelection(mode.ordinal());
		}

		// spinner listener
		this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position, final long id)
			{
				final Settings.PMMode mode = Settings.PMMode.values()[position];
				final boolean hasChanged = mode.setPref(context);
				Log.d(BrowsePredicateMatrixFragment.TAG, "mode=" + mode.name() + " has changed=" + hasChanged);

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
	public void search(final PmRolePointer pointer)
	{
		if (pointer == null)
		{
			return;
		}

		// log
		Log.d(BrowsePredicateMatrixFragment.TAG, "PM SEARCH " + pointer);

		// subtitle
		final AppCompatActivity activity = (AppCompatActivity) getActivity();
		final ActionBar actionBar = activity.getSupportActionBar();
		assert actionBar != null;
		actionBar.setSubtitle(query);

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
		getChildFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_predicatematrix, fragment) //
				.commit();
	}

	/**
	 * Handle search
	 *
	 * @param query0 query string
	 */
	@Override
	public void search(final String query0)
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
		Log.d(BrowsePredicateMatrixFragment.TAG, "PM SEARCH " + query);

		// subtitle
		final AppCompatActivity activity = (AppCompatActivity) getActivity();
		final ActionBar actionBar = activity.getSupportActionBar();
		assert actionBar != null;
		actionBar.setSubtitle(query);

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
		getChildFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_predicatematrix, fragment) //
				.commit();
	}
}
