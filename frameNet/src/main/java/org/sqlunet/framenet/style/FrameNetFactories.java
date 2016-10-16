package org.sqlunet.framenet.style;

import org.sqlunet.style.Colors;
import org.sqlunet.style.Spanner.SpanFactory;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class FrameNetFactories
{
	static public final SpanFactory frameFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Colors.dkred), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD) };
		}
	};

	static public final SpanFactory lexunitFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Color.RED), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD) };
		}
	};

	static public final SpanFactory feFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Color.MAGENTA), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD) };
		}
	};

	static public final SpanFactory fe2Factory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Color.GRAY), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD) };
		}
	};

	static public final SpanFactory feAbbrevFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new ForegroundColorSpan(Color.MAGENTA) };
		}
	};

	static public final SpanFactory sentenceFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Colors.ltyellow), new StyleSpan(Typeface.ITALIC) };
		}
	};

	static public final SpanFactory layerTypeFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Colors.dkred), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD) };
		}
	};

	static public final SpanFactory subtextFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new ForegroundColorSpan(Color.GRAY), new StyleSpan(Typeface.ITALIC) };
		}
	};

	static public final SpanFactory highlightTextFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Colors.ltyellowhighlight), new ForegroundColorSpan(Color.BLACK), new StyleSpan(Typeface.NORMAL) };
		}
	};

	public static final SpanFactory definitionFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new ForegroundColorSpan(Color.BLUE), new StyleSpan(Typeface.ITALIC) };
		}
	};

	public static final SpanFactory metadefinitionFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new ForegroundColorSpan(Color.DKGRAY), new StyleSpan(Typeface.ITALIC) };
		}
	};

	static public final SpanFactory labelFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Colors.pink), new StyleSpan(Typeface.BOLD) };
		}
	};

	static public final SpanFactory targetFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Color.BLACK), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD) };
		}
	};

	static public final SpanFactory ptFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new ForegroundColorSpan(Color.BLACK), new StyleSpan(Typeface.BOLD) };
		}
	};

	static public final SpanFactory gfFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new ForegroundColorSpan(Color.GRAY) };
		}
	};

	static public final SpanFactory governorTypeFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new ForegroundColorSpan(Colors.dkred), new StyleSpan(Typeface.BOLD) };
		}
	};

	static public final SpanFactory governorFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new ForegroundColorSpan(Color.BLACK), new StyleSpan(Typeface.BOLD) };
		}
	};
}
