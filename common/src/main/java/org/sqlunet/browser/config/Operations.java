package org.sqlunet.browser.config;

import android.net.Uri;
import android.util.Pair;

import org.sqlunet.download.FileAsyncTask;
import org.sqlunet.download.FileAsyncTaskChooser;
import org.sqlunet.download.MD5AsyncTaskChooser;
import org.sqlunet.settings.StorageReports;
import org.sqlunet.settings.StorageSettings;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

public class Operations
{
	public static void md5(@NonNull final Uri uri, @NonNull final FragmentActivity activity)
	{
		FileAsyncTask.launchMd5(activity, uri, activity::finish);
	}

	public static void copy(@NonNull final Uri uri, @NonNull final FragmentActivity activity)
	{
		FileAsyncTask.launchCopy(activity, uri, StorageSettings.getDatabasePath(activity), activity::finish);
	}

	public static void unzip(@NonNull final Uri uri, @NonNull final FragmentActivity activity)
	{
		FileAsyncTask.launchUnzip(activity, uri, StorageSettings.getDataDir(activity), activity::finish);
	}

	public static void execSql(final Uri uri, final OperationActivity activity)
	{
		ExecAsyncTask.launchExecUri(activity, uri, StorageSettings.getDatabasePath(activity), (result) -> {
			if (result)
			{
				activity.finish();
			}
		});
	}

	public static void execZippedSql(final Uri uri, final String entry, final OperationActivity activity)
	{
		ExecAsyncTask.launchExecZippedUri(activity, uri, entry, StorageSettings.getDatabasePath(activity), (result) -> {
			if (result)
			{
				activity.finish();
			}
		});
	}

	public static void copy(FragmentActivity activity)
	{
		FileAsyncTaskChooser.copyFromFile(activity, StorageSettings.getCacheDir(activity), StorageSettings.getDatabasePath(activity));
		org.sqlunet.download.Settings.unrecordDb(activity);
	}

	public static void unzip(FragmentActivity activity)
	{
		FileAsyncTaskChooser.unzipFromArchive(activity, StorageSettings.getCacheDir(activity), StorageSettings.getDatabasePath(activity));
		org.sqlunet.download.Settings.unrecordDb(activity);
	}

	public static void md5(FragmentActivity activity)
	{
		final Pair<CharSequence[], CharSequence[]> downloadDirs = StorageReports.getStyledDownloadNamesValues(activity);
		final Pair<CharSequence[], CharSequence[]> cachedDirs = StorageReports.getStyledCachesNamesValues(activity);
		final Pair<CharSequence[], CharSequence[]> storageDirs = StorageReports.getStyledStorageDirectoriesNamesValues(activity);
		MD5AsyncTaskChooser.md5(activity, downloadDirs, cachedDirs, storageDirs);
	}
}
