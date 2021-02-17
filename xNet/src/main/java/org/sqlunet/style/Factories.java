/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.style;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.sqlunet.style.Spanner.SpanFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.ColorInt;

/**
 * Span factories
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Factories
{
	static public final SpanFactory classFactory = flags -> spans(Colors.classBackColor, Colors.classForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory roleFactory = flags -> spans(Colors.roleBackColor, Colors.roleForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory memberFactory = flags -> spans(Colors.memberBackColor, Colors.memberForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory dataFactory = flags -> spans(Colors.dataBackColor, Colors.dataForeColor, new StyleSpan(Typeface.ITALIC));

	static public final SpanFactory definitionFactory = flags -> spans(Colors.definitionBackColor, Colors.definitionForeColor, new StyleSpan(Typeface.ITALIC));

	static public final SpanFactory exampleFactory = flags -> spans(Colors.exampleBackColor, Colors.exampleForeColor, new StyleSpan(Typeface.ITALIC));

	static public final SpanFactory boldFactory = flags -> new StyleSpan(Typeface.BOLD);

	static public final SpanFactory italicFactory = flags -> new StyleSpan(Typeface.ITALIC);

	static public final SpanFactory hiddenFactory = flags -> new Spanner.HiddenSpan();

	/**
	 * Build spans
	 *
	 * @param bg         background color (il TRANSPARENT skipped)
	 * @param fg         foreground color (il TRANSPARENT skipped)
	 * @param otherSpans other spans
	 * @return spans
	 */
	static public Object spans(@ColorInt final int bg, @ColorInt final int fg, final CharacterStyle... otherSpans)
	{
		if (bg == Color.TRANSPARENT && fg == Color.TRANSPARENT)
		{
			return otherSpans;
		}
		List<Object> spans = new ArrayList<>();
		if (bg != Color.TRANSPARENT)
		{
			spans.add(new BackgroundColorSpan(bg));
		}
		if (fg != Color.TRANSPARENT)
		{
			spans.add(new ForegroundColorSpan(fg));
		}
		Collections.addAll(spans, otherSpans);
		return spans;
	}
}
