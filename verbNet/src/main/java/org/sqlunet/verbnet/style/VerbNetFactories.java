/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.verbnet.style;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.sqlunet.style.Colors;
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

	static public final SpanFactory groupingFactory = flags -> new ForegroundColorSpan(Colors.groupingForeColor);

	// role

	static public final SpanFactory roleFactory = Factories.roleFactory;

	static final SpanFactory themroleFactory = flags -> new BackgroundColorSpan(Colors.themroleBackColor);

	// frame

	static public final SpanFactory frameFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.vnFrameBackColor), new ForegroundColorSpan(Colors.vnFrameForeColor), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory framesubnameFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.vnFrameSubnameBackColor), new ForegroundColorSpan(Colors.vnFrameSubnameForeColor)};

	static final SpanFactory catFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.catBackColor), new ForegroundColorSpan(Colors.catForeColor)};

	static final SpanFactory catValueFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.catValueBackColor)};

	// semantics

	static final SpanFactory predicateFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.vnPredicateBackColor), new ForegroundColorSpan(Colors.vnPredicateForeColor)};

	@Nullable
	static final SpanFactory argsFactory = flags -> null;

	static final SpanFactory constantFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.constantBackColor), new ForegroundColorSpan(Colors.constantForeColor)};

	static final SpanFactory eventFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.eventBackColor), new ForegroundColorSpan(Colors.eventForeColor)};

	// restrs

	static public final SpanFactory restrsFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.restrBackColor), new ForegroundColorSpan(Colors.restrForeColor)};

	// example

	static public final SpanFactory exampleFactory = Factories.exampleFactory;
}
