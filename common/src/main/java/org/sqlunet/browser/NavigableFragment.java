/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Context;
import android.os.Bundle;

import org.sqlunet.browser.common.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

/**
 * Navigable fragment (gets notification of activation)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

abstract public class NavigableFragment extends Fragment implements ActionBarSetter
{
	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean setActionBar(@NonNull final ActionBar actionBar, final Context context)
	{
		actionBar.setSubtitle(R.string.app_subname);
		return false;
	}
}
