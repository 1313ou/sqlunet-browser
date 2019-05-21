/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.common.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * About fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class AboutFragment extends NavigableFragment
{
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public AboutFragment()
	{
		this.titleId = R.string.title_about_section;
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
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
