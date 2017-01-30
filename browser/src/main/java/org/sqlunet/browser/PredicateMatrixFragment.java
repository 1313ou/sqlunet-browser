package org.sqlunet.browser;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.sqlunet.Word;
import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.predicatematrix.settings.Settings;
import org.sqlunet.provider.ProviderArgs;

/**
 * PredicateMatrix fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateMatrixFragment extends BaseSearchFragment
{
	static private final String TAG = "PredicateMatrixFragment";

	/**
	 * Saved pointer
	 */
	static private final String STATE_POINTER = "pointer";

	/**
	 * Pointer
	 */
	private PmRolePointer pointer;

	// C R E A T I O N

	/**
	 * Constructor
	 */
	public PredicateMatrixFragment()
	{
		this.layoutId = R.layout.fragment_predicatematrix;
		this.menuId = R.menu.predicate_matrix;
		this.colorId = R.color.predicatematrix_action_bar_color;
		this.spinnerLabels = R.array.predicatematrix_modes;
		this.spinnerIcons = R.array.predicatematrix_icons;
		this.titleId = R.string.title_predicatematrix_section;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// spinner update
		if (savedInstanceState != null)
		{
			this.pointer = savedInstanceState.getParcelable(STATE_POINTER);
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		if (savedInstanceState != null)
		{
			//TODO repeat
			this.pointer = savedInstanceState.getParcelable(STATE_POINTER);
		}

		return view;
	}

	// S A V E   R E S T O R E

	@Override
	public void onSaveInstanceState(final Bundle outState)
	{
		// always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(outState);

		// pointer
		if (this.pointer != null)
		{
			outState.putParcelable(PredicateMatrixFragment.STATE_POINTER, this.pointer);
		}
	}

	// S P I N N E R

	@Override
	protected void setupSpinner()
	{
		super.setupSpinner();

		// spinner listener
		this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position, final long id)
			{
				final Settings.PMMode mode = Settings.PMMode.values()[position];
				mode.setPref(PredicateMatrixFragment.this.getActivity());

				Log.d(PredicateMatrixFragment.TAG, mode.name());

				// restart
				if (PredicateMatrixFragment.this.pointer != null)
				{
					search(PredicateMatrixFragment.this.pointer);
				}
				else
				{
					search(PredicateMatrixFragment.this.query);
				}
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parentView)
			{
				//
			}
		});

		// saved mode
		final Settings.PMMode mode = Settings.PMMode.getPref(getActivity());
		if (mode != null)
		{
			this.spinner.setSelection(mode.ordinal());
		}
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
		Log.d(PredicateMatrixFragment.TAG, "PM SEARCH " + pointer);

		// reset
		this.pointer = pointer;
		this.query = null;

		// arguments
		final Bundle args = new Bundle();
		args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);
		args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_PM);

		// view
		final View view = getView();

		// clear splash
		assert view != null;
		final ViewGroup container = (ViewGroup) view.findViewById(R.id.container_predicatematrix);
		container.removeAllViews();

		// fragment
		final Fragment fragment = new PredicateMatrixResultFragment();
		fragment.setArguments(args);
		getChildFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_predicatematrix, fragment) //
				.commit();
	}

	/**
	 * Handle search
	 *
	 * @param query query string
	 */
	@Override
	public void search(final String query)
	{
		if (query == null || query.isEmpty())
		{
			return;
		}

		// log
		Log.d(PredicateMatrixFragment.TAG, "PM SEARCH " + query);

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

		// view
		final View view = getView();

		// clear splash
		assert view != null;
		final ViewGroup container = (ViewGroup) view.findViewById(R.id.container_predicatematrix);
		assert container != null;
		container.removeAllViews();

		// fragment
		final Fragment fragment = new PredicateMatrixResultFragment();
		fragment.setArguments(args);
		getChildFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_predicatematrix, fragment) //
				.commit();
	}
}
