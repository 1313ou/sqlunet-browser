/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix.style

import android.graphics.Typeface
import android.text.style.StyleSpan
import org.sqlunet.style.Factories
import org.sqlunet.style.Factories.spans
import org.sqlunet.style.Spanner

object PredicateMatrixFactories {

    // name
    val nameFactory = Spanner.SpanFactory { _: Long -> spans(Colors.predicateNameBackColor, Colors.predicateNameForeColor, StyleSpan(Typeface.BOLD)) }

    // group
    val groupFactory = Spanner.SpanFactory { _: Long -> spans(Colors.groupBackColor, Colors.groupForeColor, StyleSpan(Typeface.BOLD)) }

    // definition
    val definitionFactory = Factories.definitionFactory

    // class
    val classFactory = Factories.classFactory

    // role
    val roleFactory = Factories.roleFactory

    // role alias
    val roleAliasFactory = Spanner.SpanFactory { _: Long -> spans(Colors.roleAliasBackColor, Colors.roleAliasForeColor, StyleSpan(Typeface.BOLD)) }

    // data
    val dataFactory = Factories.dataFactory
}
