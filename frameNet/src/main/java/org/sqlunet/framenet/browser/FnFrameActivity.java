/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.framenet.browser;

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
}
