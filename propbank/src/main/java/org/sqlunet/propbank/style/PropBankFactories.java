package org.sqlunet.propbank.style;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
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

	static public final SpanFactory thetaFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Color.BLUE), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};
		}
	};

	public static final SpanFactory definitionFactory = Factories.definitionFactory;

	static public final SpanFactory exampleFactory = Factories.exampleFactory;

	static public final SpanFactory relationFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{new BackgroundColorSpan(Color.GRAY), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};
		}
	};
}