/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.propbank.style;

import android.graphics.Typeface;
import android.text.style.StyleSpan;

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

	static public final SpanFactory thetaFactory = flags -> Factories.spans(Colors.thetaBackColor, Colors.thetaForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory definitionFactory = Factories.definitionFactory;

	static public final SpanFactory exampleFactory = Factories.exampleFactory;

	static public final SpanFactory relationFactory = flags -> Factories.spans(Colors.relationBackColor, Colors.relationForeColor, new StyleSpan(Typeface.BOLD));
}