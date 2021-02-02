/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.browser;

import android.content.res.Configuration;

import org.sqlunet.browser.AbstractActivity;
import org.sqlunet.wordnet.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Synset activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SynsetActivity extends AbstractActivity
{
	@Override
	protected int getLayoutId()
	{
		return R.layout.activity_synset;
	}

	@Override
	protected int getContainerId()
	{
		return R.id.container_synset;
	}

	@NonNull
	@Override
	protected Fragment makeFragment()
	{
		return new SynsetFragment();
	}

	@Override
	public void onConfigurationChanged(@NonNull final Configuration newConfig)
	{
		// Needed ?
		super.onConfigurationChanged(newConfig);
	}
}
