/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.style;

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
	static public final SpanFactory classFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.classBackColor), new ForegroundColorSpan(Colors.classForeColor), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory roleFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.roleBackColor), new ForegroundColorSpan(Colors.roleForeColor), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory memberFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.memberBackColor), new ForegroundColorSpan(Colors.memberForeColor), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory dataFactory = flags -> new Object[]{new ForegroundColorSpan(Colors.dataForeColor), new StyleSpan(Typeface.ITALIC)};

	static public final SpanFactory definitionFactory = flags -> new Object[]{new ForegroundColorSpan(Colors.definitionForeColor), new StyleSpan(Typeface.ITALIC)};

	static public final SpanFactory exampleFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.exampleBackColor), new ForegroundColorSpan(Colors.exampleForeColor), new StyleSpan(Typeface.ITALIC)};

	static public final SpanFactory boldFactory = flags -> new Object[]{new StyleSpan(Typeface.BOLD)};

	// static public final SpanFactory italicFactory = flags -> new Object[]{new StyleSpan(Typeface.ITALIC)};
}
