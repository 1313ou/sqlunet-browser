/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.style

import org.sqlunet.style.RegExprSpanner

/**
 * VerbNet semantics processor
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object VerbNetSemanticsSpanner : RegExprSpanner(
    arrayOf(
        "([^\\(\n]*)\\((.*)\\)",  // predicate/args : 2 captures //
        "event:((?:E|(?:start|end|result|during)\\(E\\)))",  // event arg : 1 capture //
        "[\\( ]((?!event|E)[^\\(\\), \n]*)",  // role arg //
        "(constant\\:[^\\s,\\)]*)"
    ),
    arrayOf(
        arrayOf(VerbNetFactories.predicateFactory, VerbNetFactories.argsFactory),
        arrayOf(VerbNetFactories.eventFactory), arrayOf(VerbNetFactories.themroleFactory), arrayOf(VerbNetFactories.constantFactory)
    )
)