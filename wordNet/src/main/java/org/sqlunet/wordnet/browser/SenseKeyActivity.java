/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.browser;

import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Sense activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SenseKeyActivity extends SynsetActivity
{
	@NonNull
	@Override
	protected Fragment makeFragment()
	{
		return new SenseKeyFragment();
	}

	@Override
	public void onConfigurationChanged(@NonNull final Configuration newConfig)
	{
		// Needed ?
		super.onConfigurationChanged(newConfig);
	}
}
