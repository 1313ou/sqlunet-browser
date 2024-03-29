/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.style

import android.graphics.Typeface
import android.text.style.StyleSpan
import org.sqlunet.style.Factories
import org.sqlunet.style.Factories.spans
import org.sqlunet.style.Spanner

/**
 * VerbNet span factories
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object VerbNetFactories {

    // class
    val classFactory = Factories.classFactory

    // member
    val memberFactory = Factories.memberFactory

    // definition
    val definitionFactory = Factories.definitionFactory

    // groupings
    val groupingFactory = Spanner.SpanFactory { _: Long -> spans(Colors.groupingBackColor, Colors.groupingForeColor) }

    // role
    val roleFactory = Factories.roleFactory

    @JvmField
    val themroleFactory = Spanner.SpanFactory { _: Long -> spans(Colors.themroleBackColor, Colors.themroleForeColor) }

    // frame
    val frameFactory = Spanner.SpanFactory { _: Long -> spans(Colors.vnFrameBackColor, Colors.vnFrameForeColor, StyleSpan(Typeface.BOLD)) }
    val framesubnameFactory = Spanner.SpanFactory { _: Long -> spans(Colors.vnFrameSubnameBackColor, Colors.vnFrameSubnameForeColor) }

    @JvmField
    val catFactory = Spanner.SpanFactory { _: Long -> spans(Colors.catBackColor, Colors.catForeColor) }

    @JvmField
    val catValueFactory = Spanner.SpanFactory { _: Long -> spans(Colors.catValueBackColor, Colors.catValueForeColor) }

    // semantics
    @JvmField
    val predicateFactory = Spanner.SpanFactory { _: Long -> spans(Colors.vnPredicateBackColor, Colors.vnPredicateForeColor) }

    @JvmField
    val argsFactory: Spanner.SpanFactory = Spanner.SpanFactory { _: Long -> null }

    @JvmField
    val constantFactory = Spanner.SpanFactory { _: Long -> spans(Colors.constantBackColor, Colors.constantForeColor) }

    @JvmField
    val eventFactory = Spanner.SpanFactory { _: Long -> spans(Colors.eventBackColor, Colors.eventForeColor) }

    // restrs
    val restrsFactory = Spanner.SpanFactory { _: Long -> spans(Colors.restrBackColor, Colors.restrForeColor) }

    // example
    val exampleFactory = Factories.exampleFactory
}
