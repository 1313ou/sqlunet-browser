/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.common.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * Home fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class HomeFragment extends Fragment
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
		return inflater.inflate(R.layout.fragment_home, container, false);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		final AppCompatActivity activity = (AppCompatActivity)requireActivity();
		final ActionBar actionBar = activity.getSupportActionBar();
		assert actionBar != null;
		actionBar.setCustomView(null);
		actionBar.setBackgroundDrawable(null);
	}
}

