package org.sqlunet.framenet.style;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.sqlunet.style.Colors;
import org.sqlunet.style.Factories;
import org.sqlunet.style.Spanner.SpanFactory;

/**
 * FrameNet span factories
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetFactories
{
	static public final SpanFactory boldFactory = Factories.boldFactory;

	static public final SpanFactory dataFactory = Factories.dataFactory;

	// frame
	static public final SpanFactory frameFactory = Factories.classFactory;

	static public final SpanFactory metaFrameDefinitionFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{ new ForegroundColorSpan(Colors.dk_red), new StyleSpan(Typeface.ITALIC)};
		}
	};

	// lex unit
	static public final SpanFactory lexunitFactory = Factories.memberFactory;

	static public final SpanFactory definitionFactory = Factories.definitionFactory;

	// fe
	static public final SpanFactory feFactory = Factories.roleFactory;

	static public final SpanFactory fe2Factory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Color.GRAY), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};
		}
	};

	static public final SpanFactory feAbbrevFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new ForegroundColorSpan(Color.MAGENTA)};
		}
	};

	static public final SpanFactory metaFeDefinitionFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{ new ForegroundColorSpan(Colors.lt_blue), new StyleSpan(Typeface.ITALIC)};
		}
	};

	// sentence
	static public final SpanFactory sentenceFactory = Factories.exampleFactory;

	// annotations
	static public final SpanFactory annoSetFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new StyleSpan(Typeface.BOLD)};
		}
	};

	static public final SpanFactory layerTypeFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Colors.yellow), new StyleSpan(Typeface.BOLD)};
		}
	};

	static public final SpanFactory labelFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Colors.pink), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};
		}
	};

	static public final SpanFactory subtextFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new ForegroundColorSpan(Color.GRAY), new StyleSpan(Typeface.ITALIC)};
		}
	};

	static public final SpanFactory targetFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Color.RED), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};
		}
	};

	static public final SpanFactory highlightTextFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Colors.yellow), new ForegroundColorSpan(Color.BLACK), new StyleSpan(Typeface.NORMAL)};
		}
	};

	static public final SpanFactory targetHighlightTextFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Color.BLACK), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};
		}
	};

	static public final SpanFactory ptFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new ForegroundColorSpan(Color.BLACK), new StyleSpan(Typeface.BOLD)};
		}
	};

	static public final SpanFactory gfFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new ForegroundColorSpan(Color.GRAY)};
		}
	};

	static public final SpanFactory governorTypeFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new ForegroundColorSpan(Colors.dk_red), new StyleSpan(Typeface.BOLD)};
		}
	};

	static public final SpanFactory governorFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new ForegroundColorSpan(Color.BLACK), new StyleSpan(Typeface.BOLD)};
		}
	};
}
