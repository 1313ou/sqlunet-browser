/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.style

import android.graphics.Color
import android.graphics.Typeface
import android.text.style.BackgroundColorSpan
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.annotation.ColorInt
import org.sqlunet.style.Spanner.HiddenSpan

/**
 * Span factories
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Factories {

    val classFactory = Spanner.SpanFactory { _: Long -> spans(Colors.classBackColor, Colors.classForeColor, StyleSpan(Typeface.BOLD)) }

    val roleFactory = Spanner.SpanFactory { _: Long -> spans(Colors.roleBackColor, Colors.roleForeColor, StyleSpan(Typeface.BOLD)) }

    val memberFactory = Spanner.SpanFactory { _: Long -> spans(Colors.memberBackColor, Colors.memberForeColor, StyleSpan(Typeface.BOLD)) }

    val dataFactory = Spanner.SpanFactory { _: Long -> spans(Colors.dataBackColor, Colors.dataForeColor, StyleSpan(Typeface.ITALIC)) }

    val definitionFactory = Spanner.SpanFactory { _: Long -> spans(Colors.definitionBackColor, Colors.definitionForeColor, StyleSpan(Typeface.ITALIC)) }

    val exampleFactory = Spanner.SpanFactory { _: Long -> spans(Colors.exampleBackColor, Colors.exampleForeColor, StyleSpan(Typeface.ITALIC)) }

    val relationFactory = Spanner.SpanFactory { _: Long -> spans(Colors.relationBackColor, Colors.relationForeColor, StyleSpan(Typeface.ITALIC)) }

    val wordFactory = Spanner.SpanFactory { _: Long -> spans(Colors.wordBackColor, Colors.wordForeColor, StyleSpan(Typeface.BOLD)) }

    val casedFactory = Spanner.SpanFactory { _: Long -> spans(Colors.casedBackColor, Colors.casedForeColor, StyleSpan(Typeface.BOLD)) }

    val pronunciationFactory = Spanner.SpanFactory { _: Long -> spans(Colors.pronunciationBackColor, Colors.pronunciationForeColor) }

    val posFactory = Spanner.SpanFactory { _: Long -> spans(Colors.posBackColor, Colors.posForeColor, StyleSpan(Typeface.ITALIC)) }

    val boldFactory = Spanner.SpanFactory { _: Long -> StyleSpan(Typeface.BOLD) }

    val italicFactory = Spanner.SpanFactory { _: Long -> StyleSpan(Typeface.ITALIC) }

    val hiddenFactory = Spanner.SpanFactory { _: Long -> HiddenSpan() }

    /**
     * Build spans
     *
     * @param bg         background color (il TRANSPARENT skipped)
     * @param fg         foreground color (il TRANSPARENT skipped)
     * @param otherSpans other spans
     * @return spans
     */
    fun spans(@ColorInt bg: Int, @ColorInt fg: Int, vararg otherSpans: CharacterStyle): Any {
        if (bg == Color.TRANSPARENT && fg == Color.TRANSPARENT) {
            return otherSpans
        }
        val spans: MutableList<in CharacterStyle> = ArrayList()
        if (bg != Color.TRANSPARENT) {
            spans.add(BackgroundColorSpan(bg))
        }
        if (fg != Color.TRANSPARENT) {
            spans.add(ForegroundColorSpan(fg))
        }
        spans.addAll(otherSpans)
        return spans
    }
}
