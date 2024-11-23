/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.style

import android.graphics.Typeface
import android.text.style.StyleSpan
import org.sqlunet.style.Factories
import org.sqlunet.style.Factories.spans
import org.sqlunet.style.Spanner

/**
 * Span factories for PropBank
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object PropBankFactories {

    val roleSetFactory = Factories.classFactory
    val roleFactory = Factories.roleFactory
    val definitionFactory = Factories.definitionFactory
    val funcFactory = { _: Long -> spans(Colors.funcBackColor, Colors.funcForeColor, StyleSpan(Typeface.BOLD)) }
    val vnroleFactory = Spanner.SpanFactory { _: Long -> spans(Colors.vnroleBackColor, Colors.vnroleForeColor, StyleSpan(Typeface.BOLD)) }
    val fnfeFactory = Spanner.SpanFactory { _: Long -> spans(Colors.fnfeBackColor, Colors.fnfeForeColor, StyleSpan(Typeface.BOLD)) }
    val exampleFactory = Factories.exampleFactory
    val relationFactory = Spanner.SpanFactory { _: Long -> spans(Colors.relationBackColor, Colors.relationForeColor, StyleSpan(Typeface.BOLD)) }
}