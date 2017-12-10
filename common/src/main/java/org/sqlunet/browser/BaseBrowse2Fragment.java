package org.sqlunet.browser;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.common.R;
import org.sqlunet.settings.Settings;

/**
 * A fragment representing a detail
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public abstract class BaseBrowse2Fragment extends Fragment
{
	static private final String TAG = "Browse2Fragment";

	static private final String POINTER_STATE = "pointer";

	@SuppressWarnings("WeakerAccess")
	protected Parcelable pointer = null;

	@SuppressWarnings("WeakerAccess")
	protected String pos = null;

	// C R E A T I O N

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public BaseBrowse2Fragment()
	{
		//
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// retain instance
		setRetainInstance(true);

		final Settings.DetailViewMode mode = Settings.getDetailViewModePref(this.getActivity());

		// view
		View view = null;
		switch (mode)
		{
			case VIEW:
				view = inflater.inflate(R.layout.fragment_browse2_multi, container, false);
				break;
			case WEB:
				view = inflater.inflate(R.layout.fragment_browse2, container, false);
				break;
		}

		if (savedInstanceState != null)
		{
			Log.d(TAG, "restore instance state " + this);
			this.pointer = savedInstanceState.getParcelable(POINTER_STATE);
		}

		return view;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if (this.pointer != null)
		{
			search();
		}
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState)
	{
		Log.d(TAG, "save instance state " + this);
		super.onSaveInstanceState(outState);
		outState.putParcelable(POINTER_STATE, this.pointer);
	}

	/**
	 * Search
	 *
	 * @param pointer pointer
	 * @param pos     pos
	 */
	public void search(final Parcelable pointer, final String pos)
	{
		this.pointer = pointer;
		this.pos = pos;

		search();
	}

	/**
	 * Search
	 */
	abstract protected void search();
}
