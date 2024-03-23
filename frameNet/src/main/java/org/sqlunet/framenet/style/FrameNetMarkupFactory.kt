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
import org.sqlunet.style.MarkupSpanner.SpanPosition.Companion.valueOf
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
    override fun makeSpans(selector: String, flags: Long): Any? {
        val position = valueOf(flags)
        return if (position != null) {
            when (position) {
                SpanPosition.TAG1 -> {
                    if ("t" == selector) {
                        return ImageSpan(relationDrawable, DynamicDrawableSpan.ALIGN_BASELINE)
                    }
                    if ("fen" == selector) {
                        return ImageSpan(roleDrawable, DynamicDrawableSpan.ALIGN_BASELINE)
                    }
                    if ("fe" == selector) {
                        return ImageSpan(role2Drawable, DynamicDrawableSpan.ALIGN_BASELINE)
                    }
                    if (selector.matches("fex.*".toRegex())) {
                        return ImageSpan(role2Drawable, DynamicDrawableSpan.ALIGN_BASELINE)
                    }
                    if (selector.matches("xfen".toRegex())) {
                        return HiddenSpan()
                    }
                    if (selector.matches("ex".toRegex())) {
                        return ImageSpan(sampleDrawable, DynamicDrawableSpan.ALIGN_BASELINE)
                    }
                    if (selector.matches("x".toRegex())) {
                        HiddenSpan()
                    } else spans(Colors.tag1BackColor, Colors.tag1ForeColor)
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
        } else null
    }

    companion object {

        /**
         * Flag value : fe definition
         */
        const val FEDEF = 0x10000

        /**
         * Text factory
         */
        private val textFactory: MarkupSpanner.SpanFactory = MarkupSpanner.SpanFactory { selector: String?, flags: Long ->
            if ("fe" == selector) {
                return@SpanFactory spans(Colors.feBackColor, Colors.feForeColor)
            }
            if ("t" == selector) // target
            {
                return@SpanFactory spans(Colors.tBackColor, Colors.tForeColor)
            }
            if ("fen" == selector) {
                if (flags and FEDEF.toLong() != 0L) {
                    return@SpanFactory spans(Colors.fenWithinDefBackColor, Colors.fenWithinDefForeColor)
                } else {
                    return@SpanFactory spans(Colors.fenBackColor, Colors.fenForeColor)
                }
            }
            if (selector!!.matches("fex.*".toRegex())) {
                if (flags and FEDEF.toLong() == 0L) {
                    return@SpanFactory spans(Colors.fexWithinDefBackColor, Colors.fexWithinDefForeColor, UnderlineSpan())
                } else {
                    return@SpanFactory spans(Colors.fexBackColor, Colors.fexForeColor)
                }
            }
            if ("xfen" == selector) {
                return@SpanFactory spans(Colors.xfenBackColor, Colors.xfenForeColor)
            }
            if ("ex" == selector) {
                return@SpanFactory spans(Colors.exBackColor, Colors.exForeColor, StyleSpan(Typeface.ITALIC))
            }
            if ("x" == selector) {
                return@SpanFactory spans(Colors.xBackColor, Colors.xForeColor, StyleSpan(Typeface.BOLD))
            }
            null
        }
    }
}