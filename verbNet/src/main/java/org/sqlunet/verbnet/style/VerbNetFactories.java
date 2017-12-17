package org.sqlunet.verbnet.style;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.sqlunet.style.Colors;
import org.sqlunet.style.Factories;
import org.sqlunet.style.Spanner.SpanFactory;

/**
 * VerbNet span factories
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VerbNetFactories
{
	// class

	static public final SpanFactory classFactory = Factories.classFactory;

	// member

	static public final SpanFactory memberFactory = Factories.memberFactory;

	// definition

	static public final SpanFactory definitionFactory = Factories.definitionFactory;

	// groupings

	static public final SpanFactory groupingFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new ForegroundColorSpan(Color.LTGRAY);
		}
	};

	// role

	static public final SpanFactory roleFactory = Factories.roleFactory;

	static final SpanFactory themroleFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new BackgroundColorSpan(Colors.lt_magenta);
		}
	};

	// frame

	static public final SpanFactory frameFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Colors.brown), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};
		}
	};

	static public final SpanFactory framesubnameFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Color.LTGRAY), new ForegroundColorSpan(Color.WHITE)};
		}
	};

	static final SpanFactory catFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Colors.lt_brown), new ForegroundColorSpan(Color.DKGRAY)};
		}
	};

	static final SpanFactory catValueFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Colors.lt_magenta)};
		}
	};

	// semantics

	static final SpanFactory predicateFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Colors.orange), new ForegroundColorSpan(Color.WHITE)};
		}
	};

	@Nullable
	static final SpanFactory argsFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return null;
		}
	};

	static final SpanFactory constantFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Color.LTGRAY)};
		}
	};

	static final SpanFactory eventFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Color.BLUE), new ForegroundColorSpan(Color.WHITE)};
		}
	};

	// restrs

	static public final SpanFactory restrsFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new BackgroundColorSpan(Colors.lt_brown);
		}
	};

	// example

	static public final SpanFactory exampleFactory = Factories.exampleFactory;
}
