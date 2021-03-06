/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.bnc.style;

import android.graphics.Typeface;
import android.text.style.StyleSpan;

import org.sqlunet.style.Factories;
import org.sqlunet.style.Spanner.SpanFactory;

public class BNCFactories
{
	static public final SpanFactory headerFactory = flags -> Factories.spans(Colors.bncHeaderBackColor, Colors.bncHeaderForeColor, new StyleSpan(Typeface.BOLD));
}
