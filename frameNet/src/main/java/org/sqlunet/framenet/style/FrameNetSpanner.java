package org.sqlunet.framenet.style;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import org.sqlunet.style.MarkupSpanner;

import java.util.regex.Pattern;

public class FrameNetSpanner
{
	// spanner

	private static final Pattern pattern = Pattern.compile("<([^>]*)>([^<]*)</([^>]*)>"); //$NON-NLS-1$

	private static final Pattern pattern1 = Pattern.compile("<(ex)>(.*)</(ex)>"); //$NON-NLS-1$

	// factory

	private FrameNetMarkupFactory factory = null;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public FrameNetSpanner(final Context context)
	{
		this.factory = new FrameNetMarkupFactory(context);
	}

	public CharSequence process(final CharSequence text, final long flags)
	{
		final SpannableStringBuilder sb = new SpannableStringBuilder(text);
		MarkupSpanner.setSpan(text, sb, this.factory, flags, FrameNetSpanner.pattern, FrameNetSpanner.pattern1);
		return sb;
	}

	@SuppressWarnings("unused")
	public void addSpan(final SpannableStringBuilder builder, final int start, final int end, final String selector, final long flags)
	{
		final Object spans = this.factory.makeSpans(selector, flags);
		if (spans != null)
		{
			if (spans instanceof Object[])
			{
				for (final Object span : (Object[]) spans)
				{
					builder.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
			else
			{
				builder.setSpan(spans, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}
}
