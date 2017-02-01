package org.sqlunet.browser;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;

/**
 * Navigable fragment (gets notification of activation)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

abstract public class NavigableFragment extends Fragment implements ActionBarSetter
{
	static private final String TAG = "NavigableFragment";

	protected int titleId;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// TODO
		setRetainInstance(true);
	}

	//	@Override
	//	public void onResume()
	//	{
	//		Log.d(TAG, "onresume " + this);
	//		super.onResume();
	//		setActionBar();
	//	}
	//
	//	@Override
	//	public void onHiddenChanged(boolean hidden)
	//	{
	//		Log.d(TAG, "on hidden " + this);
	//		super.onHiddenChanged(hidden);
	//		if (!hidden)
	//		{
	//			setActionBar();
	//		}
	//	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		Log.d(TAG, "save instance " + this);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean setActionBar(final ActionBar actionBar, final Context context)
	{
		actionBar.setTitle(this.titleId);
		return false;
	}
}
