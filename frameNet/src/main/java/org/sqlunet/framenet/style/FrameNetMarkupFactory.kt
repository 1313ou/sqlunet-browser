/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.style

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import org.sqlunet.framenet.R
import org.sqlunet.style.Factories.spans
import org.sqlunet.style.MarkupSpanner
import org.sqlunet.style.MarkupSpanner.SpanPosition
import org.sqlunet.style.MarkupSpanner.SpanPosition.Companion.valueFrom
import org.sqlunet.style.Spanner.Companion.getDrawable
import org.sqlunet.style.Spanner.HiddenSpan

/**
 * FrameNet markup factory
 *
 * @param context context
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FrameNetMarkupFactory internal constructor(context: Context) : MarkupSpanner.SpanFactory {

    /**
     * Top role drawable
     */
    private val role2Drawable: Drawable

    /**
     * Role drawable
     */
    private val roleDrawable: Drawable

    /**
     * Relation drawable
     */
    private val relationDrawable: Drawable

    /**
     * Sample drawable
     */
    private val sampleDrawable: Drawable

    init {
        roleDrawable = getDrawable(context, R.drawable.role)
        role2Drawable = getDrawable(context, R.drawable.role1)
        relationDrawable = getDrawable(context, R.drawable.relation)
        sampleDrawable = getDrawable(context, R.drawable.sample)
    }

    /**
     * Make spans
     *
     * @param selector selector guide
     * @param flags    flags
     * @return spans
     */
    override fun makeSpans(selector: String, flags: Long): Any {
        val position = valueFrom(flags) ?: throw IllegalArgumentException()
        return when (position) {
            SpanPosition.TAG1 -> {
                when {
                    "t" == selector -> return ImageSpan(relationDrawable, DynamicDrawableSpan.ALIGN_BASELINE)
                    "fen" == selector -> return ImageSpan(roleDrawable, DynamicDrawableSpan.ALIGN_BASELINE)
                    "fe" == selector -> return ImageSpan(role2Drawable, DynamicDrawableSpan.ALIGN_BASELINE)
                    selector.matches("fex.*".toRegex()) -> return ImageSpan(role2Drawable, DynamicDrawableSpan.ALIGN_BASELINE)
                    selector.matches("xfen".toRegex()) -> return HiddenSpan()
                    selector.matches("ex".toRegex()) -> return ImageSpan(sampleDrawable, DynamicDrawableSpan.ALIGN_BASELINE)
                    selector.matches("x".toRegex()) -> return HiddenSpan()
                    else -> return spans(Colors.tag1BackColor, Colors.tag1ForeColor)
                }
            }

            SpanPosition.TAG2 -> {
                when (selector) {
                    "t", "x", "ex", "fex", "xfen", "fen", "fe" -> return HiddenSpan()
                }
                spans(Colors.tag2BackColor, Colors.tag2ForeColor)
            }

            SpanPosition.TEXT -> {
                textFactory.makeSpans(selector, flags)
            }
        }
    }

    companion object {

        /**
         * Flag value : fe definition
         */
        const val FEDEF = 0x10000

        /**
         * Text factory
         */
        private val textFactory: MarkupSpanner.SpanFactory = MarkupSpanner.SpanFactory { selector: String, flags: Long ->
            return@SpanFactory when {
                selector == "fe" -> spans(Colors.feBackColor, Colors.feForeColor)
                selector == "t" -> spans(Colors.tBackColor, Colors.tForeColor) // target
                selector == "fen" -> if (flags and FEDEF.toLong() != 0L) spans(Colors.fenWithinDefBackColor, Colors.fenWithinDefForeColor) else spans(Colors.fenBackColor, Colors.fenForeColor)
                selector == "xfen" -> spans(Colors.xfenBackColor, Colors.xfenForeColor)
                selector == "ex" -> spans(Colors.exBackColor, Colors.exForeColor, StyleSpan(Typeface.ITALIC))
                selector == "x" -> spans(Colors.xBackColor, Colors.xForeColor, StyleSpan(Typeface.BOLD))
                selector.matches("fex.*".toRegex()) -> if (flags and FEDEF.toLong() == 0L) spans(Colors.fexWithinDefBackColor, Colors.fexWithinDefForeColor, UnderlineSpan()) else spans(Colors.fexBackColor, Colors.fexForeColor)
                else -> {
                    throw IllegalArgumentException(selector)
                }
            }
        }
    }
}