package org.sqlunet.predicatematrix.style;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.sqlunet.style.Colors;
import org.sqlunet.style.Factories;
import org.sqlunet.style.Spanner.SpanFactory;

public class PredicateMatrixFactories
{
	// name
	static public final SpanFactory nameFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Colors.lt_yellow), new ForegroundColorSpan(Color.BLACK), new StyleSpan(Typeface.BOLD)};
		}
	};

	// group
	static public final SpanFactory groupFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Colors.orange), new ForegroundColorSpan(Color.BLACK), new StyleSpan(Typeface.BOLD)};
		}
	};

	// word
	static public final SpanFactory wordFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new ForegroundColorSpan(Color.BLACK), new StyleSpan(Typeface.ITALIC)};
		}
	};

	// definition
	static public final SpanFactory definitionFactory = Factories.definitionFactory;

	// class
	static public final SpanFactory classFactory = Factories.classFactory;

	// role
	static public final SpanFactory roleFactory = Factories.roleFactory;

	// role alias
	static public final SpanFactory roleAliasFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Colors.blue), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};
		}
	};

	// data
	static public final SpanFactory dataFactory = Factories.dataFactory;
}
