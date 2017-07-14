package org.sqlunet.browser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.sqlunet.browser.common.R;

/**
 * Home fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class HomeFragment extends NavigableFragment
{
	static private final String TAG = "HomeFragment";

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public HomeFragment()
	{
		this.titleId = R.string.title_home_section;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);
		final View view = inflater.inflate(R.layout.fragment_home, container, false);
		final ImageView image = (ImageView) view.findViewById(R.id.splash);
		return view;
	}
}

