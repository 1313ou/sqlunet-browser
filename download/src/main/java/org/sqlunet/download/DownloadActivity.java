/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
public class DownloadActivity extends AppCompatActivity implements DownloadFragment.DownloadListener
{
	// static private final String TAG = "DownloadA";

	/**
	 * Result extra
	 */
	static private final String RESULT_DOWNLOAD_DATA_AVAILABLE = "download_data_available";

	@Override
	protected void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// downloader
		final String overriddenDownloader = getIntent().getStringExtra(DOWNLOAD_DOWNLOADER_ARG);
		final Settings.Downloader downloader = overriddenDownloader == null ? Settings.Downloader.getFromPref(this) : org.sqlunet.download.Settings.Downloader.valueOf(overriddenDownloader);

		// content
		setContentView(R.layout.activity_download);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

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

				case DOWNLOAD_MANAGER:
					downloadFragment = new DownloadFragment();
					break;
			}
			downloadFragment.setArguments(getIntent().getExtras());
			downloadFragment.setListener(this);

			getSupportFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container, downloadFragment) //
					.commit();
		}
	}

	@Override
	public void onDone(boolean result)
	{
		// return result
		final Intent resultIntent = new Intent();
		resultIntent.putExtra(DownloadActivity.RESULT_DOWNLOAD_DATA_AVAILABLE, result);
		setResult(result ? Activity.RESULT_OK : Activity.RESULT_CANCELED, resultIntent);
		// finish();
	}
}
