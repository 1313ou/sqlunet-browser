/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.style;

import android.graphics.Typeface;
import android.text.style.StyleSpan;

import org.sqlunet.style.Colors;
import org.sqlunet.style.Factories;
import org.sqlunet.style.Spanner.SpanFactory;

/**
 * WordNet span factories
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WordNetFactories
{
	static public final SpanFactory lemmaFactory = flags -> Factories.spans(Colors.lemmaBackColor, Colors.lemmaForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory membersFactory = flags -> Factories.spans(Colors.membersBackColor, Colors.membersForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory wordFactory = flags -> Factories.spans(Colors.wordBackColor, Colors.wordForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory definitionFactory = Factories.definitionFactory;

	static public final SpanFactory sampleFactory = Factories.exampleFactory;

	static public final SpanFactory dataFactory = Factories.dataFactory;
}
