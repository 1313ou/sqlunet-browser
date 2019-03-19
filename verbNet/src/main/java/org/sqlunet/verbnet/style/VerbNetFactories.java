package org.sqlunet.verbnet.style;

import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.Nullable;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.sqlunet.style.Colors;
import org.sqlunet.style.Factories;
import org.sqlunet.style.Spanner.SpanFactory;

/**
 * VerbNet span factories
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VerbNetFactories
{
	// class

	static public final SpanFactory classFactory = Factories.classFactory;

	// member

	static public final SpanFactory memberFactory = Factories.memberFactory;

	// definition

	static public final SpanFactory definitionFactory = Factories.definitionFactory;

	// groupings

	static public final SpanFactory groupingFactory = flags -> new ForegroundColorSpan(Color.LTGRAY);

	// role

	static public final SpanFactory roleFactory = Factories.roleFactory;

	static final SpanFactory themroleFactory = flags -> new BackgroundColorSpan(Colors.lt_magenta);

	// frame

	static public final SpanFactory frameFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.brown), new ForegroundColorSpan(Color.WHITE), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory framesubnameFactory = flags -> new Object[]{new BackgroundColorSpan(Color.LTGRAY), new ForegroundColorSpan(Color.WHITE)};

	static final SpanFactory catFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.lt_brown), new ForegroundColorSpan(Color.DKGRAY)};

	static final SpanFactory catValueFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.lt_magenta)};

	// semantics

	static final SpanFactory predicateFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.orange), new ForegroundColorSpan(Color.WHITE)};

	@Nullable
	static final SpanFactory argsFactory = flags -> null;

	static final SpanFactory constantFactory = flags -> new Object[]{new BackgroundColorSpan(Color.LTGRAY)};

	static final SpanFactory eventFactory = flags -> new Object[]{new BackgroundColorSpan(Color.BLUE), new ForegroundColorSpan(Color.WHITE)};

	// restrs

	static public final SpanFactory restrsFactory = flags -> new BackgroundColorSpan(Colors.lt_brown);

	// example

	static public final SpanFactory exampleFactory = Factories.exampleFactory;
}
