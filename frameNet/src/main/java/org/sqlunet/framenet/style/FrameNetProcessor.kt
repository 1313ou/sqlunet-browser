/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.style

import org.sqlunet.style.Preprocessor

/**
 * FrameNet pre processor
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FrameNetProcessor : Preprocessor(*replacers) {

    /**
     * Split utility
     *
     * @param text text to split
     * @return split text
     */
    fun split(text: CharSequence?): Array<CharSequence?> {
        val processedText = process(text)
        return processedText?.toString()?.split("\n".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray() ?: arrayOfNulls(0)
    }

    companion object {
        /**
         * Replacers for preprocessor
         */
        private val replacers = arrayOf(
            "<ex>", "\n<ex>"
        )
    }
}
