package org.sqlunet.browser;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * About fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class AboutFragment extends Fragment
{
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public AboutFragment()
	{
		//
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);
		final View view = inflater.inflate(R.layout.fragment_about, container, false);

		// fragment
		final Fragment fragment = new SourceFragment();
		getChildFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_source, fragment) //
				.commit();

		return view;
	}
}
