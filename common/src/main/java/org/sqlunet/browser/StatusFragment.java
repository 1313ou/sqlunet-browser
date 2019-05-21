/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Context;

import org.sqlunet.browser.common.R;
import org.sqlunet.browser.config.SetupStatusFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

/**
 * Status fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StatusFragment extends SetupStatusFragment implements ActionBarSetter
{
	// static private final String TAG = "StatusF";

	private final int titleId;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public StatusFragment()
	{
		this.titleId = R.string.title_status_section;
	}

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean setActionBar(@NonNull final ActionBar actionBar, final Context context)
	{
		actionBar.setTitle(this.titleId);
		actionBar.setSubtitle(R.string.app_subname);
		return false;
	}
}
