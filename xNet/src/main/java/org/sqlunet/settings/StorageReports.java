/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Pair;

import org.sqlunet.settings.StorageUtils.DirType;
import org.sqlunet.settings.StorageUtils.StorageDirectory;
import org.sqlunet.settings.StorageUtils.StorageType;
import org.sqlunet.style.Report;
import org.sqlunet.xnet.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Storage styling utilities
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StorageReports
{
	// Colors

	static private final int dkgreen = 0xFF008B00;

	static private final int dkred = 0xFF8B0000;

	static private final int ltyellow = 0xFFFFFF99;

	static private final int pink = 0xFFE9967A;

	/**
	 * Styled string
	 *
	 * @param context context
	 * @param dir     storage directory
	 * @return styled string
	 */
	@NonNull
	static private CharSequence toStyledString(@NonNull final Context context, @NonNull final StorageDirectory dir)
	{
		final SpannableStringBuilder sb = new SpannableStringBuilder();
		Report.appendImage(context, sb, toIconId(dir.dir.getType()));
		sb.append(' ');
		Report.append(sb, ' ' + dir.dir.getType().toDisplay() + ' ', new RelativeSizeSpan(1.5F), new BackgroundColorSpan(ltyellow), new ForegroundColorSpan(Color.BLACK));
		sb.append('\n');
		Report.append(sb, dir.dir.getValue(), new StyleSpan(Typeface.ITALIC), new ForegroundColorSpan(Color.GRAY));
		sb.append('\n');
		Report.append(sb, StorageUtils.mbToString(dir.free), new ForegroundColorSpan(dir.status == 0 && dir.fitsIn(context) ? dkgreen : dkred));

		return sb;
	}

	/**
	 * Styled fits-in string
	 *
	 * @param context context
	 * @param dir     storage directory
	 * @return styled string
	 */
	@NonNull
	static private CharSequence styledFitsIn(@NonNull final Context context, @NonNull final StorageDirectory dir)
	{
		final SpannableStringBuilder sb = new SpannableStringBuilder();
		final boolean fitsIn = dir.fitsIn(context);
		Report.append(sb, fitsIn ? "Fits in" : "Does not fit in", new ForegroundColorSpan(fitsIn ? dkgreen : dkred));
		return sb;
	}

	/**
	 * Styled status string
	 *
	 * @param dir storage directory
	 * @return styled string
	 */
	@NonNull
	static private CharSequence styledStatus(@NonNull final StorageDirectory dir)
	{
		final SpannableStringBuilder sb = new SpannableStringBuilder();
		final CharSequence status = dir.status();
		final boolean isOk = "Ok".equals(status.toString());
		Report.append(sb, status, new ForegroundColorSpan(isOk ? dkgreen : dkred));
		return sb;
	}

	/**
	 * Dir type to icon id
	 *
	 * @param type dir type
	 * @return res id
	 */
	static private int toIconId(@NonNull final DirType type)
	{
		int resId = 0;
		switch (type)
		{
			case AUTO:
				resId = R.drawable.ic_storage_auto;
				break;
			case APP_INTERNAL:
				resId = R.drawable.ic_storage_intern;
				break;
			case APP_EXTERNAL_PRIMARY:
				resId = R.drawable.ic_storage_extern_primary;
				break;
			case APP_EXTERNAL_SECONDARY:
				resId = R.drawable.ic_storage_extern_secondary;
				break;
			case PUBLIC_EXTERNAL_PRIMARY:
			case PUBLIC_EXTERNAL_SECONDARY:
				resId = R.drawable.ic_storage_extern_public;
				break;
		}
		return resId;
	}

	// N A M E S - V A L U E S

	/**
	 * Get storage directories as names and values
	 *
	 * @param context context
	 * @return pair of names and values
	 */
	@NonNull
	static public Pair<CharSequence[], CharSequence[]> getStyledStorageDirectoriesNamesValues(@NonNull final Context context)
	{
		final List<CharSequence> names = new ArrayList<>();
		final List<CharSequence> values = new ArrayList<>();
		final List<StorageDirectory> dirs = StorageUtils.getSortedStorageDirectories(context);
		for (StorageDirectory dir : dirs)
		{
			if (dir.status != 0)
			{
				continue;
			}

			// name
			names.add(toStyledString(context, dir));

			// value
			values.add(dir.dir.getValue());
		}
		return new Pair<>(names.toArray(new CharSequence[0]), values.toArray(new CharSequence[0]));
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
		final List<StorageDirectory> dirs = StorageUtils.getSortedStorageDirectories(context);
		for (StorageDirectory dir : dirs)
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
		final List<StorageDirectory> dirs = StorageUtils.getSortedStorageDirectories(context);
		for (StorageDirectory dir : dirs)
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

	/**
	 * Get cache directories as names and values
	 *
	 * @param context context
	 * @return pair of names and values
	 */
	@SuppressWarnings("deprecation")
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

	// D I R

	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	static private SpannableStringBuilder appendDir(@NonNull final SpannableStringBuilder sb, final CharSequence header, @Nullable final File dir)
	{
		if (dir != null)
		{
			Report.appendHeader(sb, header).append(' ').append(StorageUtils.storageFreeAsString(dir)).append('\n').append(dir.getAbsolutePath()).append('\n');
		}
		return sb;
	}

	// R E P O R T S

	@NonNull
	static CharSequence reportStorageDirectories(@NonNull final Context context)
	{
		final StringBuilder sb = new StringBuilder();
		int i = 1;
		final List<StorageDirectory> dirs = StorageUtils.getSortedStorageDirectories(context);
		for (StorageDirectory dir : dirs)
		{
			sb.append(i++);
			sb.append(' ');
			sb.append('-');
			sb.append(' ');
			sb.append(dir.toLongString());
			sb.append(' ');
			sb.append(dir.fitsIn(context) ? "Fits in" : "Does not fit in");
			sb.append('\n');
			sb.append('\n');
		}
		return sb;
	}

	/**
	 * Report on external storage
	 *
	 * @return report
	 */
	@NonNull
	static CharSequence reportExternalStorage()
	{
		final Map<StorageType, File[]> storages = StorageUtils.getExternalStorages();
		final File[] physical = storages.get(StorageType.PRIMARY_PHYSICAL);
		final File[] emulated = storages.get(StorageType.PRIMARY_EMULATED);
		final File[] secondary = storages.get(StorageType.SECONDARY);

		final StringBuilder sb = new StringBuilder();
		if (physical != null)
		{
			sb.append("primary physical:\n");
			for (File f : physical)
			{
				final String s = f.getAbsolutePath();
				sb.append(s);
				sb.append(' ');
				sb.append(StorageUtils.mbToString(StorageUtils.storageCapacity(s)));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
				{
					sb.append(' ');
					try
					{
						sb.append(Environment.isExternalStorageEmulated(f) ? "emulated" : "not-emulated");
					}
					catch (Throwable e)
					{
						//
					}
				}
				sb.append('\n');
			}
		}
		if (emulated != null)
		{
			sb.append("primary emulated:\n");
			for (File f : emulated)
			{
				final String s = f.getAbsolutePath();
				sb.append(s);
				sb.append(' ');
				sb.append(StorageUtils.mbToString(StorageUtils.storageCapacity(s)));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
				{
					sb.append(' ');
					try
					{
						sb.append(Environment.isExternalStorageEmulated(f) ? "emulated" : "not-emulated");
					}
					catch (Throwable e)
					{
						//
					}
				}
				sb.append('\n');
			}
		}
		if (secondary != null)
		{
			sb.append("secondary:\n");
			for (File f : secondary)
			{
				final String s = f.getAbsolutePath();
				sb.append(s);
				sb.append(' ');
				sb.append(StorageUtils.mbToString(StorageUtils.storageCapacity(s)));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
				{
					sb.append(' ');
					try
					{
						sb.append(Environment.isExternalStorageEmulated(f) ? "emulated" : "not-emulated");
					}
					catch (Throwable e)
					{
						//
					}
				}
				sb.append('\n');
			}
		}
		return sb.toString();
	}

	// S T Y L E D   R E P O R T S

	/**
	 * Report on storage dirs
	 *
	 * @param context context
	 * @return report
	 */
	@NonNull
	static public CharSequence reportStyledStorageDirectories(@NonNull final Context context)
	{
		@SuppressWarnings("TypeMayBeWeakened") final SpannableStringBuilder sb = new SpannableStringBuilder();
		final List<StorageDirectory> dirs = StorageUtils.getSortedStorageDirectories(context);
		boolean first = true;
		for (StorageDirectory dir : dirs)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append('\n');
			}

			sb.append(toStyledString(context, dir));
			sb.append(' ');
			//sb.append(styledFitsIn(dir));
			//sb.append(' ');
			//if (dir.fitsIn())
			{
				//sb.append('|');
				//sb.append(' ');
				sb.append(styledStatus(dir));
			}
		}
		return sb;
	}

	/**
	 * Report on external storage
	 *
	 * @param context context
	 * @return report
	 */
	@NonNull
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	static public CharSequence reportStyledExternalStorage(@NonNull final Context context)
	{
		final Map<StorageType, File[]> storages = StorageUtils.getExternalStorages();
		final File[] physical = storages.get(StorageType.PRIMARY_PHYSICAL);
		final File[] emulated = storages.get(StorageType.PRIMARY_EMULATED);
		final File[] secondary = storages.get(StorageType.SECONDARY);

		final SpannableStringBuilder sb = new SpannableStringBuilder();
		if (physical != null)
		{
			Report.appendImage(context, sb, R.drawable.ic_storage_intern);
			sb.append(' ');
			Report.append(sb, " primary physical ", new RelativeSizeSpan(1.5F), new BackgroundColorSpan(pink), new ForegroundColorSpan(Color.BLACK));
			sb.append('\n');
			for (File f : physical)
			{
				final String s = f.getAbsolutePath();
				Report.append(sb, s, new StyleSpan(Typeface.ITALIC), new ForegroundColorSpan(Color.GRAY));
				sb.append('\n');
				sb.append(StorageUtils.mbToString(StorageUtils.storageCapacity(s)));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
				{
					sb.append(' ');
					try
					{
						sb.append(Environment.isExternalStorageEmulated(f) ? "emulated" : "not-emulated");
					}
					catch (Throwable e)
					{
						//
					}
				}
				sb.append('\n');
			}
		}
		if (emulated != null)
		{
			Report.appendImage(context, sb, R.drawable.ic_storage_extern_primary);
			sb.append(' ');
			Report.append(sb, " primary emulated ", new RelativeSizeSpan(1.5F), new BackgroundColorSpan(pink), new ForegroundColorSpan(Color.BLACK));
			sb.append('\n');
			for (File f : emulated)
			{
				final String s = f.getAbsolutePath();
				Report.append(sb, s, new StyleSpan(Typeface.ITALIC), new ForegroundColorSpan(Color.GRAY));
				sb.append('\n');
				sb.append(StorageUtils.mbToString(StorageUtils.storageCapacity(s)));
				sb.append(' ');
				try
				{
					sb.append(Environment.isExternalStorageEmulated(f) ? "emulated" : "not-emulated");
				}
				catch (Throwable e)
				{ //
				}
				sb.append('\n');
			}
		}
		if (secondary != null)
		{
			Report.appendImage(context, sb, R.drawable.ic_storage_extern_secondary);
			sb.append(' ');
			sb.append(' ');
			Report.append(sb, " secondary ", new RelativeSizeSpan(1.5F), new BackgroundColorSpan(pink), new ForegroundColorSpan(Color.BLACK));
			sb.append('\n');
			for (File f : secondary)
			{
				final String s = f.getAbsolutePath();
				Report.append(sb, s, new StyleSpan(Typeface.ITALIC), new ForegroundColorSpan(Color.GRAY));
				sb.append('\n');
				sb.append(StorageUtils.mbToString(StorageUtils.storageCapacity(s)));
				sb.append(' ');
				try
				{
					sb.append(Environment.isExternalStorageEmulated(f) ? "emulated" : "not-emulated");
				}
				catch (Throwable e)
				{ //
				}
				sb.append('\n');
			}
		}
		return sb;
	}

	/**
	 * Report directories
	 *
	 * @param context context
	 * @return directories report
	 */
	@NonNull
	static public CharSequence reportStyledDirs(@NonNull final Context context)
	{
		final SpannableStringBuilder sb = new SpannableStringBuilder();

		appendDir(sb, "files dir", context.getFilesDir());
		appendDir(sb, "cache dir", context.getCacheDir());
		appendDir(sb, "obb dir", context.getObbDir());

		// external files
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			int i = 1;
			for (File dir : context.getExternalFilesDirs(null))
			{
				appendDir(sb, "external files dir [" + i++ + ']', dir);
			}
		}
		else
		{
			final File dir = context.getExternalFilesDir(null);
			appendDir(sb, "external files dir", dir);
		}

		// external caches
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			int i = 1;
			for (File dir : context.getExternalCacheDirs())
			{
				appendDir(sb, "external cache dir [" + i++ + ']', dir);
			}
		}
		else
		{
			final File dir = context.getExternalCacheDir();
			appendDir(sb, "external cache dir", dir);
		}

		// obbs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			int i = 1;
			for (File dir : context.getObbDirs())
			{
				appendDir(sb, "external obb dir [" + i++ + ']', dir);
			}
		}

		return sb;
	}
}
