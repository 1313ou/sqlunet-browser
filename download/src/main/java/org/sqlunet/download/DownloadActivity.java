/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static org.sqlunet.download.BaseDownloadFragment.DOWNLOAD_DOWNLOADER_ARG;

/**
 * Download activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadActivity extends AppCompatActivity
{
	static private final String TAG = "DownloadA";

	@Override
	protected void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// downloader
		final String overriddenDownloader = getIntent().getStringExtra(DOWNLOAD_DOWNLOADER_ARG);
		final Settings.Downloader downloader = overriddenDownloader == null ? Settings.Downloader.getFromPref(this) : Settings.Downloader.valueOf(overriddenDownloader);

		// content
		setContentView(R.layout.activity_download);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.setDisplayShowTitleEnabled(true);
		}

		if (savedInstanceState == null)
		{
			// set this as listener
			BaseDownloadFragment downloadFragment = null;
			switch (downloader)
			{
				case SIMPLE_SERVICE:
					downloadFragment = new SimpleDownloadServiceFragment();
					break;

				case SIMPLE_ZIP_SERVICE:
					downloadFragment = new SimpleZipDownloadServiceFragment();
					break;
			}
			downloadFragment.setArguments(getIntent().getExtras());

			getSupportFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container, downloadFragment) //
					.commit();
		}
	}
}
