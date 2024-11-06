/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.style

import java.util.regex.Pattern

/**
 * Preprocessor
 *
 * @param data regexpr-replacement pairs
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class Preprocessor protected constructor(vararg data: String) {

    /**
     * Replacer
     *
     * @param regexpr     regexpr
     * @param replacement replacement
     *
     * @author [Bernard Bou](mailto:1313ou@gmail.com)
     */
    internal class Replacer(regexpr: String, private val replacement: String) {

        /**
         * Replaced pattern
         */
        private val pattern: Pattern = Pattern.compile(regexpr)

        /**
         * Replace
         *
         * @param input input
         * @return output
         */
        fun replace(input: CharSequence): String {
            val matcher = pattern.matcher(input)
            return matcher.replaceAll(replacement)
        }

        override fun toString(): String {
            return "$pattern -> $replacement"
        }
    }

    /**
     * Array of replacers
     */
    private val replacers: Array<Replacer?>

    init {
        val n = data.size / 2
        replacers = arrayOfNulls(n)
        var j = 0
        for (i in 0 until n) {
            replacers[i] = Replacer(data[j], data[j + 1])
            j += 2
        }
    }

    /**
     * Process
     *
     * @param input input
     * @return output
     */
    fun process(input: CharSequence?): CharSequence? {
        if (input == null) {
            return null
        }
        var string = input.toString()
        for (replacer in replacers) {
            string = replacer!!.replace(string)
        }
        return string
    }
}