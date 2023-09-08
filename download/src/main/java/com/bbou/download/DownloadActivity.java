/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.sqlunet.broadcast.Broadcast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.bbou.download.BaseDownloadFragment.DOWNLOAD_DOWNLOADER_ARG;

@FunctionalInterface
interface OnComplete
{
	void onComplete(boolean success);
}

/**
 * Download activity
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
		final Settings.Downloader downloader = overriddenDownloader == null ? Settings.Downloader.getDownloaderFromPref(this) : Settings.Downloader.valueOf(overriddenDownloader);

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
				case DOWNLOAD:
					downloadFragment = new DownloadFragment();
					break;

				case DOWNLOAD_ZIP:
					downloadFragment = new DownloadZipFragment();
					break;
			}
			// pass arguments over to fragment
			downloadFragment.setArguments(getIntent().getExtras());
			downloadFragment.setRequestKill(() -> broadcastRequest(this, Broadcast.RequestType.KILL));
			downloadFragment.setRequestNew(() -> broadcastRequest(this, Broadcast.RequestType.NEW));

			getSupportFragmentManager() //
					.beginTransaction() //
					.setReorderingAllowed(true) //
					.replace(R.id.container, downloadFragment) //
					.commit();
		}
	}

	/**
	 * Download complete callback
	 * Do nothing (let user decide whether to deploy downloaded bundle)
	 *
	 * @param success true if success
	 */
	@Override
	public void onComplete(final boolean success)
	{
		Log.d(TAG, "OnComplete succeeded=" + success + " " + this);

		// finish activity
		if (success)
		{
			finish();
		}
	}

	static private void broadcastRequest(@NonNull final Context context, @NonNull final Broadcast.RequestType request)
	{
		Log.d(TAG, "Send broadcast request " + request);
		final Intent intent = new Intent();
		intent.setPackage(context.getPackageName());
		// intent.setComponent(new ComponentName(context.getPackageName(), "org.grammarscope.ProviderManager");
		intent.setAction(Broadcast.BROADCAST_ACTION);
		intent.putExtra(Broadcast.BROADCAST_ACTION_REQUEST, request.name());
		context.sendBroadcast(intent);
	}
}
