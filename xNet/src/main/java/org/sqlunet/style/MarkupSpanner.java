package org.sqlunet.style;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.SpannableStringBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Spanner as per mark-up tags
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class MarkupSpanner extends Spanner
{
	/**
	 * Span factory
	 *
	 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
	 */
	@FunctionalInterface
	public interface SpanFactory
	{
		@Nullable
		Object makeSpans(final String selector, final long flags);
	}

	/**
	 * Span position
	 *
	 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
	 */
	public enum SpanPosition
	{
		TAG1, TEXT, TAG2;

		/**
		 * Make flags
		 *
		 * @return flags
		 */
		int flags()
		{
			switch (this)
			{
				case TAG1:
					return 1;
				case TAG2:
					return 2;
				case TEXT:
					return 3;

			}
			return 0;
		}

		/**
		 * Make position from flags
		 *
		 * @param flags flags
		 * @return position
		 */
		static public SpanPosition valueOf(final long flags)
		{
			switch ((int) (flags & 3))
			{
				case 1:
					return TAG1;
				case 2:
					return TAG2;
				case 3:
					return TEXT;
				default:
					break;
			}
			return null;
		}
	}

	/**
	 * Apply spans
	 *
	 * @param text          input text
	 * @param sb            spannable string builder
	 * @param spanFactory   span factory
	 * @param flags         flags
	 * @param pattern       pattern
	 * @param extraPatterns more patterns
	 * @return spannable string builder
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	static public CharSequence setSpan(@NonNull final CharSequence text, //
			@NonNull final SpannableStringBuilder sb, @NonNull final SpanFactory spanFactory, //
			final long flags, //
			@NonNull @SuppressWarnings("SameParameterValue") final Pattern pattern, //
			@NonNull @SuppressWarnings("SameParameterValue") final Pattern... extraPatterns)
	{
		// specific patterns
		for (final Pattern xpattern : extraPatterns)
		{
			final Matcher xmatcher = xpattern.matcher(text);
			while (xmatcher.find())
			{
				final int n = xmatcher.groupCount();

				if (n == 3)
				{
					final String headTag = xmatcher.group(1);
					final String tailTag = xmatcher.group(3);

					// start
					final Object startSpans = spanFactory.makeSpans(headTag, flags | SpanPosition.TAG1.flags());
					final int tag1Start = xmatcher.start(1) - 1;
					final int tag1End = xmatcher.end(1) + 1;
					Spanner.setSpan(sb, tag1Start, tag1End, startSpans);

					// middle
					final Object textSpans = spanFactory.makeSpans(tailTag, flags | SpanPosition.TEXT.flags());
					final int textStart = xmatcher.start(2);
					final int textEnd = xmatcher.end(2);
					Spanner.setSpan(sb, textStart, textEnd, textSpans);

					// end
					final Object endSpans = spanFactory.makeSpans(tailTag, flags | SpanPosition.TAG2.flags());
					final int tag2Start = xmatcher.start(3) - 2;
					final int tag2End = xmatcher.end(3) + 1;
					Spanner.setSpan(sb, tag2Start, tag2End, endSpans);

					sb.replace(xmatcher.start(1), xmatcher.start(1), "");
				}
			}
		}

		// general pattern
		final Matcher matcher = pattern.matcher(text);
		while (matcher.find())
		{
			final int n = matcher.groupCount();

			if (n == 3)
			{
				final String headTag = matcher.group(1);
				final String tailTag = matcher.group(3);

				final Object startSpans = spanFactory.makeSpans(headTag, flags | SpanPosition.TAG1.flags());
				final int tag1Start = matcher.start(1) - 1;
				final int tag1End = matcher.end(1) + 1;
				Spanner.setSpan(sb, tag1Start, tag1End, startSpans);

				final Object textSpans = spanFactory.makeSpans(tailTag, flags | SpanPosition.TEXT.flags());
				final int textStart = matcher.start(2);
				final int textEnd = matcher.end(2);
				Spanner.setSpan(sb, textStart, textEnd, textSpans);

				final Object endSpans = spanFactory.makeSpans(tailTag, flags | SpanPosition.TAG2.flags());
				final int tag2Start = matcher.start(3) - 2;
				final int tag2End = matcher.end(3) + 1;
				Spanner.setSpan(sb, tag2Start, tag2End, endSpans);
			}
		}
		return sb;
	}

	/**
	 * Apply spans
	 *
	 * @param selector  factory selector
	 * @param sb        spannable string builder
	 * @param i         from
	 * @param j         to
	 * @param flags     flags
	 * @param factories span factories
	 */
	@SuppressWarnings("unused")
	static private void setSpan(final String selector, @NonNull final SpannableStringBuilder sb, final int i, final int j, final long flags, @NonNull final SpanFactory... factories)
	{
		final Object[] spans = new Object[factories.length];
		for (int f = 0; f < factories.length; f++)
		{
			spans[f] = factories[f].makeSpans(selector, flags);
		}
		Spanner.setSpan(sb, i, j, spans);
	}
}
