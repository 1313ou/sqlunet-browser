package org.sqlunet.framenet.style;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.BackgroundColorSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import org.sqlunet.framenet.R;
import org.sqlunet.style.Colors;
import org.sqlunet.style.MarkupSpanner;
import org.sqlunet.style.MarkupSpanner.SpanPosition;
import org.sqlunet.style.Spanner;
import org.sqlunet.style.Spanner.HiddenSpan;

/**
 * FrameNet markup factory
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetMarkupFactory implements MarkupSpanner.SpanFactory
{
	// static private final String TAG = "FrameNetMarkupFactory";

	/**
	 * Flag value : fe definition
	 */
	static public final int FEDEF = 0x10000;

	/**
	 * Text factory
	 */
	static private final MarkupSpanner.SpanFactory textFactory = new MarkupSpanner.SpanFactory()
	{
		@Override
		public Object makeSpans(final String selector, final long flags)
		{
			if ("fe".equals(selector))
			{
				return new Object[]{new BackgroundColorSpan(Color.MAGENTA), new ForegroundColorSpan(Color.WHITE)};
			}
			if ("t".equals(selector)) // target
			{
				return new Object[]{new BackgroundColorSpan(Color.GRAY), new ForegroundColorSpan(Color.WHITE)};
			}
			if ("fen".equals(selector))
			{
				if ((flags & FrameNetMarkupFactory.FEDEF) != 0)
				{
					return new ForegroundColorSpan(Color.MAGENTA);
				}
				else
				{
					return new Object[]{new BackgroundColorSpan(Color.MAGENTA), new ForegroundColorSpan(Color.WHITE)};
				}
			}
			if (selector.matches("fex.*"))
			{
				if ((flags & FrameNetMarkupFactory.FEDEF) == 0)
				{
					return new Object[]{new ForegroundColorSpan(Color.GRAY), new UnderlineSpan()};
				}
				else
				{
					return new Object[]{new BackgroundColorSpan(Colors.lt_magenta), new ForegroundColorSpan(Color.WHITE)};
				}
			}
			if ("xfen".equals(selector))
			{
				return new ForegroundColorSpan(Color.MAGENTA);// new BackgroundColorSpan(Color.WHITE);
			}
			if ("ex".equals(selector))
			{
				return new Object[]{new ForegroundColorSpan(Color.GRAY), new StyleSpan(android.graphics.Typeface.ITALIC)};
			}
			if ("x".equals(selector))
			{
				return new Object[]{new ForegroundColorSpan(Color.BLACK), new StyleSpan(android.graphics.Typeface.BOLD)};
			}
			return null;
		}
	};

	/**
	 * Top role drawable
	 */
	private final Drawable role2Drawable;

	/**
	 * Role drawable
	 */
	private final Drawable roleDrawable;

	/**
	 * Relation drawable
	 */
	private final Drawable relationDrawable;

	/**
	 * Sample drawable
	 */
	private final Drawable sampleDrawable;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	FrameNetMarkupFactory(final Context context)
	{
		super();
		this.roleDrawable = Spanner.getDrawable(context, R.drawable.role);
		this.role2Drawable = Spanner.getDrawable(context, R.drawable.role1);
		this.relationDrawable = Spanner.getDrawable(context, R.drawable.relation);
		this.sampleDrawable = Spanner.getDrawable(context, R.drawable.sample);
	}

	/**
	 * Make spans
	 *
	 * @param selector selector guide
	 * @param flags    flags
	 * @return spans
	 */
	@Override
	public Object makeSpans(final String selector, final long flags)
	{
		// Log.d(FrameNetMarkupFactory.TAG, selector + ' ' + flags);
		final SpanPosition position = SpanPosition.valueOf(flags);
		if (position != null)
		{
			switch (position)
			{
				case TAG1:
					if ("t".equals(selector))
					{
						return new ImageSpan(this.relationDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
					}
					if ("fen".equals(selector))
					{
						return new ImageSpan(this.roleDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
					}
					if ("fe".equals(selector))
					{
						return new ImageSpan(this.role2Drawable, DynamicDrawableSpan.ALIGN_BASELINE);
					}
					if (selector.matches("fex.*"))
					{
						return new ImageSpan(this.role2Drawable, DynamicDrawableSpan.ALIGN_BASELINE);
					}
					if (selector.matches("xfen"))
					{
						return new HiddenSpan();
					}
					if (selector.matches("ex"))
					{
						return new ImageSpan(this.sampleDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
					}
					if (selector.matches("x"))
					{
						return new HiddenSpan();
					}
					return new BackgroundColorSpan(Color.GREEN);

				case TAG2:
					switch (selector)
					{
						case "t":
							return new HiddenSpan();
						case "fe":
							return new HiddenSpan();
						case "fen":
							return new HiddenSpan();
						case "xfen":
							return new HiddenSpan();
						case "fex":
							return new HiddenSpan();
						case "ex":
							return new HiddenSpan();
						case "x":
							return new HiddenSpan();
					}
					return new BackgroundColorSpan(Color.CYAN);

				case TEXT:
					return FrameNetMarkupFactory.textFactory.makeSpans(selector, flags);
			}
		}
		return null;
	}
}