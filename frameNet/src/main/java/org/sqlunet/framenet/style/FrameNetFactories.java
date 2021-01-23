/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet.style;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
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

	static public final SpanFactory metaFrameDefinitionFactory = flags -> new Object[]{new ForegroundColorSpan(Colors.metaFrameDefinitionForeColor), new StyleSpan(Typeface.ITALIC)};

	// lex unit
	static public final SpanFactory lexunitFactory = Factories.memberFactory;

	static public final SpanFactory definitionFactory = Factories.definitionFactory;

	// fe
	static public final SpanFactory feFactory = Factories.roleFactory;

	static public final SpanFactory fe2Factory = flags -> new Object[]{new BackgroundColorSpan(Colors.fe2BackColor), new ForegroundColorSpan(Colors.fe2ForeColor), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory feAbbrevFactory = flags -> new Object[]{new ForegroundColorSpan(Colors.feAbbrevForeColor)};

	static public final SpanFactory metaFeDefinitionFactory = flags -> new Object[]{new ForegroundColorSpan(Colors.metaFeDefinitionForeColor), new StyleSpan(Typeface.ITALIC)};

	// sentence
	static public final SpanFactory sentenceFactory = Factories.exampleFactory;

	// annotations
	static public final SpanFactory annoSetFactory = flags -> new Object[]{new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory layerTypeFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.layerTypeBackColor), new BackgroundColorSpan(Colors.layerTypeForeColor), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory labelFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.labelBackColor), new ForegroundColorSpan(Colors.labelForeColor), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory subtextFactory = flags -> new Object[]{new ForegroundColorSpan(Colors.subtextForeColor), new StyleSpan(Typeface.ITALIC)};

	static public final SpanFactory targetFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.targetBackColor), new ForegroundColorSpan(Colors.targetForeColor), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory highlightTextFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.highlightBackColor), new ForegroundColorSpan(Colors.highlightForeColor), new StyleSpan(Typeface.NORMAL)};

	static public final SpanFactory targetHighlightTextFactory = flags -> new Object[]{new BackgroundColorSpan(Colors.targetHighlightTextBackColor), new ForegroundColorSpan(Colors.targetHighlightTextForeColor), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory ptFactory = flags -> new Object[]{new ForegroundColorSpan(Colors.ptForeColor), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory gfFactory = flags -> new Object[]{new ForegroundColorSpan(Colors.gfForeColor)};

	static public final SpanFactory governorTypeFactory = flags -> new Object[]{new ForegroundColorSpan(Colors.governorTypeForeColor), new StyleSpan(Typeface.BOLD)};

	static public final SpanFactory governorFactory = flags -> new Object[]{new ForegroundColorSpan(Colors.governorForeColor), new StyleSpan(Typeface.BOLD)};
}
