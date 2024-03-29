/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.browser;

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
}
