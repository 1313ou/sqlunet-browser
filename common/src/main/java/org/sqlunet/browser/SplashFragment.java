/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Splash fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class SplashFragment extends Fragment
{
	// static private final String TAG = "SplashF";

	static public final String FRAGMENT_TAG = "splash";

	/**
	 * Layout id set bu super class
	 */
	protected int layoutId;

	/**
	 * Constructor
	 */
	public SplashFragment()
	{
		// Required empty public constructor
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		return inflater.inflate(this.layoutId, container, false);
	}
}
