/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.style;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.sqlunet.style.Spanner.SpanFactory;

/**
 * Span factories
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Factories
{
	static public final SpanFactory exampleFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.xlt_yellow), new StyleSpan(Typeface.ITALIC)};

	static public final SpanFactory definitionFactory = flags -> new Object[]{new ForegroundColorSpan(Colors.lt_blue), new StyleSpan(Typeface.ITALIC)};

	static public final SpanFactory classFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.dk_red), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory roleFactory = flags -> new Object[]{new BackgroundColorSpan(Color.MAGENTA), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory memberFactory = flags -> new Object[]{new BackgroundColorSpan(Color.RED), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory dataFactory = flags -> new Object[]{new ForegroundColorSpan(Color.GRAY), new StyleSpan(Typeface.ITALIC)};

	static public final SpanFactory boldFactory = flags -> new Object[]{new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory italicFactory = flags -> new Object[]{new StyleSpan(Typeface.ITALIC)};
}
