/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.propbank.browser;

import android.content.res.Configuration;

import org.sqlunet.browser.AbstractActivity;
import org.sqlunet.propbank.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * PropBank role set activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PbRoleSetActivity extends AbstractActivity
{
	@Override
	protected int getLayoutId()
	{
		return R.layout.activity_pbroleset;
	}

	@Override
	protected int getContainerId()
	{
		return R.id.container_pbroleset;
	}

	@NonNull
	@Override
	protected Fragment makeFragment()
	{
		return new PbRoleSetFragment();
	}
}

