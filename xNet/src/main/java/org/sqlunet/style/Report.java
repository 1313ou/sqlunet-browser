/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.style;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
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
	 * Append header
	 *
	 * @param sb   spannable string builder
	 * @param text text
	 * @return spannable string builder
	 */
	@NonNull
	static public SpannableStringBuilder appendHeader(@NonNull SpannableStringBuilder sb, CharSequence text)
	{
		return Spanner.appendWithSpans(sb, text, new StyleSpan(Typeface.BOLD));
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
		Spanner.appendWithSpans(sb, "\u0000", makeImageSpan(context, resId));
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
		final Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), resId, context.getTheme());
		assert drawable != null;
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		return new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM);
	}
}
