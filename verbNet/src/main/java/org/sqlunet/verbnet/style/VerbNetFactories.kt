/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.verbnet.style;

import android.graphics.Typeface;
import android.text.style.StyleSpan;

import org.sqlunet.style.Factories;
import org.sqlunet.style.Spanner.SpanFactory;

import androidx.annotation.Nullable;

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

	static public final SpanFactory groupingFactory = flags -> Factories.spans(Colors.groupingBackColor, Colors.groupingForeColor);

	// role

	static public final SpanFactory roleFactory = Factories.roleFactory;

	static final SpanFactory themroleFactory = flags -> Factories.spans(Colors.themroleBackColor, Colors.themroleForeColor);

	// frame

	static public final SpanFactory frameFactory = flags -> Factories.spans(Colors.vnFrameBackColor, Colors.vnFrameForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory framesubnameFactory = flags -> Factories.spans(Colors.vnFrameSubnameBackColor, Colors.vnFrameSubnameForeColor);

	static final SpanFactory catFactory = flags -> Factories.spans(Colors.catBackColor, Colors.catForeColor);

	static final SpanFactory catValueFactory = flags -> Factories.spans(Colors.catValueBackColor, Colors.catValueForeColor);

	// semantics

	static final SpanFactory predicateFactory = flags -> Factories.spans(Colors.vnPredicateBackColor, Colors.vnPredicateForeColor);

	@Nullable
	static final SpanFactory argsFactory = flags -> null;

	static final SpanFactory constantFactory = flags -> Factories.spans(Colors.constantBackColor, Colors.constantForeColor);

	static final SpanFactory eventFactory = flags -> Factories.spans(Colors.eventBackColor, Colors.eventForeColor);

	// restrs

	static public final SpanFactory restrsFactory = flags -> Factories.spans(Colors.restrBackColor, Colors.restrForeColor);

	// example

	static public final SpanFactory exampleFactory = Factories.exampleFactory;
}
