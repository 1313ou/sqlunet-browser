/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.style;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.sqlunet.style.Factories;
import org.sqlunet.style.Spanner.SpanFactory;

/**
 * Span factories for SyntagNet
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SyntagNetFactories
{
	static public final SpanFactory collocationFactory = flags -> new Object[]{new BackgroundColorSpan(Color.WHITE), new ForegroundColorSpan(Color.DKGRAY), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory beforeFactory = flags -> new Object[]{new BackgroundColorSpan(Color.BLUE), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory afterFactory = flags -> new Object[]{new BackgroundColorSpan(Color.RED), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory beforeDefinitionFactory = flags -> new Object[]{new BackgroundColorSpan(Color.WHITE), new ForegroundColorSpan(Color.BLUE), new StyleSpan(Typeface.ITALIC)};

	static public final SpanFactory afterDefinitionFactory = flags -> new Object[]{new BackgroundColorSpan(Color.WHITE), new ForegroundColorSpan(Color.RED), new StyleSpan(Typeface.ITALIC)};

	// static public final SpanFactory definitionFactory = Factories.definitionFactory;
}