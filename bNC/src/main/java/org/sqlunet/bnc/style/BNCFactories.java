/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.bnc.style;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.sqlunet.style.Colors;
import org.sqlunet.style.Spanner.SpanFactory;

public class BNCFactories
{
	static public final SpanFactory headerFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.pink), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};
}
