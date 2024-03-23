/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.style

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import org.sqlunet.style.MarkupSpanner.setSpan
import org.sqlunet.style.Spanner
import org.sqlunet.style.Spanner.Companion.setSpan
import java.util.regex.Pattern

/**
 * FrameNet spanner
 *
 * @param context context
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FrameNetSpanner(context: Context) {

    /**
     * Span factory
     */
    private val factory: FrameNetMarkupFactory

    init {
        factory = FrameNetMarkupFactory(context)
    }

    /**
     * Process
     *
     * @param text  text to process
     * @param flags flags
     * @return processed text
     */
    fun process(text: CharSequence, flags: Long, factory: Spanner.SpanFactory?): CharSequence {
        val sb = SpannableStringBuilder(text)
        if (factory != null) {
            setSpan(sb, 0, sb.length, 0, factory)
        }
        setSpan(text, sb, this.factory, flags, pattern, pattern1)
        return sb
    }

    /**
     * Add span
     *
     * @param sb       spannable string builder
     * @param start    start
     * @param end      end
     * @param selector selector guide
     * @param flags    flags
     */
    fun addSpan(sb: SpannableStringBuilder, start: Int, end: Int, selector: String, flags: Long) {
        val spans = factory.makeSpans(selector, flags)
        if (spans != null) {
            when (spans) {
                is Array<*> -> {
                    for (span in spans) {
                        sb.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }

                is Collection<*> -> {
                    for (span2 in spans) {
                        sb.setSpan(span2, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }

                else -> {
                    sb.setSpan(spans, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
    }

    companion object {
        private val pattern = Pattern.compile("<([^>]*)>([^<]*)</([^>]*)>")
        private val pattern1 = Pattern.compile("<(ex)>(.*)</(ex)>")
    }
}
