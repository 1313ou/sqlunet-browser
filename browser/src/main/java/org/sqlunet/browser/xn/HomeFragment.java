/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.xn;

import android.os.Bundle;
import android.view.View;

import org.sqlunet.browser.R;

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

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		final HomeImageView homeImage = view.findViewById(R.id.splash);
		homeImage.init();
	}
}

