/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.framenet.style;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import org.sqlunet.style.MarkupSpanner;
import org.sqlunet.style.Spanner;

import java.util.Collection;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * FrameNet spanner
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetSpanner
{
	static private final Pattern pattern = Pattern.compile("<([^>]*)>([^<]*)</([^>]*)>");
	static private final Pattern pattern1 = Pattern.compile("<(ex)>(.*)</(ex)>");
	/**
	 * Span factory
	 */
	@NonNull
	private final FrameNetMarkupFactory factory;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public FrameNetSpanner(@NonNull final Context context)
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
	@NonNull
	public CharSequence process(@NonNull final CharSequence text, final long flags, @Nullable final Spanner.SpanFactory factory)
	{
		final SpannableStringBuilder sb = new SpannableStringBuilder(text);
		if (factory != null)
		{
			Spanner.setSpan(sb, 0, sb.length(), 0, factory);
		}
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
	@SuppressWarnings("unchecked")
	public void addSpan(@NonNull final SpannableStringBuilder sb, final int start, final int end, @NonNull final String selector, final long flags)
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
			else if (spans instanceof Collection)
			{
				for (Object span2 : (Collection<Object>) spans)
				{
					sb.setSpan(span2, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
			else
			{
				sb.setSpan(spans, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}
}
