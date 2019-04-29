package org.sqlunet.style;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.sqlunet.xnet.R;

/**
 * Spanner
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Spanner
{
	static private final String TAG = "Spanner";
	/**
	 * Collapsed marker
	 */
	static private final char COLLAPSEDCHAR = '@';

	/**
	 * Collapsed marker
	 */
	static private final String COLLAPSEDSTRING = Character.toString(COLLAPSEDCHAR);

	/**
	 * Expanded marker
	 */
	static private final String EXPANDEDSTRING = "~";
	/**
	 * End of expanded string marker
	 */
	static private final char EOEXPANDEDSTRING = '~';

	// I N T E R F A C E S

	/**
	 * Span factory
	 *
	 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
	 */
	public interface SpanFactory
	{
		@Nullable
		Object makeSpans(@SuppressWarnings("UnusedParameters") final long flags);
	}

	/**
	 * Click image interface
	 *
	 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
	 */
	@SuppressWarnings("unused")
	public interface OnClickImage
	{
		void onClickImage(final SpannableStringBuilder sb, final int position, final boolean collapsed);
	}

	// H I D D E N S P A N

	/**
	 * Hidden span
	 *
	 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
	 */
	static public class HiddenSpan extends ReplacementSpan
	{
		/**
		 * Constructor
		 */
		public HiddenSpan()
		{
		}

		@Override
		public void draw(@NonNull final Canvas canvas, final CharSequence arg1, final int arg2, final int arg3, final float arg4, final int arg5, final int arg6, final int arg7, @NonNull final Paint arg8)
		{
			//
		}

		@Override
		public int getSize(@NonNull final Paint paint, final CharSequence text, final int from, final int to, final FontMetricsInt fm)
		{
			return 0;
		}
	}

	/**
	 * Hidden span factory
	 *
	 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
	 */
	@SuppressWarnings("unused")
	static public class HiddenSpanFactory implements SpanFactory
	{
		@NonNull
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new HiddenSpan()};
		}
	}

	// A P P L Y S P A N

	/**
	 * Apply spans
	 *
	 * @param sb    spannable string builder
	 * @param from  start
	 * @param to    finish
	 * @param spans spans to apply
	 */
	static void setSpan(@NonNull @SuppressWarnings("TypeMayBeWeakened") final SpannableStringBuilder sb, final int from, final int to, @Nullable final Object spans)
	{
		if (spans != null && from != to)
		{
			if (spans instanceof Object[])
			{
				for (final Object span : (Object[]) spans)
				{
					if (span != null)
					{
						sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}
			else
			{
				sb.setSpan(spans, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

	/**
	 * Apply spans
	 *
	 * @param sb        spannable string builder
	 * @param from      start
	 * @param to        finish
	 * @param flags     flags
	 * @param factories span factories to call to get spans
	 */
	static public void setSpan(@NonNull final SpannableStringBuilder sb, final int from, final int to, @SuppressWarnings("SameParameterValue") final long flags, @NonNull final SpanFactory... factories)
	{
		for (final SpanFactory spanFactory : factories)
		{
			final Object spans = spanFactory.makeSpans(flags);
			Spanner.setSpan(sb, from, to, spans);
		}
	}

	// I M A G E

	/**
	 * Append spans
	 *
	 * @param sb    spannable string builder
	 * @param spans image span with possible image style span
	 */
	static private void appendImageSpans(@NonNull final SpannableStringBuilder sb, @NonNull final Object... spans)
	{
		final int from = sb.length();
		sb.append(COLLAPSEDCHAR);
		final int to = sb.length();
		for (Object span : spans)
		{
			sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	/**
	 * Append image
	 *
	 * @param sb       spannable string builder
	 * @param drawable drawable to use
	 */
	static public void appendImage(@NonNull final SpannableStringBuilder sb, @NonNull final Drawable drawable)
	{
		final Object span = new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE);
		Spanner.appendImageSpans(sb, span);
	}

	// C L I C K A B L E

	/**
	 * Append clickable image
	 *
	 * @param sb       spannable string builder
	 * @param caption  caption
	 * @param listener click listener
	 * @param context  context
	 */
	@SuppressWarnings("unused")
	static public void appendClickableImage(@NonNull final SpannableStringBuilder sb, @NonNull final CharSequence caption, @NonNull final OnClickImage listener, @NonNull final Context context)
	{
		final Drawable collapsedDrawable = getDrawable(context, R.drawable.ic_collapsed);
		final Drawable expandedDrawable = getDrawable(context, R.drawable.ic_expanded);
		appendClickableImage(sb, collapsedDrawable, expandedDrawable, caption, listener);
	}

	/**
	 * Append clickable image
	 *
	 * @param sb                spannable string builder
	 * @param collapsedDrawable collapse drawable
	 * @param expandedDrawable  expandContainer drawable
	 * @param caption           caption
	 * @param listener          click listener
	 */
	static private void appendClickableImage(@NonNull final SpannableStringBuilder sb, @NonNull final Drawable collapsedDrawable, final Drawable expandedDrawable, @NonNull final CharSequence caption, @NonNull final OnClickImage listener)
	{
		final ImageSpan span = new ImageSpan(collapsedDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
		final ClickableSpan span2 = new ClickableSpan()
		{
			@Override
			synchronized public void onClick(@NonNull final View view)
			{
				// Log.d(TAG, "Click image");
				final TextView textView = (TextView) view;
				final SpannableStringBuilder sb1 = (SpannableStringBuilder) textView.getText();
				final int clickableStart = textView.getSelectionStart();
				final int clickableEnd = textView.getSelectionEnd();
				final Object[] spans = sb1.getSpans(clickableStart, clickableEnd, ImageSpan.class);
				for (final Object span3 : spans)
				{
					// get imagespan
					int from = sb1.getSpanStart(span3);
					int to = sb1.getSpanEnd(span3);

					// remove imagespan
					sb1.removeSpan(span3);

					// text
					final char c = sb1.charAt(from);
					final boolean collapsed = c == COLLAPSEDCHAR;
					sb1.replace(from, to, collapsed ? EXPANDEDSTRING : COLLAPSEDSTRING);

					// set new imagespan
					final Object newimagespan = new ImageSpan(collapsed ? expandedDrawable : collapsedDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
					sb1.setSpan(newimagespan, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

					// fire click
					Log.d(TAG, from + "->" + to);
					listener.onClickImage(sb1, to + caption.length() + 2, collapsed);
				}
				textView.setText(sb1);
			}
		};
		Spanner.appendImageSpans(sb, span, span2);
		sb.append(' ').append(caption).append('\n');
	}

	/**
	 * Insert tag
	 *
	 * @param sb       spannable string builder
	 * @param position insert position
	 * @param tag      tag
	 */
	@SuppressWarnings("unused")
	static public void insertTag(@NonNull final SpannableStringBuilder sb, final int position, @NonNull final CharSequence tag)
	{
		final String insert = tag.toString() + '\n' + Spanner.EOEXPANDEDSTRING;
		sb.insert(position, insert);
		int mark = position + tag.length() + 1;
		sb.setSpan(new Spanner.HiddenSpan(), mark, mark + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	/**
	 * Collapse
	 *
	 * @param sb       spannable string builder
	 * @param position position to start from (to end-of-expanded string)
	 */
	@SuppressWarnings("unused")
	static public void collapse(@NonNull final SpannableStringBuilder sb, final int position)
	{
		sb.delete(position, Spanner.find(sb, position, Spanner.EOEXPANDEDSTRING));
	}

	// T E X T

	/**
	 * Append text
	 *
	 * @param sb        spannable string builder
	 * @param text      text
	 * @param flags     flags
	 * @param factories span factories
	 */
	static public void append(@NonNull final SpannableStringBuilder sb, @Nullable final CharSequence text, @SuppressWarnings("SameParameterValue") final long flags, @Nullable final SpanFactory... factories)
	{
		if (text == null || text.length() == 0)
		{
			return;
		}

		final int from = sb.length();
		sb.append(text);
		final int to = sb.length();
		if (factories != null)
		{
			for (final SpanFactory spanFactory : factories)
			{
				final Object spans = spanFactory.makeSpans(flags);
				if (spans instanceof Object[])
				{
					for (final Object span : (Object[]) spans)
					{
						sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
				else
				{
					sb.setSpan(spans, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
	}

	// H E L P E R S

	/**
	 * Find delimiter
	 *
	 * @param sb        spannable string builder
	 * @param start     search start
	 * @param delimiter delimiter
	 * @return delimiter position or -1 if not found
	 */
	static private int find(@NonNull @SuppressWarnings("TypeMayBeWeakened") final SpannableStringBuilder sb, final int start, @SuppressWarnings("SameParameterValue") final char delimiter)
	{
		int i = start;
		while (i < sb.length())
		{
			if (sb.charAt(i) == delimiter)
			{
				return i + 1;
			}
			i++;
		}
		return -1;
	}

	/**
	 * Get drawable from resource id
	 *
	 * @param context context
	 * @param resId   resource id
	 * @return drawable
	 */
	static public Drawable getDrawable(@NonNull final Context context, final int resId)
	{
		@SuppressWarnings("deprecation") final Drawable drawable = context.getResources().getDrawable(resId);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		return drawable;
	}

// --Commented out by Inspection START (8/20/17 10:09 AM):
//	/**
//	 * Dump spannable string builder
//	 *
//	 * @param sb spannable string builder
//	 * @return string
//	 */
//	static public String dump(final SpannableStringBuilder sb)
//	{
//		final StringBuilder dump = new StringBuilder();
//		for (Object span : sb.getSpans(0, sb.length(), Object.class))
//		{
//			final int from = sb.getSpanStart(span);
//			final int to = sb.getSpanEnd(span);
//			final CharSequence sub = sb.subSequence(from, to);
//			final Class<?> clazz = span.getClass();
//			dump.append(sub).append(' ').append('[').append(from).append(',').append(to).append(']').append(' ').append(clazz).append(' ');
//			if (clazz.equals(BackgroundColorSpan.class))
//			{
//				final BackgroundColorSpan bSpan = (BackgroundColorSpan) span;
//				int color = bSpan.getBackgroundColor();
//				dump.append(Integer.toHexString(color));
//			}
//			else if (clazz.equals(ForegroundColorSpan.class))
//			{
//				final ForegroundColorSpan fSpan = (ForegroundColorSpan) span;
//				int color = fSpan.getForegroundColor();
//				dump.append(Integer.toHexString(color));
//			}
//			else if (clazz.equals(ImageSpan.class))
//			{
//				final ImageSpan iSpan = (ImageSpan) span;
//				dump.append(iSpan.getDrawable()).append(' ').append(iSpan.getSource());
//			}
//			else if (clazz.equals(HiddenSpan.class))
//			{
//				final HiddenSpan hSpan = (HiddenSpan) span;
//				dump.append(hSpan.toString());
//			}
//			dump.append('\n');
//		}
//		return dump.toString();
//	}
// --Commented out by Inspection STOP (8/20/17 10:09 AM)
}
