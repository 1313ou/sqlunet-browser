/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.predicatematrix.style;

import android.graphics.Typeface;
import android.text.style.StyleSpan;

import org.sqlunet.style.Factories;
import org.sqlunet.style.Spanner.SpanFactory;

public class PredicateMatrixFactories
{
	// name
	static public final SpanFactory nameFactory = flags -> Factories.spans(Colors.predicateNameBackColor, Colors.predicateNameForeColor, new StyleSpan(Typeface.BOLD));

	// group
	static public final SpanFactory groupFactory = flags -> Factories.spans(Colors.groupBackColor, Colors.groupForeColor, new StyleSpan(Typeface.BOLD));

	// definition
	static public final SpanFactory definitionFactory = Factories.definitionFactory;

	// class
	static public final SpanFactory classFactory = Factories.classFactory;

	// role
	static public final SpanFactory roleFactory = Factories.roleFactory;

	// role alias
	static public final SpanFactory roleAliasFactory = flags -> Factories.spans(Colors.roleAliasBackColor, Colors.roleAliasForeColor, new StyleSpan(Typeface.BOLD));

	// data
	static public final SpanFactory dataFactory = Factories.dataFactory;
}
