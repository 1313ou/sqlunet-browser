/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.style

import android.graphics.Typeface
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import org.sqlunet.style.Factories.spans
import org.sqlunet.style.Spanner

/**
 * Span factories for SyntagNet
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object SyntagNetFactories {

    val collocationFactory = Spanner.SpanFactory { _: Long -> spans(Colors.collocationBackColor, Colors.collocationForeColor, StyleSpan(Typeface.BOLD)) }
    val word1Factory = Spanner.SpanFactory { _: Long -> spans(Colors.word1BackColor, Colors.word1ForeColor, StyleSpan(Typeface.BOLD)) }
    val word2Factory = Spanner.SpanFactory { _: Long -> spans(Colors.word2BackColor, Colors.word2ForeColor, StyleSpan(Typeface.BOLD)) }
    val definition1Factory = Spanner.SpanFactory { _: Long -> spans(Colors.definition1BackColor, Colors.definition1ForeColor, StyleSpan(Typeface.ITALIC)) }
    val definition2Factory = Spanner.SpanFactory { _: Long -> spans(Colors.definition2BackColor, Colors.definition2ForeColor, StyleSpan(Typeface.ITALIC)) }
    val idsFactory = Spanner.SpanFactory { _: Long -> spans(Colors.idsBackColor, Colors.idsForeColor, RelativeSizeSpan(.7f), StyleSpan(Typeface.ITALIC)) }
}