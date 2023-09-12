/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.config;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.EntryActivity;
import org.sqlunet.browser.MenuHandler;
import org.sqlunet.browser.common.R;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageSettings;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import static com.bbou.download.AbstractDownloadFragment.DOWNLOAD_DOWNLOADER_ARG;
import static com.bbou.download.AbstractDownloadFragment.DOWNLOAD_FROM_ARG;
import static com.bbou.download.AbstractDownloadFragment.DOWNLOAD_TO_ARG;
import static com.bbou.download.AbstractDownloadFragment.THEN_UNZIP_TO_ARG;
import static com.bbou.download.BaseDownloadFragment.DOWNLOAD_RENAME_FROM_ARG;
import static com.bbou.download.BaseDownloadFragment.DOWNLOAD_RENAME_TO_ARG;
import static com.bbou.download.DownloadZipFragment.DOWNLOAD_ENTRY_ARG;

/**
 * Download activity
 * Handles completion by re-entering application through entry activity.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadActivity extends com.bbou.download.DownloadActivity
{
	static private final String TAG = "DownloadA";

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
		}
	}

	@Override
	public void onComplete(final boolean success)
	{
		Log.d(TAG, "OnComplete " + success + " " + this);
		if (success)
		{
			EntryActivity.rerun(this);
		}
	}

	// M E N U

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean onCreateOptionsMenu(@NonNull final Menu menu)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		getMenuInflater().inflate(R.menu.initialize, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		// handle home
		if (item.getItemId() == android.R.id.home)
		{
			Log.d(TAG, "onHomePressed");
			EntryActivity.rerun(this);
			return true;
		}
		return MenuHandler.menuDispatchWhenCantRun(this, item);
	}

	public static Intent makeIntent(@NonNull final Context context)
	{
		return makeIntent(context, com.bbou.download.Settings.Downloader.DOWNLOAD_ZIP.toString().equals(Settings.getDownloaderPref(context)));
	}

	public static Intent makeIntent(@NonNull final Context context, boolean zipped)
	{
		String dbSrc = StorageSettings.getDbDownloadSource(context);
		String dbDest = StorageSettings.getDbDownloadTarget(context);
		Intent intent = new Intent(context, DownloadActivity.class);
		if (zipped)
		{
			Uri src = Uri.parse(dbSrc);
			String entry = src.getLastPathSegment();
			File dest = new File(dbDest);
			String name = dest.getName();
			intent.putExtra(DOWNLOAD_FROM_ARG, dbSrc);
			intent.putExtra(DOWNLOAD_ENTRY_ARG, entry);
			intent.putExtra(DOWNLOAD_TO_ARG, dest.getParent());
			intent.putExtra(DOWNLOAD_RENAME_FROM_ARG, entry);
			intent.putExtra(DOWNLOAD_RENAME_TO_ARG, name);
		}
		else
		{
			intent.putExtra(DOWNLOAD_FROM_ARG, dbSrc);
			intent.putExtra(DOWNLOAD_TO_ARG, dbDest);
		}
		return intent;
	}

	public static Intent makeIntentWithDeploy(@NonNull final Context context)
	{
		Intent intent = new Intent(context, DownloadActivity.class);
		intent.putExtra(DOWNLOAD_DOWNLOADER_ARG, com.bbou.download.Settings.Downloader.DOWNLOAD.toString()); // force non zipped transfer
		intent.putExtra(DOWNLOAD_FROM_ARG, StorageSettings.getDbDownloadZippedSource(context)); // source archive
		intent.putExtra(DOWNLOAD_TO_ARG, StorageSettings.getDbDownloadZippedTarget(context)); // destination archive
		intent.putExtra(THEN_UNZIP_TO_ARG, StorageSettings.getDataDir(context)); // unzip destination directory
		intent.putExtra(DOWNLOAD_RENAME_FROM_ARG, StorageSettings.getDbDownloadFile(context)); // rename
		intent.putExtra(DOWNLOAD_RENAME_TO_ARG, StorageSettings.getDatabaseName(context)); // rename to
		return intent;
	}
}
