package org.sqlunet.verbnet.style;

import org.sqlunet.style.Colors;
import org.sqlunet.style.Spanner.SpanFactory;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class VerbNetFactories
{
	// colors

	// class

	static public final SpanFactory classFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Colors.dkred), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD) };
		}
	};

	// item

	static public final SpanFactory itemFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Color.RED), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD) };
		}
	};

	// role

	public static final SpanFactory roleFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Color.MAGENTA), new ForegroundColorSpan(Color.WHITE) };
		}
	};

	static final SpanFactory themroleFactory = new SpanFactory()
	{
		@SuppressWarnings("synthetic-access")
		@Override
		public Object makeSpans(final long flags)
		{
			return new BackgroundColorSpan(Colors.ltmagenta);
		}
	};

	// frame

	public static final SpanFactory frameFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Colors.brown), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD) };
		}
	};

	public static final SpanFactory framesubnameFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Color.LTGRAY), new ForegroundColorSpan(Color.WHITE) };
		}
	};

	static final SpanFactory catFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Colors.ltbrown), new ForegroundColorSpan(Color.DKGRAY) };
		}
	};

	static final SpanFactory catValueFactory = new SpanFactory()
	{
		@SuppressWarnings("synthetic-access")
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Colors.ltmagenta) };
		}
	};

	// semantics

	static final SpanFactory predicateFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Colors.orange), new ForegroundColorSpan(Color.WHITE) };
		}
	};

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
			return new Object[] { new BackgroundColorSpan(Color.LTGRAY) };
		}
	};

	static final SpanFactory eventFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[] { new BackgroundColorSpan(Color.BLUE), new ForegroundColorSpan(Color.WHITE) };
		}
	};

	// restrs

	public static final SpanFactory restrsFactory = new SpanFactory()
	{
		@SuppressWarnings("synthetic-access")
		@Override
		public Object makeSpans(final long flags)
		{
			return new BackgroundColorSpan(Colors.ltyellow);
		}
	};

	// example

	public static final SpanFactory exampleFactory = new SpanFactory()
	{
		@SuppressWarnings("synthetic-access")
		@Override
		public Object makeSpans(final long flags)
		{
			return new BackgroundColorSpan(Colors.ltyellow);
		}
	};
}
