/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
	@Nullable
	static private final MarkupSpanner.SpanFactory textFactory = (selector, flags) -> {
		if ("fe".equals(selector))
		{
			return new Object[]{new BackgroundColorSpan(Colors.feBackColor), new ForegroundColorSpan(Colors.feBackColor)};
		}
		if ("t".equals(selector)) // target
		{
			return new Object[]{new BackgroundColorSpan(Colors.tBackColor), new ForegroundColorSpan(Colors.tForeColor)};
		}
		if ("fen".equals(selector))
		{
			if ((flags & FrameNetMarkupFactory.FEDEF) != 0)
			{
				return new ForegroundColorSpan(Colors.fenWithinDefForeColor);
			}
			else
			{
				return new Object[]{new BackgroundColorSpan(Colors.fenBackColor), new ForegroundColorSpan(Colors.fenForeColor)};
			}
		}
		if (selector.matches("fex.*"))
		{
			if ((flags & FrameNetMarkupFactory.FEDEF) == 0)
			{
				return new Object[]{new ForegroundColorSpan(Colors.fexWithinDefForeColor), new UnderlineSpan()};
			}
			else
			{
				return new Object[]{new BackgroundColorSpan(Colors.fexBackColor), new ForegroundColorSpan(Colors.fexForeColor)};
			}
		}
		if ("xfen".equals(selector))
		{
			return new ForegroundColorSpan(Colors.xfenForeColor);// new BackgroundColorSpan(Colors.xfenBackColor);
		}
		if ("ex".equals(selector))
		{
			return new Object[]{new StyleSpan(android.graphics.Typeface.ITALIC)};
		}
		if ("x".equals(selector))
		{
			return new Object[]{new ForegroundColorSpan(Colors.xForeColor), new StyleSpan(android.graphics.Typeface.BOLD)};
		}
		return null;
	};

	/**
	 * Top role drawable
	 */
	@NonNull
	private final Drawable role2Drawable;

	/**
	 * Role drawable
	 */
	@NonNull
	private final Drawable roleDrawable;

	/**
	 * Relation drawable
	 */
	@NonNull
	private final Drawable relationDrawable;

	/**
	 * Sample drawable
	 */
	@NonNull
	private final Drawable sampleDrawable;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	FrameNetMarkupFactory(@NonNull final Context context)
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
	@Nullable
	@Override
	public Object makeSpans(@NonNull final String selector, final long flags)
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
					return new BackgroundColorSpan(Colors.tag1ForeColor);

				case TAG2:
					switch (selector)
					{
						case "t":
						case "x":
						case "ex":
						case "fex":
						case "xfen":
						case "fen":
						case "fe":
							return new HiddenSpan();
					}
					return new BackgroundColorSpan(Colors.tag2ForeColor);

				case TEXT:
					assert textFactory != null;
					return textFactory.makeSpans(selector, flags);
			}
		}
		return null;
	}
}