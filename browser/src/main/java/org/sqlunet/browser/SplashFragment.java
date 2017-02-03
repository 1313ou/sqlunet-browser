package org.sqlunet.browser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Splash fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SplashFragment extends Fragment
{
	// static private final String TAG = "SpalshFragment";

	protected int layoutId;

	/**
	 * Constructor
	 */
	public SplashFragment()
	{
		// Required empty public constructor
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(this.layoutId, container, false);
		return view;
	}
}
