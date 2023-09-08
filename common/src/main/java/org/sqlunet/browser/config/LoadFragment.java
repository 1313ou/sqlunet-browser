/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import org.sqlunet.browser.common.R;
import org.sqlunet.settings.StorageSettings;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static com.bbou.download.AbstractDownloadFragment.DOWNLOAD_FROM_ARG;
import static com.bbou.download.AbstractDownloadFragment.DOWNLOAD_TO_ARG;

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
	{}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// inflate
		final View view = inflater.inflate(R.layout.fragment_load, container, false);

		// buttons
		final ImageButton assetLoadButton = view.findViewById(R.id.assetload);
		assetLoadButton.setOnClickListener((v) -> {

			Activity activity = requireActivity();
			final Intent intent = new Intent(activity, AssetLoadActivity.class);
			intent.addFlags(0);
			activity.startActivity(intent);
		});
		final ImageButton downloadButton = view.findViewById(R.id.download);
		downloadButton.setOnClickListener((v) -> {

			Activity activity = requireActivity();
			final Intent intent = new Intent(activity, DownloadActivity.class);
			intent.putExtra(DOWNLOAD_FROM_ARG, StorageSettings.getDbDownloadSource(activity));
			intent.putExtra(DOWNLOAD_TO_ARG, StorageSettings.getDbDownloadTarget(activity));
			intent.addFlags(0);
			activity.startActivity(intent);
		});
		final Button cancelButton = view.findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener((v) -> requireActivity().finish());
		return view;
	}
}
