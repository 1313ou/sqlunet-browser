/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.SpannableStringBuilder;
import android.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

class StorageReports
{
	/**
	 * Get cache directories as names and values
	 *
	 * @param context context
	 * @return pair of names and values
	 */
	@NonNull
	static public Pair<CharSequence[], CharSequence[]> getStyledCachesNamesValues(@NonNull final Context context)
	{
		final List<CharSequence> names = new ArrayList<>();
		final List<CharSequence> values = new ArrayList<>();
		SpannableStringBuilder name;
		CharSequence value;
		File dir = context.getExternalCacheDir();
		if (dir != null)
		{
			value = dir.getAbsolutePath();
			name = new SpannableStringBuilder();
			Report.appendHeader(name, "External cache").append(' ').append(StorageUtils.storageFreeAsString(value.toString())).append('\n').append(value);
			names.add(name);
			values.add(value);
		}

		dir = context.getCacheDir();
		if (dir != null)
		{
			value = dir.getAbsolutePath();
			name = new SpannableStringBuilder();
			Report.appendHeader(name, "Cache").append(' ').append(StorageUtils.storageFreeAsString(value.toString())).append('\n').append(value);
			names.add(name);
			values.add(value);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			int i = 1;
			for (File dir2 : context.getExternalCacheDirs())
			{
				if (dir2 == null)
				{
					continue;
				}
				value = dir2.getAbsolutePath();
				name = new SpannableStringBuilder();
				Report.appendHeader(name, "External cache[" + i++ + "]").append(' ').append(StorageUtils.storageFreeAsString(value.toString())).append('\n').append(value);
				names.add(name);
				values.add(value);
			}
		}

		dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		if (dir != null)
		{
			value = dir.getAbsolutePath();
			name = new SpannableStringBuilder();
			Report.appendHeader(name, "Download").append(' ').append(StorageUtils.storageFreeAsString(value.toString())).append('\n').append(value);
			names.add(name);
			values.add(value);
		}

		// convert to array
		final CharSequence[] entries = names.toArray(new CharSequence[0]);
		final CharSequence[] entryValues = values.toArray(new CharSequence[0]);

		return new Pair<>(entries, entryValues);
	}

	/**
	 * Get storage directories as names and values
	 *
	 * @param context context
	 * @return pair of names and values
	 */
	@NonNull
	static public Pair<CharSequence[], CharSequence[]> getStorageDirectoriesNamesValues(@NonNull final Context context)
	{
		final List<CharSequence> names = new ArrayList<>();
		final List<CharSequence> values = new ArrayList<>();
		final List<StorageUtils.StorageDirectory> dirs = StorageUtils.getSortedStorageDirectories(context);
		for (StorageUtils.StorageDirectory dir : dirs)
		{
			if (dir.status != 0)
			{
				continue;
			}

			// name
			names.add(dir.dir.getType().toDisplay() + ' ' + StorageUtils.storageFreeAsString(dir.dir.getValue().toString()));

			// value
			values.add(dir.dir.getFile().getAbsolutePath());
		}
		return new Pair<>(names.toArray(new CharSequence[0]), values.toArray(new CharSequence[0]));
	}

	/**
	 * Get storage dirs names and values
	 *
	 * @param context context
	 * @return pair of names and values
	 */
	@NonNull
	static public Pair<CharSequence[], CharSequence[]> get2StorageDirectoriesNamesValues(@NonNull final Context context)
	{
		final List<CharSequence> names = new ArrayList<>();
		final List<CharSequence> values = new ArrayList<>();
		final List<StorageUtils.StorageDirectory> dirs = StorageUtils.getSortedStorageDirectories(context);
		for (StorageUtils.StorageDirectory dir : dirs)
		{
			if (dir.status != 0)
			{
				continue;
			}
			// name
			names.add(dir.toShortString());

			// value
			values.add(dir.dir.getValue());
		}
		return new Pair<>(names.toArray(new CharSequence[0]), values.toArray(new CharSequence[0]));
	}
}
