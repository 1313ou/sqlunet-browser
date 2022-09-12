/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.download;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static org.sqlunet.download.BaseDownloadFragment.DOWNLOAD_DOWNLOADER_ARG;

@FunctionalInterface
interface OnComplete
{
	void onComplete(boolean success);
}

/**
 * Download activity
 * This activity does not handle the completion signal.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadActivity extends AppCompatActivity implements OnComplete
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
			// fragment
			BaseDownloadFragment downloadFragment = null;
			switch (downloader)
			{
				case DOWNLOAD_SERVICE:
					downloadFragment = new DownloadFragment();
					break;

				case DOWNLOAD_ZIP_SERVICE:
					downloadFragment = new DownloadZipFragment();
					break;
			}
			// pass arguments over to fragment
			downloadFragment.setArguments(getIntent().getExtras());

			getSupportFragmentManager() //
					.beginTransaction() //
					.setReorderingAllowed(true) //
					.replace(R.id.container, downloadFragment) //
					.commit();
		}
	}

	@Override
	public void onComplete(final boolean success)
	{
		Log.d(TAG, "OnComplete " + success + " " + this);
	}
}