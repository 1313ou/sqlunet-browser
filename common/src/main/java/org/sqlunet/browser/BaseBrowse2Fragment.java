/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.browser;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
	String word = null;

	@Nullable
	String cased = null;

	@Nullable
	String pronunciation = null;

	@Nullable
	String pos = null;

	@SuppressWarnings("WeakerAccess")
	protected int layoutId = R.layout.fragment_browse2_multi;

	@Nullable
	protected TextView targetView;

	// C R E A T I O N

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//noinspection deprecation
		this.setRetainInstance(false); // default
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
				view = inflater.inflate(this.layoutId, container, false);
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
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		targetView = view.findViewById(R.id.target);
		requireActivity().invalidateOptionsMenu();
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
		Log.d(TAG, "Save instance state " + this);
		super.onSaveInstanceState(outState);
		outState.putParcelable(POINTER_STATE, this.pointer);
	}

	/**
	 * Search
	 *
	 * @param pointer       pointer
	 * @param word          word
	 * @param cased         cased
	 * @param pronunciation pronunciation
	 * @param pos           pos
	 */
	public void search(final Parcelable pointer, final String word, final String cased, final String pronunciation, final String pos)
	{
		this.pointer = pointer;
		this.word = word;
		this.cased = cased;
		this.pronunciation = pronunciation;
		this.pos = pos;

		search();
	}

	/**
	 * Search
	 */
	abstract protected void search();
}
