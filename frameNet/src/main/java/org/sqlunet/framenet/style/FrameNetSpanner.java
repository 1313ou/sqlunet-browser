package org.sqlunet.framenet.style;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import org.sqlunet.style.MarkupSpanner;

import java.util.regex.Pattern;

/**
 * FrameNet spanner
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetSpanner
{
	private static final Pattern pattern = Pattern.compile("<([^>]*)>([^<]*)</([^>]*)>"); //$NON-NLS-1$

	private static final Pattern pattern1 = Pattern.compile("<(ex)>(.*)</(ex)>"); //$NON-NLS-1$

	/**
	 * Span factory
	 */
	private final FrameNetMarkupFactory factory;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public FrameNetSpanner(final Context context)
	{
		this.factory = new FrameNetMarkupFactory(context);
	}

	/**
	 * Process
	 *
	 * @param text  text to process
	 * @param flags flags
	 * @return processed text
	 */
	public CharSequence process(final CharSequence text, final long flags)
	{
		final SpannableStringBuilder sb = new SpannableStringBuilder(text);
		MarkupSpanner.setSpan(text, sb, this.factory, flags, FrameNetSpanner.pattern, FrameNetSpanner.pattern1);
		return sb;
	}

	/**
	 * Add span
	 *
	 * @param sb       spannable string builder
	 * @param start    start
	 * @param end      end
	 * @param selector selector guide
	 * @param flags    flags
	 */
	@SuppressWarnings("unused")
	public void addSpan(@SuppressWarnings("TypeMayBeWeakened") final SpannableStringBuilder sb, final int start, final int end, final String selector, final long flags)
	{
		final Object spans = this.factory.makeSpans(selector, flags);
		if (spans != null)
		{
			if (spans instanceof Object[])
			{
				for (final Object span : (Object[]) spans)
				{
					sb.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
			else
			{
				sb.setSpan(spans, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}
}
