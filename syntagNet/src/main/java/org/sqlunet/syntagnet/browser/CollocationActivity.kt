/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.syntagnet.browser;

import org.sqlunet.browser.AbstractActivity;
import org.sqlunet.syntagnet.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * SyntagNet collocation activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class CollocationActivity extends AbstractActivity
{
	@Override
	protected int getLayoutId()
	{
		return R.layout.activity_collocation;
	}

	@Override
	protected int getContainerId()
	{
		return R.id.container_collocation;
	}

	@NonNull
	@Override
	protected Fragment makeFragment()
	{
		return new CollocationFragment();
	}
}

