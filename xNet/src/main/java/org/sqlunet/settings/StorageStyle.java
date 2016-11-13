package org.sqlunet.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Pair;

import org.sqlunet.settings.StorageUtils.CandidateStorage;
import org.sqlunet.settings.StorageUtils.DirType;
import org.sqlunet.settings.StorageUtils.StorageType;
import org.sqlunet.xnet.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Storage styling utilities
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StorageStyle
{
	// Colors

	static private final int dkgreen = 0xFF008B00;

	static private final int dkred = 0xFF8B0000;

	static private final int ltyellow = 0xFFFFFF99;

	static private final int pink = 0xFFE9967A;

	/**
	 * Styled string
	 *
	 * @param candidate candidate storage
	 * @return styled string
	 */
	static private CharSequence toStyledString(final Context context, final CandidateStorage candidate)
	{
		final SpannableStringBuilder sb = new SpannableStringBuilder();
		appendImage(context, sb, toResId(candidate.dir.type));
		sb.append(' ');
		append(sb, ' ' + candidate.dir.type.toString() + ' ', new RelativeSizeSpan(1.5F), new BackgroundColorSpan(ltyellow), new ForegroundColorSpan(Color.BLACK));
		sb.append('\n');
		append(sb, candidate.dir.file.getAbsolutePath(), new StyleSpan(Typeface.ITALIC), new ForegroundColorSpan(Color.GRAY));
		sb.append('\n');
		append(sb, StorageUtils.mbToString(candidate.free), new ForegroundColorSpan(candidate.status == 0 && candidate.fitsIn() ? dkgreen : dkred));

		return sb;
	}

	/**
	 * Styled fits-in string
	 *
	 * @param candidate candidate storage
	 * @return styled string
	 */
	static private CharSequence styledFitsIn(final CandidateStorage candidate)
	{
		final SpannableStringBuilder sb = new SpannableStringBuilder();
		final boolean fitsIn = candidate.fitsIn();
		append(sb, fitsIn ? "Fits in" : "Does not fit in", new ForegroundColorSpan(fitsIn ? dkgreen : dkred));
		return sb;
	}

	/**
	 * Styled status string
	 *
	 * @param candidate candidate storage
	 * @return styled string
	 */
	static private CharSequence styledStatus(final CandidateStorage candidate)
	{
		final SpannableStringBuilder sb = new SpannableStringBuilder();
		final CharSequence status = candidate.status();
		final boolean isOk = "Ok".equals(status.toString());
		append(sb, status, new ForegroundColorSpan(isOk ? dkgreen : dkred));
		return sb;
	}

	/**
	 * Dir type to res id
	 *
	 * @param type dir type
	 * @return res id
	 */
	static private int toResId(final DirType type)
	{
		int resId = 0;
		switch (type)
		{
			case APP_INTERNAL_POSSIBLY_ADOPTED:
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

	/**
	 * Append text
	 *
	 * @param sb    spannable string builder
	 * @param text  text
	 * @param spans spans to apply
	 */
	static private void append(final SpannableStringBuilder sb, final CharSequence text, final Object... spans)
	{
		if (text == null || text.length() == 0)
		{
			return;
		}

		final int from = sb.length();
		sb.append(text);
		final int to = sb.length();

		for (final Object span : spans)
		{
			sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	/**
	 * Append Image
	 *
	 * @param context context
	 * @param sb      spannable string builder
	 * @param resId   resource id
	 */
	static private void appendImage(final Context context, final SpannableStringBuilder sb, final int resId)
	{
		append(sb, "\u0000", makeImageSpan(context, resId));
	}

	/**
	 * Make image span
	 *
	 * @param context context
	 * @param resId   res id
	 * @return image span
	 */
	@SuppressWarnings("deprecation")
	static private Object makeImageSpan(final Context context, final int resId)
	{
		Drawable drawable;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
		{
			drawable = context.getResources().getDrawable(resId);
		}
		else
		{
			drawable = context.getResources().getDrawable(resId, null);
		}
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		return new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM);
	}

	/**
	 * Get candidate names and values
	 *
	 * @param context context
	 * @return pair of names and values
	 */
	static public Pair<CharSequence[], CharSequence[]> getCandidateNamesValues(final Context context)
	{
		final List<CharSequence> names = new ArrayList<>();
		final List<CharSequence> values = new ArrayList<>();
		final List<CandidateStorage> candidates = StorageUtils.getSortedCandidateStorages(context);
		for (CandidateStorage candidate : candidates)
		{
			if (candidate.status != 0)
			{
				continue;
			}
			names.add(toStyledString(context, candidate));
			if (candidate.dir.type == DirType.APP_INTERNAL_POSSIBLY_ADOPTED)
			{
				values.add("auto");
			}
			else
			{
				values.add(candidate.dir.file.getAbsolutePath());
			}
		}
		return new Pair<>(names.toArray(new CharSequence[0]), values.toArray(new CharSequence[0]));
	}

	// R E P O R T S

	/**
	 * Report on candidate storage
	 *
	 * @param context context
	 * @return report
	 */
	static public CharSequence reportStyledCandidateStorage(final Context context)
	{
		@SuppressWarnings("TypeMayBeWeakened") final SpannableStringBuilder sb = new SpannableStringBuilder();
		final List<CandidateStorage> candidates = StorageUtils.getSortedCandidateStorages(context);
		for (CandidateStorage candidate : candidates)
		{
			sb.append(toStyledString(context, candidate));
			sb.append(' ');
			sb.append(styledFitsIn(candidate));
			sb.append(' ');
			sb.append('|');
			sb.append(' ');
			sb.append(styledStatus(candidate));
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
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	static public CharSequence reportExternalStorage(final Context context)
	{
		final Map<StorageType, File[]> storages = StorageUtils.getStorageDirectories();
		final File[] physical = storages.get(StorageType.PRIMARY_PHYSICAL);
		final File[] emulated = storages.get(StorageType.PRIMARY_EMULATED);
		final File[] secondary = storages.get(StorageType.SECONDARY);

		final SpannableStringBuilder sb = new SpannableStringBuilder();
		if (physical != null)
		{
			appendImage(context, sb, R.drawable.ic_storage_intern);
			sb.append(' ');
			append(sb, " primary physical ", new RelativeSizeSpan(1.5F), new BackgroundColorSpan(pink), new ForegroundColorSpan(Color.BLACK));
			sb.append('\n');
			for (File f : physical)
			{
				final String s = f.getAbsolutePath();
				append(sb, s, new StyleSpan(Typeface.ITALIC), new ForegroundColorSpan(Color.GRAY));
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
		if (emulated != null)
		{
			appendImage(context, sb, R.drawable.ic_storage_extern_primary);
			sb.append(' ');
			append(sb, " primary emulated ", new RelativeSizeSpan(1.5F), new BackgroundColorSpan(pink), new ForegroundColorSpan(Color.BLACK));
			sb.append('\n');
			for (File f : emulated)
			{
				final String s = f.getAbsolutePath();
				append(sb, s, new StyleSpan(Typeface.ITALIC), new ForegroundColorSpan(Color.GRAY));
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
			appendImage(context, sb, R.drawable.ic_storage_extern_secondary);
			sb.append(' ');
			sb.append(' ');
			append(sb, " secondary ", new RelativeSizeSpan(1.5F), new BackgroundColorSpan(pink), new ForegroundColorSpan(Color.BLACK));
			sb.append('\n');
			for (File f : secondary)
			{
				final String s = f.getAbsolutePath();
				append(sb, s, new StyleSpan(Typeface.ITALIC), new ForegroundColorSpan(Color.GRAY));
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
}
