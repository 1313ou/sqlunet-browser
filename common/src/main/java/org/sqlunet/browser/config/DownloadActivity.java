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

import com.bbou.download.Settings.Downloader;

import org.sqlunet.browser.EntryActivity;
import org.sqlunet.browser.MenuHandler;
import org.sqlunet.browser.common.R;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import static com.bbou.download.AbstractDownloadFragment.DOWNLOAD_DOWNLOADER_ARG;
import static com.bbou.download.AbstractDownloadFragment.DOWNLOAD_FROM_ARG;
import static com.bbou.download.AbstractDownloadFragment.DOWNLOAD_TARGET_FILE_ARG;
import static com.bbou.download.AbstractDownloadFragment.THEN_UNZIP_TO_ARG;
import static com.bbou.download.BaseDownloadFragment.DOWNLOAD_RENAME_FROM_ARG;
import static com.bbou.download.BaseDownloadFragment.DOWNLOAD_RENAME_TO_ARG;
import static com.bbou.download.DownloadFragment.DOWNLOAD_TO_FILE_ARG;
import static com.bbou.download.DownloadZipFragment.DOWNLOAD_ENTRY_ARG;
import static com.bbou.download.DownloadZipFragment.DOWNLOAD_TO_DIR_ARG;

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
		String pref = Settings.getDownloaderPref(context);
		String type = pref;
		if (type == null)
		{
			type = "DOWNLOAD";
		}
		switch (type)
		{
			default:
			case "DOWNLOAD":
				return makeIntentPlainDownload(context);
			case "DOWNLOAD_ZIP":
				return makeIntentZipDownload(context);
			case "DOWNLOAD_ZIP_THEN_UNZIP":
				return makeIntentDownloadThenDeploy(context);
		}
	}

	public static Intent makeIntentPlainDownload(@NonNull final Context context)
	{
		String dbTarget = StorageSettings.getDbDownloadTarget(context);
		Intent intent = new Intent(context, DownloadActivity.class);
		intent.putExtra(DOWNLOAD_DOWNLOADER_ARG, Downloader.DOWNLOAD.toString()); // plain transfer
		intent.putExtra(DOWNLOAD_FROM_ARG, StorageSettings.getDbDownloadSource(context)); // source file
		intent.putExtra(DOWNLOAD_TO_FILE_ARG, dbTarget); // dest file
		intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, dbTarget); // target file
		return intent;
	}

	public static Intent makeIntentZipDownload(@NonNull final Context context)
	{
		String zipEntry = Uri.parse(StorageSettings.getDbDownloadSource(context)).getLastPathSegment();
		String dbName = StorageSettings.getDatabaseName();
		Intent intent = new Intent(context, DownloadActivity.class);
		intent.putExtra(DOWNLOAD_DOWNLOADER_ARG, Downloader.DOWNLOAD_ZIP.toString()); // zipped transfer
		intent.putExtra(DOWNLOAD_FROM_ARG, StorageSettings.getDbDownloadZippedSource(context)); // source archive
		intent.putExtra(DOWNLOAD_ENTRY_ARG, zipEntry); // zip entry
		intent.putExtra(DOWNLOAD_TO_DIR_ARG, StorageSettings.getDataDir(context)); // dest directory
		intent.putExtra(DOWNLOAD_RENAME_FROM_ARG, zipEntry); // rename from
		intent.putExtra(DOWNLOAD_RENAME_TO_ARG, dbName); // rename to
		intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, StorageSettings.getDbDownloadTarget(context)); // target file
		return intent;
	}

	public static Intent makeIntentDownloadThenDeploy(@NonNull final Context context)
	{
		Intent intent = new Intent(context, DownloadActivity.class);
		intent.putExtra(DOWNLOAD_DOWNLOADER_ARG, Downloader.DOWNLOAD.toString()); // plain transfer
		intent.putExtra(DOWNLOAD_FROM_ARG, StorageSettings.getDbDownloadZippedSource(context)); // source archive
		intent.putExtra(DOWNLOAD_TO_FILE_ARG, StorageSettings.getDbDownloadZippedTarget(context)); // destination archive
		intent.putExtra(THEN_UNZIP_TO_ARG, StorageSettings.getDataDir(context)); // unzip destination directory
		intent.putExtra(DOWNLOAD_RENAME_FROM_ARG, StorageSettings.getDbDownloadFile(context)); // rename from
		intent.putExtra(DOWNLOAD_RENAME_TO_ARG, StorageSettings.getDatabaseName()); // rename to
		intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, StorageSettings.getDbDownloadTarget(context)); // target file
		return intent;
	}
}
