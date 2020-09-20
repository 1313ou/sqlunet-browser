/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.sn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Home fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class HomeFragment extends org.sqlunet.browser.HomeFragment
{
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public HomeFragment()
	{
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		assert view != null;
		return view;
	}
}

