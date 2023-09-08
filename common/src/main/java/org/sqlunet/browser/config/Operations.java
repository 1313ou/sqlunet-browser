/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.config;

import android.net.Uri;
import android.util.Pair;
import android.widget.TextView;

import org.sqlunet.browser.common.R;
import com.bbou.download.FileAsyncTask;
import com.bbou.download.FileAsyncTaskChooser;
import com.bbou.download.MD5AsyncTaskChooser;
import com.bbou.download.Settings;

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

	public static void unzipEntry(@NonNull final Uri uri, @NonNull final String entry, @NonNull final FragmentActivity activity)
	{
		FileAsyncTask.launchUnzip(activity, uri, entry, StorageSettings.getDataDir(activity), (result) -> {
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
		final Pair<CharSequence[], CharSequence[]> downloadDirs = StorageReports.getStyledDownloadNamesValues(activity);
		final Pair<CharSequence[], CharSequence[]> cachedDirs = StorageReports.getStyledCachesNamesValues(activity);
		FileAsyncTaskChooser.copyFromFile(activity, StorageSettings.getDatabasePath(activity), downloadDirs, cachedDirs);
		Settings.unrecordDatapack(activity);
	}

	public static void unzip(@NonNull FragmentActivity activity)
	{
		final Pair<CharSequence[], CharSequence[]> downloadDirs = StorageReports.getStyledDownloadNamesValues(activity);
		final Pair<CharSequence[], CharSequence[]> cachedDirs = StorageReports.getStyledCachesNamesValues(activity);
		FileAsyncTaskChooser.unzipEntryFromArchive(activity, StorageSettings.getDatabasePath(activity), downloadDirs, cachedDirs);
		Settings.unrecordDatapack(activity);
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
