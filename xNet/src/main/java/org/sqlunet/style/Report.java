package org.sqlunet.style;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;

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
	static public SpannableStringBuilder append(final SpannableStringBuilder sb, final CharSequence text, final Object... spans)
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
	@SuppressWarnings("TypeMayBeWeakened")
	static public SpannableStringBuilder appendHeader(SpannableStringBuilder sb, CharSequence text)
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
	static public void appendImage(final Context context, final SpannableStringBuilder sb, final int resId)
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
		final Resources res = context.getResources();
		Drawable drawable;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
		{
			drawable = res.getDrawable(resId);
		}
		else
		{
			drawable = res.getDrawable(resId, null);
		}
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		return new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM);
	}
}
