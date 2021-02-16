/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.sqlunet.browser.EntryActivity;
import org.sqlunet.browser.MenuHandler;
import org.sqlunet.browser.common.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Load fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class LoadFragment extends Fragment
{
	/**
	 * Constructor
	 */
	public LoadFragment()
	{
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// inflate
		final View view = inflater.inflate(R.layout.fragment_load, container, false);

		// buttons
		final ImageButton assetLoadButton = view.findViewById(R.id.assetload);
		assetLoadButton.setOnClickListener((v) -> {
		});
		final ImageButton downloadButton = view.findViewById(R.id.download
		);
		downloadButton.setOnClickListener((v) -> {
		});
		return view;
	}
}
