/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.style

import android.graphics.Typeface
import android.text.style.StyleSpan
import org.sqlunet.style.Factories
import org.sqlunet.style.Factories.spans
import org.sqlunet.style.Spanner

/**
 * FrameNet span factories
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object FrameNetFactories {
    val boldFactory = Factories.boldFactory
    val dataFactory = Factories.dataFactory

    // frame
    val frameFactory = Factories.classFactory
    val metaFrameDefinitionFactory = Spanner.SpanFactory { _: Long -> spans(Colors.metaFrameDefinitionBackColor, Colors.metaFrameDefinitionForeColor, StyleSpan(Typeface.ITALIC)) }

    // lex unit
    val lexunitFactory = Factories.memberFactory
    val definitionFactory = Factories.definitionFactory

    // fe
    val feFactory = Factories.roleFactory
    val fe2Factory = Spanner.SpanFactory { _: Long -> spans(Colors.fe2BackColor, Colors.fe2ForeColor, StyleSpan(Typeface.BOLD)) }
    val feAbbrevFactory = Spanner.SpanFactory { _: Long -> spans(Colors.feAbbrevBackColor, Colors.feAbbrevForeColor) }
    val metaFeDefinitionFactory = Spanner.SpanFactory { _: Long -> spans(Colors.metaFeDefinitionBackColor, Colors.metaFeDefinitionForeColor, StyleSpan(Typeface.ITALIC)) }

    // sentence
    val sentenceFactory = Factories.exampleFactory

    // governor
    val governorTypeFactory = Spanner.SpanFactory { _: Long -> spans(Colors.governorTypeBackColor, Colors.governorTypeForeColor, StyleSpan(Typeface.BOLD)) }
    val governorFactory = Spanner.SpanFactory { _: Long -> spans(Colors.governorBackColor, Colors.governorForeColor, StyleSpan(Typeface.BOLD)) }

    // annotations
    val annoSetFactory = Spanner.SpanFactory { _: Long -> spans(Colors.annoSetBackColor, Colors.annoSetForeColor, StyleSpan(Typeface.BOLD)) }
    val layerTypeFactory = Spanner.SpanFactory { _: Long -> spans(Colors.layerTypeBackColor, Colors.layerTypeForeColor, StyleSpan(Typeface.BOLD)) }
    val labelFactory = Spanner.SpanFactory { _: Long -> spans(Colors.labelBackColor, Colors.labelForeColor, StyleSpan(Typeface.BOLD)) }
    val subtextFactory = Spanner.SpanFactory { _: Long -> spans(Colors.subtextBackColor, Colors.subtextForeColor, StyleSpan(Typeface.ITALIC)) }
    val targetFactory = Spanner.SpanFactory { _: Long -> spans(Colors.targetBackColor, Colors.targetForeColor, StyleSpan(Typeface.BOLD)) }
    val highlightTextFactory = Spanner.SpanFactory { _: Long -> spans(org.sqlunet.style.Colors.textHighlightBackColor, org.sqlunet.style.Colors.textHighlightForeColor, StyleSpan(Typeface.NORMAL)) }
    val targetHighlightTextFactory = Spanner.SpanFactory { _: Long -> spans(Colors.targetHighlightTextBackColor, Colors.targetHighlightTextForeColor, StyleSpan(Typeface.BOLD)) }
    val ptFactory = Spanner.SpanFactory { _: Long -> spans(Colors.ptBackColor, Colors.ptForeColor, StyleSpan(Typeface.BOLD)) }
    val gfFactory = Spanner.SpanFactory { _: Long -> spans(Colors.gfBackColor, Colors.gfForeColor) }
}
