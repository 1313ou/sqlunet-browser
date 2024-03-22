/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.config;

import android.net.Uri;
import android.util.Pair;
import android.widget.TextView;

import com.bbou.deploy.workers.FileTasks;
import com.bbou.download.preference.Settings;
import com.bbou.download.workers.choose.FileTaskChooser;
import com.bbou.download.workers.choose.MD5Chooser;

import org.sqlunet.browser.common.R;
import org.sqlunet.settings.StorageReports;
import org.sqlunet.settings.StorageSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

public class Operations
{
	public static void copy(@NonNull final Uri uri, @NonNull final FragmentActivity activity)
	{
		FileTasks.launchCopy(activity, uri, StorageSettings.getDatabasePath(activity), (result) -> {
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
		FileTasks.launchUnzip(activity, uri, StorageSettings.getDataDir(activity), (result) -> {
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
		FileTasks.launchUnzip(activity, uri, entry, StorageSettings.getDataDir(activity), (result) -> {
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
		FileTasks.launchMd5(activity, uri, activity::finish);
	}

	// file

	public static void copy(@NonNull FragmentActivity activity)
	{
		final Pair<CharSequence[], String[]> downloadDirs = StorageReports.getStyledDownloadNamesValues(activity);
		final Pair<CharSequence[], String[]> cachedDirs = StorageReports.getStyledCachesNamesValues(activity);
		FileTaskChooser.copyFromFile(activity, StorageSettings.getDatabasePath(activity), mergeNamesValues(downloadDirs, cachedDirs));
		Settings.unrecordDatapack(activity);
	}

	public static void unzip(@NonNull FragmentActivity activity)
	{
		final Pair<CharSequence[], String[]> downloadDirs = StorageReports.getStyledDownloadNamesValues(activity);
		final Pair<CharSequence[], String[]> cachedDirs = StorageReports.getStyledCachesNamesValues(activity);
		FileTaskChooser.unzipEntryFromArchive(activity, StorageSettings.getDatabasePath(activity), mergeNamesValues(downloadDirs, cachedDirs));
		Settings.unrecordDatapack(activity);
	}

	public static void md5(@NonNull FragmentActivity activity)
	{
		final Pair<CharSequence[], String[]> downloadDirs = StorageReports.getStyledDownloadNamesValues(activity);
		final Pair<CharSequence[], String[]> cachedDirs = StorageReports.getStyledCachesNamesValues(activity);
		final Pair<CharSequence[], String[]> storageDirs = StorageReports.getStyledStorageDirectoriesNamesValues(activity);
		MD5Chooser.md5(activity, mergeNamesValues(downloadDirs, cachedDirs, storageDirs));
	}

	@SafeVarargs
	static private kotlin.Pair<? extends CharSequence[], String[]> mergeNamesValues(Pair<CharSequence[], String[]> nameValues1, Pair<CharSequence[], String[]>... otherNameValues)
	{
		List<CharSequence> names = new ArrayList<>(Arrays.asList(nameValues1.first));
		for (Pair<CharSequence[], String[]> nameValues2 : otherNameValues)
		{
			names.addAll(Arrays.asList(nameValues2.first));
		}
		List<String> values = new ArrayList<>(Arrays.asList(nameValues1.second));
		for (Pair<CharSequence[], String[]> nameValues2 : otherNameValues)
		{
			values.addAll(Arrays.asList(nameValues2.second));
		}
		return new kotlin.Pair<>(names.toArray(new CharSequence[0]), values.toArray(new String[0]));
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
