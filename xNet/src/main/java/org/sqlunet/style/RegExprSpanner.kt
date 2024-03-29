/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.style

import android.text.SpannableStringBuilder
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import kotlin.math.min

/**
 * Spanner as per regexpr
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class RegExprSpanner : Spanner {

    /**
     * Replacers
     */
    private val spanReplacers: Array<SpanReplacer?>

    /**
     * Constructor
     *
     * @param regexprs  regexprs
     * @param factories span factories, distinct for each regexpr
     */
    protected constructor(regexprs: Array<String>, factories: Array<Array<SpanFactory>>) {
        val n = min(regexprs.size.toDouble(), factories.size.toDouble()).toInt()
        spanReplacers = arrayOfNulls(n)
        for (i in 0 until n) {
            spanReplacers[i] = SpanReplacer(regexprs[i], *factories[i])
        }
    }

    /**
     * Constructor
     *
     * @param regexprs  regexprs
     * @param factories span factories common to all regexprs
     */
    constructor(regexprs: Array<String>, factories: Array<SpanFactory>) {
        val n = regexprs.size
        spanReplacers = arrayOfNulls(n)
        for (i in 0 until n) {
            spanReplacers[i] = SpanReplacer(regexprs[i], *factories)
        }
    }

    /**
     * Append
     *
     * @param text  text to append
     * @param sb    spannable string builder to append to
     * @param flags flags
     */
    fun append(text: CharSequence?, sb: SpannableStringBuilder, flags: Long) {
        val from = sb.length
        sb.append(text)
        setSpan(sb, from, flags)
    }

    /**
     * Apply span
     *
     * @param sb    spannable string builder
     * @param from  start
     * @param flags flags
     */
    fun setSpan(sb: SpannableStringBuilder, from: Int, flags: Long) {
        val text = sb.subSequence(from, sb.length)
        for (spanReplacer in spanReplacers) {
            spanReplacer!!.setSpan(text, sb, from, flags)
        }
    }

    /**
     * Replacer
     *
     * @param regexpr   regexpr
     * @param factories span factories
     *
     * @author [Bernard Bou](mailto:1313ou@gmail.com)
     */
    internal class SpanReplacer(regexpr: String, vararg factories: SpanFactory) {

        /**
         * Pattern
         */
        private val pattern: Pattern

        /**
         * Span factories to call whenever pattern is found
         */
        private val spanFactories: Array<out SpanFactory>

        /**
         * Constructor
         *
         */
        init {
            val p: Pattern = try {
                Pattern.compile(regexpr, Pattern.MULTILINE)
            } catch (pse: PatternSyntaxException) {
                val regexpr2 = ""
                Pattern.compile(regexpr2, Pattern.MULTILINE)
            }
            pattern = p
            spanFactories = factories
        }

        /**
         * Make spans
         *
         * @param input input string
         * @param sb    spannable string builder
         * @param from  start
         * @param flags flags
         */
        fun setSpan(input: CharSequence, sb: SpannableStringBuilder, from: Int, flags: Long) {
            if (input.isEmpty()) {
                return
            }
            val matcher = pattern.matcher(input)
            while (matcher.find()) {
                val n = matcher.groupCount()
                for (i in 0 until n) {
                    if (matcher.group(i + 1) == null) {
                        return
                    }

                    // Log.d(TAG, '"' + matcher.group(i + 1) + '"')
                    val start = from + matcher.start(i + 1)
                    val end = from + matcher.end(i + 1)
                    if (end - start > 0) {
                        // span
                        val startSpans = spanFactories[i].make(flags)
                        applySpans(sb, start, end, startSpans!!)
                    }
                }
            }
        }

        override fun toString(): String {
            val sb = StringBuilder()
            sb.append(pattern).append(" ->")
            for (factory in spanFactories) {
                sb.append(' ').append(factory)
            }
            return sb.toString()
        }
    }
}
