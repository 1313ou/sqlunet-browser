package org.sqlunet.browser;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;

import org.sqlunet.browser.common.R;

/**
 * Navigable fragment (gets notification of activation)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

abstract public class NavigableFragment extends Fragment implements ActionBarSetter
{
	// static private final String TAG = "NavigableFragment";

	protected int titleId;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// retain instance
		setRetainInstance(true);
	}

	@Override
	public boolean setActionBar(@NonNull final ActionBar actionBar, final Context context)
	{
		actionBar.setTitle(this.titleId);
		actionBar.setSubtitle(R.string.app_subname);
		return false;
	}
}
