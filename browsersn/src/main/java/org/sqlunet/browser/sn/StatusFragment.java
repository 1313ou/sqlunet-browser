/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.sn;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Status fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StatusFragment extends SetupSnStatusFragment
{
	@Override
	public void onResume()
	{
		super.onResume();

		final AppCompatActivity activity = (AppCompatActivity) requireActivity();
		final ActionBar actionBar = activity.getSupportActionBar();
		assert actionBar != null;
		actionBar.setCustomView(null);
		actionBar.setBackgroundDrawable(null);
	}
}
