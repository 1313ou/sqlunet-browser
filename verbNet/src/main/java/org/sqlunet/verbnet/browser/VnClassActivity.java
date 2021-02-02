/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.verbnet.browser;

import android.content.res.Configuration;

import org.sqlunet.browser.AbstractActivity;
import org.sqlunet.verbnet.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * VerbNet class activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VnClassActivity extends AbstractActivity
{
	@Override
	protected int getLayoutId()
	{
		return R.layout.activity_vnclass;
	}

	@Override
	protected int getContainerId()
	{
		return R.id.container_vnclass;
	}

	@NonNull
	@Override
	protected Fragment makeFragment()
	{
		return new VnClassFragment();
	}

	@Override
	public void onConfigurationChanged(@NonNull final Configuration newConfig)
	{
		// Needed ?
		super.onConfigurationChanged(newConfig);
	}
}
