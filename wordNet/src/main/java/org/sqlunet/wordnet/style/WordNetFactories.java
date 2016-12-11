package org.sqlunet.wordnet.style;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.sqlunet.style.Factories;
import org.sqlunet.style.Spanner.SpanFactory;

/**
 * WordNet span factories
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WordNetFactories
{
	public static final SpanFactory lemmaFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new ForegroundColorSpan(Color.BLACK), new StyleSpan(Typeface.BOLD)};
		}
	};

	public static final SpanFactory membersFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new ForegroundColorSpan(Color.DKGRAY), new StyleSpan(Typeface.BOLD)};
		}
	};

	public static final SpanFactory wordFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new ForegroundColorSpan(Color.BLACK), new StyleSpan(Typeface.BOLD)};
		}
	};

	public static final SpanFactory definitionFactory = Factories.definitionFactory;

	public static final SpanFactory sampleFactory = Factories.exampleFactory;

	public static final SpanFactory dataFactory = Factories.dataFactory;
}
