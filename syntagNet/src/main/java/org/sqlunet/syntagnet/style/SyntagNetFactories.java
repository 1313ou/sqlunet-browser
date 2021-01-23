/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.style;

import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import org.sqlunet.style.Colors;
import org.sqlunet.style.Spanner.SpanFactory;

/**
 * Span factories for SyntagNet
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SyntagNetFactories
{
	static public final int COLOR1 = 0xFFE46D42;

	static public final int COLOR2 = 0xFF76608B;

	static public final SpanFactory collocationFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.collocationBackColor), new ForegroundColorSpan(Colors.collocationForeColor), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory word1Factory = flags -> new Object[]{new BackgroundColorSpan(Colors.word1BackColor), new ForegroundColorSpan(Colors.word1ForeColor), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory word2Factory = flags -> new Object[]{new BackgroundColorSpan(Colors.word2BackColor), new ForegroundColorSpan(Colors.word2ForeColor), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory definition1Factory = flags -> new Object[]{new BackgroundColorSpan(Colors.definition1BackColor), new ForegroundColorSpan(Colors.definition1ForeColor), new StyleSpan(Typeface.ITALIC)};

	static public final SpanFactory definition2Factory = flags -> new Object[]{new BackgroundColorSpan(Colors.definition2BackColor), new ForegroundColorSpan(Colors.definition2ForeColor), new StyleSpan(Typeface.ITALIC)};

	static public final SpanFactory idsFactory = flags -> new Object[]{new RelativeSizeSpan(.7F), new ForegroundColorSpan(Colors.idsForeColor), new StyleSpan(Typeface.ITALIC)};
}