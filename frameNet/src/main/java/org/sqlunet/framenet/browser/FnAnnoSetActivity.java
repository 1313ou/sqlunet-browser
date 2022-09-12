/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.framenet.browser;

import org.sqlunet.browser.AbstractActivity;
import org.sqlunet.framenet.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * AnnoSet activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnAnnoSetActivity extends AbstractActivity
{
	@Override
	protected int getLayoutId()
	{
		return R.layout.activity_fnannoset;
	}

	@Override
	protected int getContainerId()
	{
		return R.id.container_annoset;
	}

	@NonNull
	@Override
	protected Fragment makeFragment()
	{
		return new FnAnnoSetFragment();
	}
}
