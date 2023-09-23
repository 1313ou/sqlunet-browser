/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.config;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.bbou.deploy.Deploy;
import com.bbou.download.Settings;

import org.sqlunet.settings.StorageSettings;

import androidx.annotation.NonNull;

import static com.bbou.download.AbstractDownloadFragment.DOWNLOAD_FROM_ARG;
import static com.bbou.download.AbstractDownloadFragment.DOWNLOAD_MODE_ARG;
import static com.bbou.download.AbstractDownloadFragment.DOWNLOAD_RENAME_FROM_ARG;
import static com.bbou.download.AbstractDownloadFragment.DOWNLOAD_RENAME_TO_ARG;
import static com.bbou.download.AbstractDownloadFragment.DOWNLOAD_TARGET_FILE_ARG;
import static com.bbou.download.AbstractDownloadFragment.THEN_UNZIP_TO_ARG;
import static com.bbou.download.DownloadFragment.DOWNLOAD_TO_FILE_ARG;
import static com.bbou.download.DownloadZipFragment.DOWNLOAD_ENTRY_ARG;
import static com.bbou.download.DownloadZipFragment.DOWNLOAD_TO_DIR_ARG;

public class DownloadIntentFactory
{
	public static Intent makeIntent(@NonNull final Context context)
	{
		Settings.Mode type = Settings.Mode.getModePref(context);
		if (type == null)
		{
			type = Settings.Mode.DOWNLOAD_ZIP;
		}
		return makeIntent(context, type);
	}

	public static Intent makeIntent(@NonNull final Context context, @NonNull final Settings.Mode type)
	{
		switch (type)
		{
			case DOWNLOAD:
				return makeIntentPlainDownload(context);
			case DOWNLOAD_ZIP:
				return makeIntentZipDownload(context);
			case DOWNLOAD_ZIP_THEN_UNZIP:
				return makeIntentDownloadThenDeploy(context);
			default:
				throw new RuntimeException(type.toString());
		}
	}

	public static Intent makeIntentPlainDownload(@NonNull final Context context)
	{
		final String dbSource = StorageSettings.getDbDownloadSourcePath(context);
		return makeIntentPlainDownload(context, dbSource);
	}

	public static Intent makeIntentPlainDownload(@NonNull final Context context, final String dbSource)
	{
		final String dbDest = StorageSettings.getDatabasePath(context);
		final Intent intent = new Intent(context, DownloadActivity.class);
		intent.putExtra(DOWNLOAD_MODE_ARG, Settings.Mode.DOWNLOAD.toString()); // plain transfer
		intent.putExtra(DOWNLOAD_FROM_ARG, dbSource); // source file
		intent.putExtra(DOWNLOAD_TO_FILE_ARG, dbDest); // dest file
		intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, dbDest); // target file
		return intent;
	}

	public static Intent makeIntentZipDownload(@NonNull final Context context)
	{
		final String dbZipSource = StorageSettings.getDbDownloadZippedSourcePath(context);
		return makeIntentZipDownload(context, dbZipSource);
	}

	public static Intent makeIntentZipDownload(@NonNull final Context context, final String dbZipSource)
	{
		final String dbZipEntry = StorageSettings.getDbDownloadName(context);
		final String dbDestDir = StorageSettings.getDataDir(context);
		final String dbName = StorageSettings.getDatabaseName();
		final String dbTarget = StorageSettings.getDatabasePath(context);
		final Intent intent = new Intent(context, DownloadActivity.class);
		intent.putExtra(DOWNLOAD_MODE_ARG, Settings.Mode.DOWNLOAD_ZIP.toString()); // zipped transfer
		intent.putExtra(DOWNLOAD_FROM_ARG, dbZipSource); // source archive
		intent.putExtra(DOWNLOAD_TO_DIR_ARG, dbDestDir); // dest directory
		intent.putExtra(DOWNLOAD_ENTRY_ARG, dbZipEntry); // zip entry
		intent.putExtra(DOWNLOAD_RENAME_FROM_ARG, dbZipEntry); // rename from
		intent.putExtra(DOWNLOAD_RENAME_TO_ARG, dbName); // rename to
		intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, dbTarget); // target file
		return intent;
	}

	public static Intent makeIntentDownloadThenDeploy(@NonNull final Context context)
	{
		final String dbZipSource = StorageSettings.getDbDownloadZippedSourcePath(context);
		final String dbZipDest = StorageSettings.getCachedZippedPath(context);
		return makeIntentDownloadThenDeploy(context, dbZipSource, dbZipDest);
	}

	public static Intent makeIntentDownloadThenDeploy(@NonNull final Context context, final String dbZipSource, final String dbZipDest)
	{
		final String dbDir = StorageSettings.getDataDir(context);
		final String dbRenameFrom = StorageSettings.getDbDownloadName(context);
		final String dbRenameTo = StorageSettings.getDatabaseName();
		final String dbTarget = StorageSettings.getDatabasePath(context);
		final Intent intent = new Intent(context, DownloadActivity.class);
		intent.putExtra(DOWNLOAD_MODE_ARG, Settings.Mode.DOWNLOAD_ZIP_THEN_UNZIP.toString()); // zip transfer then unzip
		intent.putExtra(DOWNLOAD_FROM_ARG, dbZipSource); // source archive
		intent.putExtra(DOWNLOAD_TO_FILE_ARG, dbZipDest); // destination archive
		intent.putExtra(THEN_UNZIP_TO_ARG, dbDir); // unzip destination directory
		intent.putExtra(DOWNLOAD_RENAME_FROM_ARG, dbRenameFrom); // rename from
		intent.putExtra(DOWNLOAD_RENAME_TO_ARG, dbRenameTo); // rename to
		intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, dbTarget); // target file
		return intent;
	}

	public static Intent makeUpdateIntent(@NonNull final Context context)
	{
		final String downloadSourceType = Settings.getDatapackSourceType(context);
		final String downloadSourceUrl = "download".equals(downloadSourceType) ? Settings.getDatapackSource(context) : StorageSettings.getDbDownloadZippedSourcePath(context);
		assert downloadSourceUrl != null;
		if (!downloadSourceUrl.endsWith(Deploy.ZIP_EXTENSION)) //
		{
			return makeIntentPlainDownload(context, downloadSourceUrl);
		}

		// source has zip extension
		Settings.Mode mode = Settings.Mode.getModePref(context);
		if (mode == null)
		{
			mode = Settings.Mode.DOWNLOAD_ZIP;
		}
		switch (mode)
		{
			case DOWNLOAD_ZIP_THEN_UNZIP:
				final String name = Uri.parse(downloadSourceUrl).getLastPathSegment();
				final String cache = StorageSettings.getCacheDir(context);
				final String cachePath = cache + '/' + name;
				return makeIntentDownloadThenDeploy(context, downloadSourceUrl, cachePath);

			case DOWNLOAD_ZIP:
				return makeIntentZipDownload(context, downloadSourceUrl);

			case DOWNLOAD:
				throw new RuntimeException(mode.toString());
				// return makeIntentPlainDownload(context, downloadSourceUrl);

			default:
				throw new RuntimeException(mode.toString());
		}
	}
}
