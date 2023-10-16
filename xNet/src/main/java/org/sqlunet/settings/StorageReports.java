/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.settings;

import android.annotation.TargetApi;
import android.content.Context;
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
import org.sqlunet.style.Colors;
import org.sqlunet.style.Factories;
import org.sqlunet.style.Report;
import org.sqlunet.style.Spanner;
import org.sqlunet.xnet.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * Storage styling utilities
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StorageReports
{
	static private final float ENLARGE = 1.2F;

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
		// icon
		Report.appendImage(context, sb, toIconId(dir.dir.getType()));
		sb.append(' ');
		// type
		Spanner.appendWithSpans(sb, ' ' + dir.dir.getType().toDisplay() + ' ', new BackgroundColorSpan(Colors.dirTypeBackColor), new ForegroundColorSpan(Colors.dirTypeForeColor), new RelativeSizeSpan(ENLARGE));
		sb.append('\n');
		// value
		Spanner.appendWithSpans(sb, dir.dir.getTaggedValue(), Factories.spans(Colors.dirValueBackColor, Colors.dirValueForeColor, new StyleSpan(Typeface.ITALIC)));
		sb.append('\n');
		// status
		final boolean suitable = dir.status == 0 && dir.fitsIn(context);
		Spanner.appendWithSpans(sb, StorageUtils.mbToString(dir.free), Factories.spans( //
				suitable ? Colors.dirOkBackColor : Colors.dirFailBackColor,  //
				suitable ? Colors.dirOkForeColor : Colors.dirFailForeColor));
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
		Spanner.appendWithSpans(sb, fitsIn ? "Fits in" : "Does not fit in", Factories.spans(fitsIn ? Colors.dirOkBackColor : Colors.dirFailBackColor, fitsIn ? Colors.dirOkForeColor : Colors.dirFailForeColor));
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
		final boolean statusOk = "Ok".equals(status.toString());
		Spanner.appendWithSpans(sb, "Status: " + status, Factories.spans(statusOk ? Colors.dirOkBackColor : Colors.dirFailBackColor, statusOk ? Colors.dirOkForeColor : Colors.dirFailForeColor));
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
	static public Pair<CharSequence[], CharSequence[]> getXStyledStorageDirectoriesNamesValues(@NonNull final Context context)
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
	static public Pair<CharSequence[], CharSequence[]> getStyledStoragesNamesValues(@NonNull final Context context)
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
	static public Pair<CharSequence[], CharSequence[]> getStyledStorageDirectoriesNamesValues(@NonNull final Context context)
	{
		final List<CharSequence> names = new ArrayList<>();
		final List<CharSequence> values = new ArrayList<>();
		final List<StorageDirectory> dirs = StorageUtils.getSortedStorageDirectories(context);

		int i = 1;
		for (StorageDirectory dir : dirs)
		{
			if (dir == null)
			{
				continue;
			}
			SpannableStringBuilder name = new SpannableStringBuilder();
			CharSequence value;
			DirType type = dir.dir.getType();
			if (type.equals(DirType.AUTO) || type.equals(DirType.APP_INTERNAL))
			{
				value = dir.dir.getFSValue();
				Report.appendHeader(name, "Files").append(' ').append(type.toShortDisplay()).append(' ').append(StorageUtils.storageFreeAsString(value.toString())).append('\n').append(value);
			}
			else
			{
				value = dir.dir.getValue();
				Report.appendHeader(name, "External files[" + i++ + "]").append(' ').append(type.toDisplay()).append(' ').append(StorageUtils.storageFreeAsString(value.toString())).append('\n').append(value);
			}
			names.add(name);
			values.add(value);
		}
		return new Pair<>(names.toArray(new CharSequence[0]), values.toArray(new CharSequence[0]));
	}

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

		// external
		int i = 1;
		for (File dir2 : ContextCompat.getExternalCacheDirs(context))
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

		// internal
		File dir = context.getCacheDir();
		if (dir != null)
		{
			value = dir.getAbsolutePath();
			name = new SpannableStringBuilder();
			Report.appendHeader(name, "Cache").append(' ').append(StorageUtils.storageFreeAsString(value.toString())).append('\n').append(value);
			names.add(name);
			values.add(value);
		}

		// convert to array
		final CharSequence[] entries = names.toArray(new CharSequence[0]);
		final CharSequence[] entryValues = values.toArray(new CharSequence[0]);

		return new Pair<>(entries, entryValues);
	}

	/**
	 * Get download directories as names and values
	 *
	 * @param context context
	 * @return pair of names and values
	 */
	@NonNull
	static public Pair<CharSequence[], CharSequence[]> getStyledDownloadNamesValues(@NonNull final Context context)
	{
		final List<CharSequence> names = new ArrayList<>();
		final List<CharSequence> values = new ArrayList<>();
		SpannableStringBuilder name;
		CharSequence value;

		List<File> dirs = new ArrayList<>();
		dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
		dirs.addAll(Arrays.asList(context.getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS)));
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
		{
			dirs.addAll(Arrays.asList(context.getExternalMediaDirs()));
		}

		for (File dir : dirs)
		{
			if (dir != null)
			{
				value = dir.getAbsolutePath();
				name = new SpannableStringBuilder();
				Report.appendHeader(name, "Download").append(' ').append(StorageUtils.storageFreeAsString(value.toString())).append('\n').append(value);
				names.add(name);
				values.add(value);
			}
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

	/**
	 * Report on external storage
	 *
	 * @param context context
	 * @return report
	 */
	@NonNull
	public static CharSequence reportStorageDirectories(@NonNull final Context context)
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
	 * @param context context
	 * @return report
	 */
	@NonNull
	public static CharSequence reportExternalStorage(@NonNull final Context context)
	{
		final Map<StorageType, File[]> storages = StorageUtils.getExternalStorages(context);
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

	@SafeVarargs
	@NonNull
	static public CharSequence namesValuesToReportStyled(@NonNull final Pair<CharSequence[], CharSequence[]>... directories)
	{
		@SuppressWarnings("TypeMayBeWeakened") final SpannableStringBuilder sb = new SpannableStringBuilder();
		for (Pair<CharSequence[], CharSequence[]> namesValues : directories)
		{
			final CharSequence[] names = namesValues.first;
			for (CharSequence name : names)
			{
				sb.append(name);
				sb.append('\n');
			}
			sb.append('\n');
		}
		return sb;
	}

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
		final Map<StorageType, File[]> storages = StorageUtils.getExternalStorages(context);
		final File[] physical = storages.get(StorageType.PRIMARY_PHYSICAL);
		final File[] emulated = storages.get(StorageType.PRIMARY_EMULATED);
		final File[] secondary = storages.get(StorageType.SECONDARY);

		final SpannableStringBuilder sb = new SpannableStringBuilder();
		if (physical != null)
		{
			Report.appendImage(context, sb, R.drawable.ic_storage_intern);
			sb.append(' ');
			Spanner.appendWithSpans(sb, " primary physical ", new BackgroundColorSpan(Colors.storageTypeBackColor), new ForegroundColorSpan(Colors.storageTypeForeColor), new RelativeSizeSpan(ENLARGE));
			sb.append('\n');
			for (File f : physical)
			{
				final String s = f.getAbsolutePath();
				Spanner.appendWithSpans(sb, s, Factories.spans(Colors.storageValueBackColor, Colors.storageValueForeColor, new StyleSpan(Typeface.ITALIC)));
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
			Spanner.appendWithSpans(sb, " primary emulated ", new BackgroundColorSpan(Colors.storageTypeBackColor), new ForegroundColorSpan(Colors.storageTypeForeColor), new RelativeSizeSpan(ENLARGE));
			sb.append('\n');
			for (File f : emulated)
			{
				final String s = f.getAbsolutePath();
				Spanner.appendWithSpans(sb, s, Factories.spans(Colors.storageValueBackColor, Colors.storageValueForeColor, new StyleSpan(Typeface.ITALIC)));
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
			Spanner.appendWithSpans(sb, " secondary ", new BackgroundColorSpan(Colors.storageTypeBackColor), new ForegroundColorSpan(Colors.storageTypeForeColor), new RelativeSizeSpan(ENLARGE));
			sb.append('\n');
			for (File f : secondary)
			{
				final String s = f.getAbsolutePath();
				Spanner.appendWithSpans(sb, s, Factories.spans(Colors.storageValueBackColor, Colors.storageValueForeColor, new StyleSpan(Typeface.ITALIC)));
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
		sb.append('\n');

		// external files
		int i = 1;
		for (File dir : ContextCompat.getExternalFilesDirs(context, null))
		{
			appendDir(sb, "external files dir [" + i++ + ']', dir);
		}

		// external caches
		i = 1;
		for (File dir : ContextCompat.getExternalCacheDirs(context))
		{
			appendDir(sb, "external cache dir [" + i++ + ']', dir);
		}

		// obbs
		i = 1;
		for (File dir : ContextCompat.getObbDirs(context))
		{
			appendDir(sb, "external obb dir [" + i++ + ']', dir);
		}
		return sb;
	}
}
