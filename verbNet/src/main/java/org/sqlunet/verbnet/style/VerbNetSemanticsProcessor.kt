/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.style

import org.sqlunet.style.Preprocessor

/**
 * VerbNet preprocessor
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object VerbNetSemanticsProcessor : Preprocessor() {

    /**
     * Replacers
     */
    private val replacers = arrayOf<String>(
        // "([^\\(\n]*)\\((.*)\\)","<pred>$1</pred> ($2)",
        // "(event:E|(?:start|end|result)\\(E\\))","<event>$1</event>"
    )
}
