package org.sqlunet.wordnet.style;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.sqlunet.style.Factories;
import org.sqlunet.style.Spanner.SpanFactory;

/**
 * WordNet span factories
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WordNetFactories
{
	static public final SpanFactory lemmaFactory = flags -> new Object[]{new ForegroundColorSpan(Color.BLACK), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory membersFactory = flags -> new Object[]{new ForegroundColorSpan(Color.DKGRAY), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory wordFactory = flags -> new Object[]{new ForegroundColorSpan(Color.BLACK), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory definitionFactory = Factories.definitionFactory;

	static public final SpanFactory sampleFactory = Factories.exampleFactory;

	static public final SpanFactory dataFactory = Factories.dataFactory;
}
