/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet.browser;

import android.content.res.Configuration;

import org.sqlunet.browser.AbstractActivity;
import org.sqlunet.framenet.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Frame activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnFrameActivity extends AbstractActivity
{
	@Override
	protected int getLayoutId()
	{
		return R.layout.activity_fnframe;
	}

	@Override
	protected int getContainerId()
	{
		return R.id.container_frame;
	}

	@NonNull
	@Override
	protected Fragment makeFragment()
	{
		return new FnFrameFragment();
	}

	@Override
	public void onConfigurationChanged(@NonNull final Configuration newConfig)
	{
		// Needed ?
		super.onConfigurationChanged(newConfig);
	}
}
