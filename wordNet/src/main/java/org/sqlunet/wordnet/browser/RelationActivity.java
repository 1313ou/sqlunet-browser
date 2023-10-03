/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.browser;

import org.sqlunet.browser.AbstractActivity;
import org.sqlunet.wordnet.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Synset activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class RelationActivity extends AbstractActivity
{
	@Override
	protected int getLayoutId()
	{
		return R.layout.activity_relation;
	}

	@Override
	protected int getContainerId()
	{
		return R.id.container_relation;
	}

	@NonNull
	@Override
	protected Fragment makeFragment()
	{
		return new RelationFragment();
	}
}
