package org.sqlunet.bnc.style;

import org.sqlunet.style.Colors;
import org.sqlunet.style.Spanner.SpanFactory;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class BNCFactories
{
	static public final SpanFactory headerFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Colors.pink), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD) };
		}
	};
}
