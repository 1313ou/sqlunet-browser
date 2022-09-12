/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.syntagnet.style;

import android.graphics.Typeface;
import android.text.style.RelativeSizeSpan;
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
	static public final SpanFactory collocationFactory = flags -> Factories.spans(Colors.collocationBackColor, Colors.collocationForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory word1Factory = flags -> Factories.spans(Colors.word1BackColor, Colors.word1ForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory word2Factory = flags -> Factories.spans(Colors.word2BackColor, Colors.word2ForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory definition1Factory = flags -> Factories.spans(Colors.definition1BackColor, Colors.definition1ForeColor, new StyleSpan(Typeface.ITALIC));

	static public final SpanFactory definition2Factory = flags -> Factories.spans(Colors.definition2BackColor, Colors.definition2ForeColor, new StyleSpan(Typeface.ITALIC));

	static public final SpanFactory idsFactory = flags -> Factories.spans(Colors.idsBackColor, Colors.idsForeColor, new RelativeSizeSpan(.7F), new StyleSpan(Typeface.ITALIC));
}