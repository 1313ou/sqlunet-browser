/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.style;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

/**
 * Report helper
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Report
{
	// H E L P E R S

	/**
	 * Append text
	 *
	 * @param sb    spannable string builder
	 * @param text  text
	 * @param spans spans to apply
	 */
	@NonNull
	static public SpannableStringBuilder append(@NonNull final SpannableStringBuilder sb, @Nullable final CharSequence text, @NonNull final Object... spans)
	{
		if (text != null && text.length() > 0)
		{
			final int from = sb.length();
			sb.append(text);
			final int to = sb.length();

			for (final Object span : spans)
			{
				sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return sb;
	}

	/**
	 * Append header
	 *
	 * @param sb   spannable string builder
	 * @param text text
	 * @return spannable string builder
	 */
	@NonNull
	static public SpannableStringBuilder appendHeader(@NonNull SpannableStringBuilder sb, CharSequence text)
	{
		return append(sb, text, new StyleSpan(Typeface.BOLD));
	}

	/**
	 * Append Image
	 *
	 * @param context context
	 * @param sb      spannable string builder
	 * @param resId   resource id
	 */
	static public void appendImage(@NonNull final Context context, @NonNull final SpannableStringBuilder sb, @DrawableRes final int resId)
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
	@NonNull
	static private Object makeImageSpan(@NonNull final Context context, @DrawableRes final int resId)
	{
		final Resources res = context.getResources();
		final Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), resId, context.getTheme());
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		return new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM);
	}
}
