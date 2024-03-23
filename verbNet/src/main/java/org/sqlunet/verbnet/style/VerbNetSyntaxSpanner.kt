/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.style

import org.sqlunet.style.RegExprSpanner

/**
 * VerbNet syntax processor
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object VerbNetSyntaxSpanner : RegExprSpanner(
    arrayOf(
        "^([^\\s\n]*)",  // cat : 1 capture
        "^[^\\s\n]* (\\p{Upper}[\\p{Lower}_\\p{Upper}]*)"
    ),
    arrayOf(
        arrayOf(VerbNetFactories.catFactory),
        arrayOf(VerbNetFactories.catValueFactory)
    )
)
