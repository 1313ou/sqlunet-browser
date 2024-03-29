/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.style

import android.graphics.Typeface
import android.text.style.StyleSpan
import org.sqlunet.style.Factories
import org.sqlunet.style.Factories.spans
import org.sqlunet.style.Spanner

/**
 * WordNet span factories
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object WordNetFactories {

    @JvmField
    val membersFactory = Spanner.SpanFactory { _: Long -> spans(Colors.membersBackColor, Colors.membersForeColor, StyleSpan(Typeface.BOLD)) }

    @JvmField
    val wordFactory = Spanner.SpanFactory { _: Long -> spans(Colors.wordBackColor, Colors.wordForeColor, StyleSpan(Typeface.BOLD)) }

    @JvmField
    val definitionFactory = Factories.definitionFactory

    @JvmField
    val sampleFactory = Factories.exampleFactory

    @JvmField
    val relationFactory = Factories.relationFactory

    @JvmField
    val dataFactory = Factories.dataFactory
}
