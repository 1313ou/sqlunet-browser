package org.sqlunet.browser;

import android.support.v4.app.Fragment;

/**
 * Browse splash fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class BrowseSplashFragment extends Fragment
{
	// static private final String TAG = "MainSplashFragment";

	protected final int layoutId;

	/**
	 * Constructor
	 */
	public BrowseSplashFragment()
	{
		this.layoutId = R.layout.layout_leaf;
	}
}
