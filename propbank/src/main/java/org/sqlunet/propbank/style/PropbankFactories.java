package org.sqlunet.propbank.style;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.sqlunet.style.Colors;
import org.sqlunet.style.Spanner.SpanFactory;

public class PropbankFactories
{
	static public final SpanFactory roleSetFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Color.RED), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};
		}
	};

	static public final SpanFactory roleFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Color.MAGENTA), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};
		}
	};

	static public final SpanFactory thetaFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Color.BLUE), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};
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

	static public final SpanFactory exampleFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Colors.ltyellow), new StyleSpan(Typeface.ITALIC)};
		}
	};

	static public final SpanFactory textFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Colors.ltyellow), new StyleSpan(Typeface.ITALIC)};
		}
	};

	static public final SpanFactory relationFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Color.GRAY), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};
		}
	};

	// --Commented out by Inspection START (10/15/16 8:10 PM):
	//	static public SpanFactory traceFactory = new SpanFactory()
	//	{
	//		@Override
	//		public Object makeSpans(final long flags)
	//		{
	//			return new Object[] { new BackgroundColorSpan(Color.GRAY), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD) };
	//		}
	//	};
	// --Commented out by Inspection STOP (10/15/16 8:10 PM)
}