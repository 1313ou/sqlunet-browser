package org.sqlunet.style;

import android.text.SpannableStringBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Spanner as per regexpr
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class RegExprSpanner extends Spanner
{
	/**
	 * Replacers
	 */
	final private SpanReplacer[] spanReplacers;

	/**
	 * Constructor
	 *
	 * @param regexprs  regexprs
	 * @param factories span factories
	 */
	public RegExprSpanner(final String[] regexprs, final SpanFactory[][] factories)
	{
		this.spanReplacers = new SpanReplacer[regexprs.length];
		for (int i = 0; i < regexprs.length; i++)
		{
			this.spanReplacers[i] = new SpanReplacer(regexprs[i], factories[i]);
		}
	}

	/**
	 * Append
	 *
	 * @param text  text to append
	 * @param sb    spannable string builder to append to
	 * @param flags flags
	 */
	public void append(final CharSequence text, final SpannableStringBuilder sb, @SuppressWarnings("SameParameterValue") final long flags)
	{
		final int from = sb.length();
		sb.append(text);
		setSpan(sb, from, flags);
	}

	/**
	 * Apply span
	 *
	 * @param sb    spannable string builder
	 * @param from  start
	 * @param flags flags
	 */
	public void setSpan(final SpannableStringBuilder sb, final int from, final long flags)
	{
		final CharSequence text = sb.subSequence(from, sb.length());
		for (final SpanReplacer spanReplacer : this.spanReplacers)
		{
			spanReplacer.setSpan(text, sb, from, flags);
		}
	}

	/**
	 * Replacer
	 *
	 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
	 */
	static class SpanReplacer
	{
		// static private final String TAG = "RegExpSpanner";

		/**
		 * Pattern
		 */
		final private Pattern pattern;

		/**
		 * Span factories to call whenever pattern is found
		 */
		final private SpanFactory[] spanFactories;

		/**
		 * Constructor
		 *
		 * @param regexpr    regexpr
		 * @param factories span factories
		 */
		SpanReplacer(final String regexpr, final SpanFactory... factories)
		{
			this.pattern = Pattern.compile(regexpr, Pattern.MULTILINE);
			this.spanFactories = factories;
		}

		/**
		 * Make spans
		 *
		 * @param input input string
		 * @param sb    spannable string builder
		 * @param from  start
		 * @param flags flags
		 */
		public void setSpan(final CharSequence input, final SpannableStringBuilder sb, final int from, final long flags)
		{
			if (input.length() == 0)
			{
				return;
			}

			final Matcher matcher = this.pattern.matcher(input);
			while (matcher.find())
			{
				final int n = matcher.groupCount();

				for (int i = 0; i < n; i++)
				{
					if (matcher.group(i + 1) == null)
					{
						return;
					}

					// Log.d(SpanReplacer.TAG, '"' + matcher.group(i + 1) + '"');
					final int start = from + matcher.start(i + 1);
					final int end = from + matcher.end(i + 1);

					// span
					final Object startSpans = this.spanFactories[i].makeSpans(flags);
					Spanner.setSpan(sb, start, end, startSpans);
				}
			}
		}

		@Override
		public String toString()
		{
			final StringBuilder sb = new StringBuilder();
			sb.append(this.pattern).append(" ->");
			for (SpanFactory factory : this.spanFactories)
			{
				sb.append(' ').append(factory);
			}
			return sb.toString();
		}
	}
}
