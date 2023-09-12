/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
		final String overriddenDownloader = getIntent().getStringExtra(AbstractDownloadFragment.DOWNLOAD_DOWNLOADER_ARG);
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
			Bundle args = getIntent().getExtras();
			downloadFragment.setArguments(args);

			if (args != null)
			{
				String broadcastAction = args.getString(AbstractDownloadFragment.BROADCAST_ACTION);
				String broadcastRequestKey = args.getString(AbstractDownloadFragment.BROADCAST_REQUEST_KEY);
				if (broadcastAction != null && !broadcastAction.isEmpty() && broadcastRequestKey != null && !broadcastRequestKey.isEmpty())
				{
					String broadcastKillRequestValue = args.getString(AbstractDownloadFragment.BROADCAST_KILL_REQUEST_VALUE);
					if (broadcastKillRequestValue != null && !broadcastKillRequestValue.isEmpty())
					{
						downloadFragment.setRequestKill(() -> broadcastRequest(this, broadcastAction, broadcastRequestKey, broadcastKillRequestValue));
					}
					String broadcastNewRequestValue = args.getString(AbstractDownloadFragment.BROADCAST_NEW_REQUEST_VALUE);
					if (broadcastNewRequestValue != null && !broadcastNewRequestValue.isEmpty())
					{
						downloadFragment.setRequestNew(() -> broadcastRequest(this, broadcastAction, broadcastRequestKey, broadcastNewRequestValue));
					}
				}
			}

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

	/**
	 * Broadcast request
	 *
	 * @param context               context
	 * @param broadcastAction       broadcast action
	 * @param broadcastRequestKey   broadcast request arg key
	 * @param broadcastRequestValue broadcast request arg value
	 */
	static private void broadcastRequest(@NonNull final Context context, @NonNull final String broadcastAction, @NonNull final String broadcastRequestKey, @NonNull final String broadcastRequestValue)
	{
		Log.d(TAG, "Send broadcast request " + broadcastRequestValue);
		final Intent intent = new Intent();
		intent.setPackage(context.getPackageName());
		intent.setAction(broadcastAction);
		intent.putExtra(broadcastRequestKey, broadcastRequestValue);
		context.sendBroadcast(intent);
	}
}
