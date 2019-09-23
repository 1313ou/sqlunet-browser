/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Context;
import android.os.Bundle;

import org.sqlunet.browser.common.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * Navigable fragment (gets notification of activation)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

abstract public class NavigableFragment extends Fragment
{
	@Override
	public void onPause()
	{
		super.onPause();

		final AppCompatActivity activity = (AppCompatActivity)requireActivity();
		final ActionBar actionBar = activity.getSupportActionBar();
		actionBar.setCustomView(null);
		actionBar.setBackgroundDrawable(null);
	}
}
