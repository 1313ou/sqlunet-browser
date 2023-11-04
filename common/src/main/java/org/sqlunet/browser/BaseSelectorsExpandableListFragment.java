/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.os.Bundle;
import android.util.Log;

import androidx.app.local.ExpandableListFragment;

public class BaseSelectorsExpandableListFragment extends ExpandableListFragment
{
	static private final String TAG = "SelectorsXListF";

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public BaseSelectorsExpandableListFragment()
	{
		Log.d(TAG, "Lifecycle: Constructor (0) " + this);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Lifecycle: onCreate (2) " + this + " from "  + savedInstanceState);
		//noinspection deprecation
		this.setRetainInstance(false); // default
	}
}
