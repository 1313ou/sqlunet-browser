/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Logging fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class LoggingFragment extends Fragment
{
	static private final String TAG = "Lifecycle";

	// C R E A T I O N

	/**
	 * Constructor
	 */
	public LoggingFragment()
	{
		super();
		Log.d(TAG, "Constructor (0) " + this);
	}

	@Override
	public void onAttach(@NonNull final Context context)
	{
		Log.d(TAG, "onAttach() (1) " + this);
		super.onAttach(context);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreate() (2) " + this + " from " + savedInstanceState);
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("InflateParams")
	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateView() (3) " + this + " from " + savedInstanceState);
		return null;
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		Log.d(TAG, "onViewCreated() (4) " + this + " from " + savedInstanceState);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onViewStateRestored(@Nullable final Bundle savedInstanceState)
	{
		Log.d(TAG, "onViewStateRestored() (5) " + this);
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onStart()
	{
		Log.d(TAG, "onStart() (6) " + this);
		super.onStart();
	}

	@Override
	public void onResume()
	{
		Log.d(TAG, "onResume() (7) " + this);
		super.onResume();
	}


	@Override
	public void onPause()
	{
		super.onPause();
		Log.d(TAG, "onPause() (-6) " + this);
	}

	@Override
	public void onStop()
	{
		super.onStop();
		Log.d(TAG, "onStop() (-5) " + this);
	}

	/**
	 * This method may be called at any time before onDestroy.
	 * There are many situations where a fragment may be mostly torn down (such as when placed on the back stack with no UI showing),
	 * but its state will not be saved until its owning activity actually needs to save its state.
	 */
	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		super.onSaveInstanceState(outState);
		Log.d(TAG, "onSaveInstanceState() (-4) " + this);
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		Log.d(TAG, "onDestroyView() (-3) " + this);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "onDestroy() (-2) " + this);
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		Log.d(TAG, "onDetach() (-1) " + this);
	}
}
