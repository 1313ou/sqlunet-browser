/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.common.R;
import org.sqlunet.settings.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * A fragment representing a detail
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public abstract class BaseBrowse2Fragment extends Fragment
{
	static private final String TAG = "Browse2F";

	static private final String POINTER_STATE = "pointer";

	@Nullable
	Parcelable pointer = null;

	@Nullable
	String pos = null;

	@SuppressWarnings("WeakerAccess")
	protected int layoutId = R.layout.fragment_browse2_multi;

	// C R E A T I O N

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public BaseBrowse2Fragment()
	{
		//
	}

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setRetainInstance(false);
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		// mode
		final Settings.DetailViewMode mode = Settings.getDetailViewModePref(requireContext());

		// view
		View view = null;
		switch (mode)
		{
			case VIEW:
				view = inflater.inflate(this.layoutId , container, false);
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
