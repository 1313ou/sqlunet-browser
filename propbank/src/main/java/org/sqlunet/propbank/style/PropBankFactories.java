/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.propbank.style;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.sqlunet.style.Colors;
import org.sqlunet.style.Factories;
import org.sqlunet.style.Spanner.SpanFactory;

/**
 * Span factories for PropBank
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PropBankFactories
{
	static public final SpanFactory roleSetFactory = Factories.classFactory;

	static public final SpanFactory roleFactory = Factories.roleFactory;

	static public final SpanFactory thetaFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.thetaBackColor), new ForegroundColorSpan(Colors.thetaForeColor), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory definitionFactory = Factories.definitionFactory;

	static public final SpanFactory exampleFactory = Factories.exampleFactory;

	static public final SpanFactory relationFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.relationBackColor), new ForegroundColorSpan(Colors.relationForeColor), new StyleSpan(Typeface.BOLD)};
}