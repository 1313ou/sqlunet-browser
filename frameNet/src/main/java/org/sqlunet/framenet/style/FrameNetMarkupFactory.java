package org.sqlunet.framenet.style;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.BackgroundColorSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;

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

	static public final int FEDEF = 0x10000;

	/**
	 * Role drawable
	 */
	private final Drawable roleDrawable;

	/**
	 *
	 */
	private final Drawable role1Drawable;

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
		this.role1Drawable = Spanner.getDrawable(context, R.drawable.role1);
		this.relationDrawable = Spanner.getDrawable(context, R.drawable.relation);
		this.sampleDrawable = Spanner.getDrawable(context, R.drawable.sample);
	}

	/**
	 * Background factory
	 */
	private static final MarkupSpanner.SpanFactory backgroundFactory = new MarkupSpanner.SpanFactory()
	{
		@Override
		public Object makeSpans(final String selector, final long flags)
		{
			if ("fe".equals(selector)) //$NON-NLS-1$
			{
				return new BackgroundColorSpan(Color.MAGENTA);
			}
			if ("t".equals(selector)) //$NON-NLS-1$
			{
				return new BackgroundColorSpan(Color.BLACK);
			}
			if ("fen".equals(selector)) //$NON-NLS-1$
			{
				return new BackgroundColorSpan((flags & FrameNetMarkupFactory.FEDEF) == 0 ? Color.MAGENTA : Color.LTGRAY);
			}
			if (selector.matches("fex.*")) //$NON-NLS-1$
			{
				return new BackgroundColorSpan((flags & FrameNetMarkupFactory.FEDEF) == 0 ? Colors.pink : Colors.ltmagenta);
			}
			if (selector.matches("xfen")) //$NON-NLS-1$
			{
				return new BackgroundColorSpan(Colors.ltyellow);
			}
			if ("ex".equals(selector)) //$NON-NLS-1$
			{
				return new StyleSpan(android.graphics.Typeface.ITALIC); // new BackgroundColorSpan(Color.LTGRAY);
			}
			return null;
		}
	};

	/**
	 * Foreground factory
	 */
	private static final MarkupSpanner.SpanFactory foregroundFactory = new MarkupSpanner.SpanFactory()
	{
		@Override
		public Object makeSpans(final String selector, final long flags)
		{
			if ("t".equals(selector)) //$NON-NLS-1$
			{
				return new ForegroundColorSpan(Color.WHITE);
			}
			if ("fe".equals(selector)) //$NON-NLS-1$
			{
				return new ForegroundColorSpan(Color.WHITE);
			}
			return null;
		}
	};

	@Override
	public Object makeSpans(final String selector, final long flags)
	{
		// Log.d(FrameNetMarkupFactory.TAG, selector + " " + flags);
		final SpanPosition position = SpanPosition.valueOf(flags);
		switch (position)
		{
			case TAG1:
				if ("t".equals(selector)) //$NON-NLS-1$
				{
					return new ImageSpan(this.relationDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
				}
				if ("fe".equals(selector)) //$NON-NLS-1$
				{
					return new ImageSpan(this.roleDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
				}
				if ("fen".equals(selector)) //$NON-NLS-1$
				{
					return new ImageSpan(this.role1Drawable, DynamicDrawableSpan.ALIGN_BASELINE);
				}
				if (selector.matches("fex.*")) //$NON-NLS-1$
				{
					return new ImageSpan(this.roleDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
				}
				if (selector.matches("xfen")) //$NON-NLS-1$
				{
					return new HiddenSpan();
				}
				if (selector.matches("ex")) //$NON-NLS-1$
				{
					return new ImageSpan(this.sampleDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
				}
				return new BackgroundColorSpan(Color.GREEN);

			case TAG2:
				switch (selector)
				{
					case "t":
						//$NON-NLS-1$
						return new HiddenSpan();
					case "fe":
						//$NON-NLS-1$
						return new HiddenSpan();
					case "fen":
						//$NON-NLS-1$
						return new HiddenSpan();
					case "xfen":
						//$NON-NLS-1$
						return new HiddenSpan();
					case "fex":
						//$NON-NLS-1$
						return new HiddenSpan();
					case "ex":
						//$NON-NLS-1$
						return new HiddenSpan();
				}
				return new BackgroundColorSpan(Color.GREEN);

			case TEXT:
				final Object backgroundSpan = FrameNetMarkupFactory.backgroundFactory.makeSpans(selector, flags);
				final Object foregroundSpan = FrameNetMarkupFactory.foregroundFactory.makeSpans(selector, flags);
				if (backgroundSpan != null && foregroundSpan != null)
				{
					return new Object[]{backgroundSpan, foregroundSpan};
				}
				if (backgroundSpan != null)
				{
					return backgroundSpan;
				}
				if (foregroundSpan != null)
				{
					return foregroundSpan;
				}
				return new BackgroundColorSpan(Color.GREEN);
		}
		return null;
	}
}