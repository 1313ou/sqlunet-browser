/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.style

import android.text.SpannableStringBuilder
import java.util.regex.Pattern

/**
 * Spanner as per mark-up tags
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object MarkupSpanner : Spanner() {

    /**
     * Apply spans
     *
     * @param text          input text
     * @param sb            spannable string builder
     * @param spanFactory   span factory
     * @param flags         flags
     * @param pattern       pattern
     * @param extraPatterns more patterns
     * @return spannable string builder
     */
    @JvmStatic
    fun setSpan(
        text: CharSequence,
        sb: SpannableStringBuilder, spanFactory: SpanFactory,
        flags: Long,
        pattern: Pattern,
        vararg extraPatterns: Pattern
    ): CharSequence {
        // specific patterns
        for (xpattern in extraPatterns) {
            val xmatcher = xpattern.matcher(text)
            while (xmatcher.find()) {
                val n = xmatcher.groupCount()
                if (n == 3) {
                    val headTag = xmatcher.group(1)!!
                    val tailTag = xmatcher.group(3)!!

                    // start
                    val startSpans = spanFactory.makeSpans(headTag, flags or SpanPosition.TAG1.flags().toLong())
                    val tag1Start = xmatcher.start(1) - 1
                    val tag1End = xmatcher.end(1) + 1
                    setSpan(sb, tag1Start, tag1End, startSpans)

                    // middle
                    val textSpans = spanFactory.makeSpans(tailTag, flags or SpanPosition.TEXT.flags().toLong())
                    val textStart = xmatcher.start(2)
                    val textEnd = xmatcher.end(2)
                    setSpan(sb, textStart, textEnd, textSpans)

                    // end
                    val endSpans = spanFactory.makeSpans(tailTag, flags or SpanPosition.TAG2.flags().toLong())
                    val tag2Start = xmatcher.start(3) - 2
                    val tag2End = xmatcher.end(3) + 1
                    setSpan(sb, tag2Start, tag2End, endSpans)
                    sb.replace(xmatcher.start(1), xmatcher.start(1), "")
                }
            }
        }

        // general pattern
        val matcher = pattern.matcher(text)
        while (matcher.find()) {
            val n = matcher.groupCount()
            if (n == 3) {
                val headTag = matcher.group(1)!!
                val tailTag = matcher.group(3)!!

                // <tag>
                val startSpans = spanFactory.makeSpans(headTag, flags or SpanPosition.TAG1.flags().toLong())
                val tag1Start = matcher.start(1) - 1
                val tag1End = matcher.end(1) + 1
                setSpan(sb, tag1Start, tag1End, startSpans)

                // text
                val textSpans = spanFactory.makeSpans(tailTag, flags or SpanPosition.TEXT.flags().toLong())
                val textStart = matcher.start(2)
                val textEnd = matcher.end(2)
                setSpan(sb, textStart, textEnd, textSpans)

                // </tag>
                val endSpans = spanFactory.makeSpans(tailTag, flags or SpanPosition.TAG2.flags().toLong())
                val tag2Start = matcher.start(3) - 2
                val tag2End = matcher.end(3) + 1
                setSpan(sb, tag2Start, tag2End, endSpans)
            }
        }
        return sb
    }

    /**
     * Apply spans
     *
     * @param selector  factory selector
     * @param sb        spannable string builder
     * @param i         from
     * @param j         to
     * @param flags     flags
     * @param factories span factories
     */
    private fun setSpan(selector: String, sb: SpannableStringBuilder, i: Int, j: Int, flags: Long, vararg factories: SpanFactory) {
        val spans = Array(factories.size) {
            factories[it].makeSpans(selector, flags)
        }
        setSpan(sb, i, j, spans)
    }

    /**
     * Span factory
     *
     * @author [Bernard Bou](mailto:1313ou@gmail.com)
     */
    fun interface SpanFactory {

        fun makeSpans(selector: String, flags: Long): Span
    }

    /**
     * Span position
     *
     * @author [Bernard Bou](mailto:1313ou@gmail.com)
     */
    enum class SpanPosition {

        TAG1,
        TEXT,
        TAG2;

        /**
         * Make flags
         *
         * @return flags
         */
        fun flags(): Int {
            return when (this) {
                TAG1 -> 1
                TAG2 -> 2
                TEXT -> 3
            }
        }

        companion object {

            /**
             * Make position from flags
             *
             * @param flags flags
             * @return position
             */
            @JvmStatic
            fun valueFrom(flags: Long): SpanPosition? {
                when ((flags and 3L).toInt()) {
                    1 -> return TAG1
                    2 -> return TAG2
                    3 -> return TEXT
                    else -> {}
                }
                return null
            }
        }
    }
}
