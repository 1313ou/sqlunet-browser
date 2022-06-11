package org.sqlunet.speak;

import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SpeakButton
{
	//private static final String TAG = "SpeakButton";

	/**
	 * Collapsed marker
	 */
	static private final char COLLAPSED_CHAR = '@';


	// I M A G E

	/**
	 * Append spans
	 *
	 * @param sb         spannable string builder
	 * @param imageSpan  image span
	 * @param clickSpan  click span
	 * @param extraSpans possible image style span
	 */
	static private void appendImageSpans(@NonNull final SpannableStringBuilder sb, @Nullable final CharSequence caption, @NonNull final Object imageSpan, @NonNull final Object clickSpan, @NonNull final Object... extraSpans)
	{
		final int from = sb.length();
		sb.append(COLLAPSED_CHAR);
		int to = sb.length();
		sb.setSpan(imageSpan, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		if (caption != null && caption.length() > 0)
		{
			sb.append(' ');
			sb.append(caption);
		}
		to = sb.length();
		sb.setSpan(clickSpan, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		for (Object span : extraSpans)
		{
			sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	// C L I C K A B L E

	/**
	 * Append clickable image
	 *
	 * @param sb          spannable string builder
	 * @param drawableRes drawable res
	 * @param caption     caption
	 * @param listener    click listener
	 */
	static public void appendClickableImage(@NonNull final SpannableStringBuilder sb, @DrawableRes int drawableRes, @NonNull final CharSequence caption, @NonNull final Runnable listener, @NonNull final Context context)
	{
		final ImageSpan span;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
		{
			span = new ImageSpan(context, drawableRes, DynamicDrawableSpan.ALIGN_BOTTOM);
		}
		else
		{
			span = new ImageSpan(context, drawableRes);
		}
		final ClickableSpan span2 = new ClickableSpan()
		{
			@Override
			synchronized public void onClick(@NonNull final View view)
			{
				// Log.d(TAG, "Click");
				listener.run();
			}
		};
		appendImageSpans(sb, caption, span, span2, new ForegroundColorSpan(fetchColor(context, R.attr.colorHighlight2OnBackground)));
	}

	@ColorInt
	static private int fetchColor(@NonNull final Context context, @AttrRes int attr)
	{
		final TypedValue typedValue = new TypedValue();
		final Resources.Theme theme = context.getTheme();
		theme.resolveAttribute(attr, typedValue, true);
		return typedValue.data;
	}
}
