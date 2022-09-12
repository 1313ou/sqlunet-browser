/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.browser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Splash fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class SplashFragment extends Fragment
{
	// static private final String TAG = "SplashF";

	/**
	 * Layout id set bu super class
	 */
	int layoutId;

	/**
	 * Constructor
	 */
	public SplashFragment()
	{
		// Required empty public constructor
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		return inflater.inflate(this.layoutId, container, false);
	}
}
