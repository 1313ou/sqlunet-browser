package org.sqlunet.browser.config;

import android.net.Uri;
import android.util.Pair;
import android.widget.TextView;

import org.sqlunet.browser.common.R;
import org.sqlunet.download.FileAsyncTask;
import org.sqlunet.download.FileAsyncTaskChooser;
import org.sqlunet.download.MD5AsyncTaskChooser;
import org.sqlunet.settings.StorageReports;
import org.sqlunet.settings.StorageSettings;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

public class Operations
{
	public static void copy(@NonNull final Uri uri, @NonNull final FragmentActivity activity)
	{
		FileAsyncTask.launchCopy(activity, uri, StorageSettings.getDatabasePath(activity), (result) -> {
			if (result)
			{
				activity.finish();
			}
			else
			{
				TextView tv = activity.findViewById(R.id.status);
				if (tv != null)
				{
					tv.setText(R.string.result_fail);
				}
			}
		});
	}

	public static void unzip(@NonNull final Uri uri, @NonNull final FragmentActivity activity)
	{
		FileAsyncTask.launchUnzip(activity, uri, StorageSettings.getDataDir(activity), (result) -> {
			if (result)
			{
				activity.finish();
			}
			else
			{
				TextView tv = activity.findViewById(R.id.status);
				if (tv != null)
				{
					tv.setText(R.string.result_fail);
				}
			}
		});
	}

	public static void md5(@NonNull final Uri uri, @NonNull final FragmentActivity activity)
	{
		FileAsyncTask.launchMd5(activity, uri, activity::finish);
	}

	// file

	public static void copy(@NonNull FragmentActivity activity)
	{
		FileAsyncTaskChooser.copyFromFile(activity, StorageSettings.getCacheDir(activity), StorageSettings.getDatabasePath(activity));
		org.sqlunet.download.Settings.unrecordDb(activity);
	}

	public static void unzip(@NonNull FragmentActivity activity)
	{
		FileAsyncTaskChooser.unzipFromArchive(activity, StorageSettings.getCacheDir(activity), StorageSettings.getDatabasePath(activity));
		org.sqlunet.download.Settings.unrecordDb(activity);
	}

	public static void md5(@NonNull FragmentActivity activity)
	{
		final Pair<CharSequence[], CharSequence[]> downloadDirs = StorageReports.getStyledDownloadNamesValues(activity);
		final Pair<CharSequence[], CharSequence[]> cachedDirs = StorageReports.getStyledCachesNamesValues(activity);
		final Pair<CharSequence[], CharSequence[]> storageDirs = StorageReports.getStyledStorageDirectoriesNamesValues(activity);
		MD5AsyncTaskChooser.md5(activity, downloadDirs, cachedDirs, storageDirs);
	}

	// exec

	public static void execSql(@NonNull final Uri uri, @NonNull final FragmentActivity activity)
	{
		ExecAsyncTask.launchExecUri(activity, uri, StorageSettings.getDatabasePath(activity), (result) -> {
			if (result)
			{
				activity.finish();
			}
			else
			{
				TextView tv = activity.findViewById(R.id.status);
				if (tv != null)
				{
					tv.setText(R.string.result_fail);
				}
			}
		});
	}

	public static void execZippedSql(@NonNull final Uri uri, @NonNull final String entry, @NonNull final FragmentActivity activity)
	{
		ExecAsyncTask.launchExecZippedUri(activity, uri, entry, StorageSettings.getDatabasePath(activity), (result) -> {
			if (result)
			{
				activity.finish();
			}
			else
			{
				TextView tv = activity.findViewById(R.id.status);
				if (tv != null)
				{
					tv.setText(R.string.result_fail);
				}
			}
		});
	}
}
