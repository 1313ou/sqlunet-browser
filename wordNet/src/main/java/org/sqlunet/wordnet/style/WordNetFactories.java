package org.sqlunet.wordnet.style;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.sqlunet.style.Colors;
import org.sqlunet.style.Spanner.SpanFactory;

/**
 * WordNet span factories
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WordNetFactories
{
	public static final SpanFactory membersFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new ForegroundColorSpan(Color.DKGRAY), new StyleSpan(Typeface.BOLD)};
		}
	};

	public static final SpanFactory lemmaFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new ForegroundColorSpan(Color.BLACK), new StyleSpan(Typeface.BOLD)};
		}
	};

	public static final SpanFactory definitionFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new ForegroundColorSpan(Color.BLUE), new StyleSpan(Typeface.ITALIC)};
		}
	};

	public static final SpanFactory sampleFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new BackgroundColorSpan(Colors.ltyellow);
		}
	};
}
