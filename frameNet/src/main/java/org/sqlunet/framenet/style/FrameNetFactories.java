/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet.style;

import android.graphics.Typeface;
import android.text.style.StyleSpan;

import org.sqlunet.style.Colors;
import org.sqlunet.style.Factories;
import org.sqlunet.style.Spanner.SpanFactory;

/**
 * FrameNet span factories
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetFactories
{
	static public final SpanFactory boldFactory = Factories.boldFactory;

	static public final SpanFactory dataFactory = Factories.dataFactory;

	// frame
	static public final SpanFactory frameFactory = Factories.classFactory;

	static public final SpanFactory metaFrameDefinitionFactory = flags -> Factories.spans(Colors.metaFrameDefinitionBackColor, Colors.metaFrameDefinitionForeColor, new StyleSpan(Typeface.ITALIC));

	// lex unit
	static public final SpanFactory lexunitFactory = Factories.memberFactory;

	static public final SpanFactory definitionFactory = Factories.definitionFactory;

	// fe
	static public final SpanFactory feFactory = Factories.roleFactory;

	static public final SpanFactory fe2Factory = flags -> Factories.spans(Colors.fe2BackColor, Colors.fe2ForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory feAbbrevFactory = flags -> Factories.spans(Colors.feAbbrevBackColor, Colors.feAbbrevForeColor);

	static public final SpanFactory metaFeDefinitionFactory = flags -> Factories.spans(Colors.metaFeDefinitionBackColor, Colors.metaFeDefinitionForeColor, new StyleSpan(Typeface.ITALIC));

	// sentence
	static public final SpanFactory sentenceFactory = Factories.exampleFactory;

	// governor
	static public final SpanFactory governorTypeFactory = flags -> Factories.spans(Colors.governorTypeBackColor, Colors.governorTypeForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory governorFactory = flags -> Factories.spans(Colors.governorBackColor, Colors.governorForeColor, new StyleSpan(Typeface.BOLD));

	// annotations
	static public final SpanFactory annoSetFactory = flags -> Factories.spans(Colors.annoSetBackColor, Colors.annoSetForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory layerTypeFactory = flags -> Factories.spans(Colors.layerTypeBackColor, Colors.layerTypeForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory labelFactory = flags -> Factories.spans(Colors.labelBackColor, Colors.labelForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory subtextFactory = flags -> Factories.spans(Colors.subtextBackColor, Colors.subtextForeColor, new StyleSpan(Typeface.ITALIC));

	static public final SpanFactory targetFactory = flags -> Factories.spans(Colors.targetBackColor, Colors.targetForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory highlightTextFactory = flags -> Factories.spans(Colors.highlightBackColor, Colors.highlightForeColor, new StyleSpan(Typeface.NORMAL));

	static public final SpanFactory targetHighlightTextFactory = flags -> Factories.spans(Colors.targetHighlightTextBackColor, Colors.targetHighlightTextForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory ptFactory = flags -> Factories.spans(Colors.ptBackColor, Colors.ptForeColor, new StyleSpan(Typeface.BOLD));

	static public final SpanFactory gfFactory = flags -> Factories.spans(Colors.gfBackColor, Colors.gfForeColor);
}
